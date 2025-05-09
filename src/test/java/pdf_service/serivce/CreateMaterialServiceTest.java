package pdf_service.serivce;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pdf_service.entity.MaterialEntity;
import pdf_service.entity.SourceEntity;
import pdf_service.model.CreateMaterialRequest;
import pdf_service.repository.MaterialRepository;
import pdf_service.repository.SourceRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateMaterialServiceTest {

    @Mock
    private SourceRepository sourceRepository;

    @Mock
    private MaterialRepository materialRepository;

    @Mock
    private PdfGeneratorService pdfGeneratorService;

    @InjectMocks
    private CreateMaterialService createMaterialService;

    @Test
    void createMaterial_ThrowsWhenNoSources() {
        CreateMaterialRequest request = CreateMaterialRequest.builder()
                .sources_id(List.of(1L, 2L))
                .build();

        when(sourceRepository.getAllByIds(any())).thenReturn(new ArrayList<>());

        assertThrows(IllegalArgumentException.class, () ->
                createMaterialService.createMaterial(1L, request));

        verify(sourceRepository).getAllByIds(request.getSources_id());
    }

    @Test
    void createMaterial_Success() {
        // Arrange
        Long teacherId = 1L;
        List<Long> sourceIds = List.of(1L, 2L);
        CreateMaterialRequest request = CreateMaterialRequest.builder()
                .sources_id(sourceIds)
                .build();

        SourceEntity source1 = new SourceEntity(1L, "Title1", "desc1", "Content1");
        SourceEntity source2 = new SourceEntity(2L, "Title2", "desc2", "Content2");
        List<SourceEntity> sources = Arrays.asList(source1, source2);

        SourceEntity combinedSource = new SourceEntity(1L, "Combined", "combined desc", "Combined Content");
        String pdfUrl = "http://example.com/material.pdf";
        MaterialEntity savedMaterial = new MaterialEntity(combinedSource, teacherId, pdfUrl);

        when(sourceRepository.getAllByIds(sourceIds)).thenReturn(sources);
        when(pdfGeneratorService.generateCombinedSource(sources)).thenReturn(combinedSource);
        when(pdfGeneratorService.generatePdf(combinedSource)).thenReturn(pdfUrl);
        when(materialRepository.save(any())).thenReturn(savedMaterial);

        Long result = createMaterialService.createMaterial(teacherId, request);

        verify(sourceRepository).getAllByIds(sourceIds);
        verify(pdfGeneratorService).generateCombinedSource(sources);
        verify(pdfGeneratorService).generatePdf(combinedSource);
        verify(materialRepository).save(any(MaterialEntity.class));
    }
}