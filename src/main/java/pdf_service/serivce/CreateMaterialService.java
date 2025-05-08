package pdf_service.serivce;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pdf_service.entity.MaterialEntity;
import pdf_service.entity.SourceEntity;
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
    private final AuthenticationManager authenticationManager;

    public Long createMaterial(Long teacherId, CreateMaterialRequest request) {

        ArrayList<SourceEntity> sources = materialRepository.getAllByIds(request.getSources_id());
        if (sources.isEmpty()) {
            throw new IllegalArgumentException("No materials found with provided IDs");
        }

        SourceEntity combinedSource = pdfGeneratorService.generateCombinedSource(sources);
        String materialUrl = pdfGeneratorService.generatePdf(combinedSource);

        MaterialEntity combinedMaterial = new MaterialEntity(combinedSource, teacherId, materialUrl);

        return materialRepository.save(combinedMaterial).getId();
    }
}
