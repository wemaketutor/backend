package pdf_service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CreateMaterialService {
    private final MaterialRepository materialRepository;
    private final PdfGeneratorService pdfGeneratorService;
    private final StorageService storageService;

    public Long createMaterial(CreateMaterialRequest request) {
        List<MaterialEntity> materials = materialRepository.findAllById(request.getMaterials_id());
        if (materials.isEmpty()) {
            throw new IllegalArgumentException("No materials found with provided IDs");
        }

        validateMaterials(materials);

        String combinedTitle = "Combined Materials";
        StringBuilder combinedContent = new StringBuilder();

        for (MaterialEntity material : materials) {
            combinedContent.append("# ").append(material.getTitle()).append("\n\n");
            combinedContent.append(material.getDescription()).append("\n\n");
        }

        byte[] pdfContent = pdfGeneratorService.generatePdf(
                combinedTitle,
                combinedContent.toString()
        );

        MaterialEntity combinedMaterial = MaterialEntity.builder()
                .title(combinedTitle)
                .description("Combination of " + materials.size() + " materials")
                .fileUrl(storageService.storePdf(pdfContent))
                .isPublic(false)
                .teacher(materials.get(0).getTeacher())
                .build();

        MaterialEntity savedMaterial = materialRepository.save(combinedMaterial);
        log.info("Successfully created combined material with ID: {}", savedMaterial.getId());
        return savedMaterial.getId();
    }
}
