package pdf_service.serivce;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pdf_service.entity.MaterialEntity;
import pdf_service.entity.SourceEntity;
import pdf_service.model.CreateMaterialRequest;
import pdf_service.repository.CreateMaterialRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateMaterialServiceTest {

    @Mock
    private CreateMaterialRepository materialRepository;

    @Mock
    private PdfGeneratorService pdfGeneratorService;

    @InjectMocks
    private CreateMaterialService createMaterialService;

    @Test
    void createMaterial_ShouldThrowException_WhenNoSourcesFound() {
        CreateMaterialRequest request = new CreateMaterialRequest(new ArrayList<>(Arrays.asList(1L, 2L)));

         when(materialRepository.getAllByIds(any())).thenReturn(new ArrayList<>());

        assertThrows(IllegalArgumentException.class, () ->
                createMaterialService.createMaterial(1L, request));
    }

    @Test
    void createMaterial_ShouldReturnMaterialId_WhenSuccessful() {
        // Arrange
        Long teacherId = 1L;
        CreateMaterialRequest request = CreateMaterialRequest.builder()
                .sources_id(new ArrayList<>(Arrays.asList(1L, 2L)))
                .build();

        SourceEntity source1 = new SourceEntity(1L, "Title1", "Desc1", "Body1");
        SourceEntity source2 = new SourceEntity(2L, "Title2", "Desc2", "Body2");
        List<SourceEntity> sources = Arrays.asList(source1, source2);

        SourceEntity combinedSource = new SourceEntity(null, "Combined", "Combined Desc", "Combined Body");
        String materialUrl = "http://example.com/material.pdf";

        MaterialEntity savedMaterial = MaterialEntity.builder()
                .id(1L)
                .title(combinedSource.getTitle())
                .description(combinedSource.getDescription())
                .fileUrl(materialUrl)
                .teacherId(teacherId)
                .isPublic(false)
                .build();

        when(materialRepository.getAllByIds(any())).thenReturn(sources);
        when(pdfGeneratorService.generateCombinedSource(any())).thenReturn(combinedSource);
        when(pdfGeneratorService.generatePdf(any())).thenReturn(materialUrl);
        when(materialRepository.save(any())).thenReturn(savedMaterial);

        Long result = createMaterialService.createMaterial(teacherId, request);

        assertEquals(1L, result);
        verify(materialRepository, times(1)).save(any());
    }
}