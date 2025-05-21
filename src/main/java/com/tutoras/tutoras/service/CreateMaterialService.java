package com.tutoras.tutoras.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.tutoras.tutoras.entity.MaterialEntity;
import com.tutoras.tutoras.entity.SourceEntity;
import com.tutoras.tutoras.entity.TeacherEntity;
import com.tutoras.tutoras.model.CreateMaterialRequest;
import com.tutoras.tutoras.repository.MaterialRepository;
import com.tutoras.tutoras.repository.SourceRepository;
import com.tutoras.tutoras.repository.TeacherRepository;

import jakarta.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CreateMaterialService {
    private final SourceRepository sourceRepository;
    private final MaterialRepository materialRepository;
    private final TeacherRepository teacherRepository;
    private final PdfGeneratorService pdfGeneratorService;

    public Long createMaterial(Long teacherId, CreateMaterialRequest request) {
        TeacherEntity teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + teacherId));

        List<SourceEntity> sources = new ArrayList<>();
        if (request.getSources_id() != null && !request.getSources_id().isEmpty()) {
            sources = sourceRepository.getAllByIds(request.getSources_id());
            if (sources.isEmpty()) {
                log.warn("No sources found with provided IDs: {}", request.getSources_id());
                
                SourceEntity randomSource = sourceRepository.findRandom();
                if (randomSource != null) {
                    log.info("Используем случайный источник из БД: {}", randomSource.getId());
                    sources.add(randomSource);
                } else {
                    log.info("Создаем пустой источник, так как случайный источник не найден");
                    sources.add(new SourceEntity(1L, "Пустой документ", 
                        "Автоматически сгенерированный документ", "Содержимое документа не указано"));
                }
            }
        } else {

            SourceEntity randomSource = sourceRepository.findRandom();
            if (randomSource != null) {
                log.info("Используем случайный источник из БД: {}", randomSource.getId());
                sources.add(randomSource);
            } else {
                log.info("Создаем пустой источник, так как источники не указаны и случайный источник не найден");
                sources.add(new SourceEntity(1L, "Пустой документ", 
                    "Автоматически сгенерированный документ", "Содержимое документа не указано"));
            }
        }

        SourceEntity combinedSource = pdfGeneratorService.generateCombinedSource(sources);
        
        String materialUrl = pdfGeneratorService.generatePdfWithLatex(combinedSource);
        if (materialUrl == null) {
            log.error("Failed to generate PDF for material: {}", combinedSource.getTitle());
            materialUrl = "";
        }

        MaterialEntity material = new MaterialEntity(
                combinedSource.getTitle(),
                combinedSource.getDescription(),
                materialUrl,
                true,
                teacher
        );

        MaterialEntity savedMaterial = materialRepository.save(material);
        log.info("Created new material with ID: {}", savedMaterial.getId());

        return savedMaterial.getId();
    }
}
