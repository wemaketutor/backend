package com.tutoras.tutoras.service;

import com.tutoras.tutoras.entity.SourceEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class PdfGeneratorService {

    @Value("${spring.web.resources.static-locations[0]}")
    private String uploadDir;

    private static final String PDF_DIRECTORY = "files/pdf/";

    public SourceEntity generateCombinedSource(List<SourceEntity> sources) {
        if (sources == null || sources.isEmpty()) {
            log.info("Создаю пустой документ, так как список источников пуст");
            return new SourceEntity(1L, "Пустой документ", "Автоматически сгенерированный документ", "");
        }

        StringBuilder combinedBody = new StringBuilder();
        String title = sources.get(0).getTitle();
        String description = sources.get(0).getDescription();

        log.info("Объединяю {} источников в один документ с заголовком '{}'", sources.size(), title);

        for (SourceEntity source : sources) {
            combinedBody.append("# ").append(source.getTitle()).append("\n\n");
            combinedBody.append(source.getBody()).append("\n\n");
            combinedBody.append("----------\n\n");
        }

        return new SourceEntity(1L, title, description, combinedBody.toString());
    }

    public String generatePdf(SourceEntity source) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "document_" + timestamp + "_" + UUID.randomUUID().toString().substring(0, 8) + ".pdf";

        String realPath = uploadDir.replace("file:", "");
        Path pdfDirPath = Paths.get(realPath, PDF_DIRECTORY);

        try {
            Files.createDirectories(pdfDirPath);
        } catch (IOException e) {
            log.error("Ошибка при создании директории для файлов: {}", e.getMessage(), e);
            return null;
        }

        Path filePath = pdfDirPath.resolve(fileName);

        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        
        try (InputStream fontStream = getClass().getResourceAsStream("/fonts/LiberationSans-Regular.ttf")) {
            if (fontStream == null) {
                log.error("Файл шрифта не найден: /fonts/LiberationSans-Regular.ttf");
                document.close();
                return null;
            }
            PDType0Font font = PDType0Font.load(document, fontStream);
        
            // Заголовок и описание можно вывести в одном contentStream
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(font, 16);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText(source.getTitle());
                contentStream.endText();
        
                if (source.getDescription() != null && !source.getDescription().isEmpty()) {
                    contentStream.beginText();
                    contentStream.setFont(font, 12);
                    contentStream.newLineAtOffset(50, 720);
                    contentStream.showText(source.getDescription());
                    contentStream.endText();
                }
            }
        
            // Тело документа с переносом строк и страниц
            String[] lines = source.getBody() != null ? source.getBody().split("\n") : new String[0];
            int yPosition = 700;
        
            PDPageContentStream contentStream = null;
            try {
                contentStream = new PDPageContentStream(document, page);
                contentStream.beginText();
                contentStream.setFont(font, 12);
                contentStream.newLineAtOffset(50, yPosition);
        
                for (String line : lines) {
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -15);
                    yPosition -= 15;
        
                    if (yPosition < 50) {
                        contentStream.endText();
                        contentStream.close();
        
                        // Новая страница
                        page = new PDPage();
                        document.addPage(page);
        
                        contentStream = new PDPageContentStream(document, page);
                        contentStream.beginText();
                        contentStream.setFont(font, 12);
                        contentStream.newLineAtOffset(50, 750);
                        yPosition = 750;
                    }
                }
                contentStream.endText();
            } finally {
                if (contentStream != null) {
                    contentStream.close();
                }
            }
        
            document.save(filePath.toFile());
            log.info("PDF успешно создан: {}", filePath.toAbsolutePath());
            return PDF_DIRECTORY + fileName;
        
        } catch (Exception e) {
            log.error("Ошибка при создании PDF файла: {}", e.getMessage(), e);
            return null;
        } finally {
            try {
                document.close();
            } catch (IOException e) {
                log.error("Ошибка при закрытии документа: {}", e.getMessage(), e);
            }
        }
    }
}
