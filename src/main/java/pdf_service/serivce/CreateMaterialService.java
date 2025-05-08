package pdf_service.serivce;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pdf_service.entity.MaterialEntity;
import pdf_service.StorageService;
import pdf_service.model.CreateMaterialRequest;
import pdf_service.repository.CreateMaterialRepository;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CreateMaterialService {
    private final CreateMaterialRepository materialRepository;
    private final PdfGeneratorService pdfGeneratorService;
    private final StorageService storageService;

    public Long createMaterial(CreateMaterialRequest request) {
        ArrayList<MaterialEntity> materials = materialRepository.getAllByIds(request.getMaterials_id());
        if (materials.isEmpty()) {
            throw new IllegalArgumentException("No materials found with provided IDs");
        }

        ArrayList<MaterialFiles> materialFiles = storageService.getMaterialFiles(materials);
        String combinedMaterialUrl = pdfGeneratorService.generateCombinedPdf(materialFiles);
        MaterialEntity combinedMaterial(); // передаем параметры
        return materialRepository.save(combinedMaterial);
    }
}
