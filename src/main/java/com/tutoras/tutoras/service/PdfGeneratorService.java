package com.tutoras.tutoras.service;

import com.tutoras.tutoras.entity.SourceEntity;
import com.tutoras.tutoras.repository.SourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final SourceRepository sourceRepository;

    @Autowired
    public PdfGeneratorService(SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

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

    private String generateLatexContent(SourceEntity source) {
        String bodyContent = source.getBody() == null ? "" : source.getBody();
        
        // Разбиваем body на параграфы и обрабатываем их с учетом форматирования
        String[] paragraphs = bodyContent.split("\n\n");
        StringBuilder formattedBody = new StringBuilder();
        
        for (String paragraph : paragraphs) {
            if (paragraph.trim().isEmpty()) {
                continue;
            }
            
            // Обработка заголовков (начинаются с # или ##)
            if (paragraph.trim().startsWith("# ")) {
                String heading = paragraph.trim().substring(2);
                formattedBody.append("\\section{").append(escapeLatex(heading)).append("}\n\n");
            } else if (paragraph.trim().startsWith("## ")) {
                String heading = paragraph.trim().substring(3);
                formattedBody.append("\\subsection{").append(escapeLatex(heading)).append("}\n\n");
            }
            // Обработка маркированных списков (начинаются с - или *)
            else if (paragraph.trim().startsWith("- ") || paragraph.trim().startsWith("* ")) {
                formattedBody.append("\\begin{itemize}\n");
                String[] lines = paragraph.split("\n");
                for (String line : lines) {
                    if (line.trim().startsWith("- ") || line.trim().startsWith("* ")) {
                        String item = line.trim().substring(2);
                        formattedBody.append("\\item ").append(escapeLatex(item)).append("\n");
                    }
                }
                formattedBody.append("\\end{itemize}\n\n");
            }
            // Обработка разделителей (-----)
            else if (paragraph.trim().matches("^-{3,}$")) {
                formattedBody.append("\\hrulefill\n\n");
            }
            // Обычный текст
            else {
                formattedBody.append(escapeLatex(paragraph.trim())).append("\n\n");
            }
        }
        
        String template = """
        \\documentclass[12pt]{article}
        \\usepackage[T2A]{fontenc}
        \\usepackage[utf8]{inputenc}
        \\usepackage[russian]{babel}
        \\usepackage{geometry}
        \\usepackage{hyperref}
        \\geometry{a4paper, margin=2cm}
        \\begin{document}
        \\begin{center}
        \\textbf{\\Large %s}\\\\[1em]
        %s
        \\end{center}
        
        %s
        \\end{document}
        """;
        
        String result = String.format(template,
            escapeLatex(source.getTitle()),
            escapeLatex(source.getDescription()),
            formattedBody.toString()
        );
        
        log.info("Сгенерирован LaTeX шаблон: {}", result.substring(0, Math.min(200, result.length())) + "...");
        return result;
    }
    
    /**
     * Транслитерация русского текста в латиницу 
     * Метод оставлен для возможного использования в будущем, но сейчас не используется
     */
    private String transliterateRussian(String text) {
        if (text == null) return "";
        
        return text
            .replace("а", "a").replace("б", "b").replace("в", "v").replace("г", "g")
            .replace("д", "d").replace("е", "e").replace("ё", "yo").replace("ж", "zh")
            .replace("з", "z").replace("и", "i").replace("й", "y").replace("к", "k")
            .replace("л", "l").replace("м", "m").replace("н", "n").replace("о", "o")
            .replace("п", "p").replace("р", "r").replace("с", "s").replace("т", "t")
            .replace("у", "u").replace("ф", "f").replace("х", "kh").replace("ц", "ts")
            .replace("ч", "ch").replace("ш", "sh").replace("щ", "sch").replace("ъ", "")
            .replace("ы", "y").replace("ь", "").replace("э", "e").replace("ю", "yu")
            .replace("я", "ya")
            .replace("А", "A").replace("Б", "B").replace("В", "V").replace("Г", "G")
            .replace("Д", "D").replace("Е", "E").replace("Ё", "Yo").replace("Ж", "Zh")
            .replace("З", "Z").replace("И", "I").replace("Й", "Y").replace("К", "K")
            .replace("Л", "L").replace("М", "M").replace("Н", "N").replace("О", "O")
            .replace("П", "P").replace("Р", "R").replace("С", "S").replace("Т", "T")
            .replace("У", "U").replace("Ф", "F").replace("Х", "Kh").replace("Ц", "Ts")
            .replace("Ч", "Ch").replace("Ш", "Sh").replace("Щ", "Sch").replace("Ъ", "")
            .replace("Ы", "Y").replace("Ь", "").replace("Э", "E").replace("Ю", "Yu")
            .replace("Я", "Ya");
    }

    private String escapeLatex(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\textbackslash{}")
                .replace("_", "\\_")
                .replace("&", "\\&")
                .replace("%", "\\%")
                .replace("$", "\\$")
                .replace("#", "\\#")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace("~", "\\textasciitilde{}")
                .replace("^", "\\textasciicircum{}");
    }

    public String generatePdfWithLatex(SourceEntity source) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String baseFileName = "document_" + timestamp + "_" + UUID.randomUUID().toString().substring(0, 8);
        String realPath = uploadDir.replace("file:", "");
        Path pdfDirPath = Paths.get(realPath, PDF_DIRECTORY);

        try {
            Files.createDirectories(pdfDirPath);
            Path texFile = pdfDirPath.resolve(baseFileName + ".tex");
            Path pdfFile = pdfDirPath.resolve(baseFileName + ".pdf");

            Files.writeString(texFile, generateLatexContent(source));

            // Компилируем через pdflatex с использованием shell-скрипта для дополнительной надежности
            String command = "cd " + pdfDirPath.toString() + " && /usr/bin/pdflatex -interaction=nonstopmode \"" + texFile.getFileName() + "\"";
            log.info("Выполняем команду: {}", command);
            
            ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", command);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            // Читаем вывод процесса для отладки
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()))) {
                String line;
                StringBuilder output = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                log.info("pdflatex output: {}", output.toString());
            }
            
            int exitCode = process.waitFor();
            log.info("pdflatex exit code: {}", exitCode);

            // Проверяем, создался ли PDF с точным указанием пути
            Path exactPdfFile = pdfDirPath.resolve(texFile.getFileName().toString().replace(".tex", ".pdf"));
            log.info("Проверяем PDF по пути: {}", exactPdfFile);
            
            if (Files.exists(exactPdfFile)) {
                log.info("PDF успешно создан!");
                // Можно удалить вспомогательные файлы .aux, .log, .tex
                Files.deleteIfExists(pdfDirPath.resolve(texFile.getFileName().toString().replace(".pdf", ".aux")));
                
                // Вернем относительный путь к PDF
                return PDF_DIRECTORY + exactPdfFile.getFileName().toString();
            } else {
                Path logFile = pdfDirPath.resolve(texFile.getFileName().toString().replace(".tex", ".log"));
                String logContent = "";
                if (Files.exists(logFile)) {
                    try {
                        logContent = Files.readString(logFile);
                        log.error("PDF не был создан. Содержимое лог-файла: {}", logContent);
                    } catch (Exception e) {
                        log.error("Ошибка при чтении лог-файла: {}", e.getMessage());
                    }
                }
                
                // Для отладки сохраняем .tex и .log файлы
                log.error("PDF не был создан. Tex-файл сохранен: {}, Log-файл сохранен: {}",
                          texFile, logFile);
                return null;
            }
        } catch (Exception e) {
            log.error("Ошибка при генерации PDF через LaTeX: {}", e.getMessage(), e);
            return null;
        }
    }

    public String generateRandomPdfWithLatex() {
        log.info("Вызов метода generateRandomPdfWithLatex");

        SourceEntity randomSource = sourceRepository.findRandom();
        log.info("Вызов метода after randomSource generateRandomPdfWithLatex", randomSource.getBody());
        if (randomSource == null) {
            log.warn("В базе нет ни одного источника для генерации PDF");
            return null;
        }
        log.info("Генерирую PDF для источника: {}", randomSource.getBody());
        return generatePdfWithLatex(randomSource);
    }
}
