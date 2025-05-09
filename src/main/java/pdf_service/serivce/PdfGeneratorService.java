package pdf_service.serivce;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pdf_service.entity.SourceEntity;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.*;

@Service
public class PdfGeneratorService {
    private final String TEMPLATE_DIR = "latex_templates/";//CHECK!!!!!!!! я хз какой тут фр адрес папки с темплейтами ставить, и сойдет ли так
    private final MinioService minioService;
    private final Path tempDir;

    public PdfGeneratorService(MinioService minioService) {
        this.minioService = minioService;
        //organizing the temp directory
        this.tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "latex");
        Files.createDirectories(tempDir);

        copyTemplateToTemp("main.tex", tempDir);
        copyTemplateToTemp("style.tex", tempDir);
    }

    public String generatePdf(SourseEntity source) throws IOException {
        Path resFile = tempDir.resolve("res.tex");
        Files.write(source.getBody().getBytes());

        // compiling
        Path mainTex = tempDir.resolve("main.tex");
        compileLatex(tempDir.toString(), mainTex.toString());

        // update to minio
        String pdfName = "material_" + UUID.randomUUID() + ".pdf";
        Path pdfFile = tempDir.resolve("main.pdf");
        minioService.uploadFile("materials", pdfName, pdfFile.toString());

        Files.deleteIfExists(resFile);
        Files.deleteIfExists(pdfFile);

        return minioService.getFileUrl("materials", pdfName);
    }

    public SourceEntity generateCombinedSource(ArrayList<SourceEntity> sources) {
        StringBuilder combinedBody = new StringBuilder();
        String title = "Combined Material";
        String description = "Combined from multiple sources";
        
        for (SourceEntity source : sources) {
            combinedBody.append(source.getBody()).append("\n\n");
        }
        
        return new SourceEntity(null, title, description, combinedBody.toString());
    }

    private void copyTemplateToTemp(String templateName, Path tempDir) throws IOException {
        ClassPathResource resource = new ClassPathResource(TEMPLATE_DIR + templateName);
        Path target = tempDir.resolve(templateName);
        Files.copy(resource.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
    }

    private void compileLatex(String workingDir, String texFile) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                "pdflatex",
                "-interaction=nonstopmode",
                "-output-directory=" + workingDir,
                texFile
        );
        pb.directory(new File(workingDir));
        Process process = pb.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) throw new IOException("LaTeX compilation failed");
    }
}