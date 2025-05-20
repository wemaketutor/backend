package com.tutoras.tutoras.service;

import com.tutoras.tutoras.entity.SourceEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
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
        String fileName = "document_" + timestamp + "_" + UUID.randomUUID().toString().substring(0, 8) + ".txt";
        
        log.info("Начинаю генерацию файла: {}", fileName);
        log.info("Использую директорию для загрузки: {}", uploadDir);
        
        String realPath = uploadDir.replace("file:", "");
        log.info("Реальный путь без префикса file:: {}", realPath);
        
        Path pdfDirPath = Paths.get(realPath, PDF_DIRECTORY);
        try {
            log.info("Проверяю/создаю директорию: {}", pdfDirPath.toAbsolutePath());
            Files.createDirectories(pdfDirPath);
            log.info("Директория создана/существует: {}", pdfDirPath.toAbsolutePath());
        } catch (IOException e) {
            log.error("Ошибка при создании директории для файлов: {}", e.getMessage(), e);
            return null;
        }
        
        Path filePath = pdfDirPath.resolve(fileName);
        log.info("Полный путь к создаваемому файлу: {}", filePath.toAbsolutePath());
        
        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            writer.write(source.getTitle() + "\n\n");
            
            if (source.getDescription() != null && !source.getDescription().isEmpty()) {
                writer.write(source.getDescription() + "\n\n");
            }
            
            if (source.getBody() != null) {
                writer.write(source.getBody());
            }
            
            log.info("Файл успешно создан: {}", filePath.toAbsolutePath());
            
            if (Files.exists(filePath)) {
                log.info("Подтверждено существование файла: {} (размер: {} байт)", 
                    filePath.toAbsolutePath(), Files.size(filePath));
            } else {
                log.error("Файл не найден после создания: {}", filePath.toAbsolutePath());
            }
            
            String fileUrl = PDF_DIRECTORY + fileName;
            log.info("Возвращаю URL файла: {}", fileUrl);
            return fileUrl;
        } catch (IOException e) {
            log.error("Ошибка при создании файла: {}", e.getMessage(), e);
            return null;
        }
    }
}