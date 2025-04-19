package com.tutoras.tutoras.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tutoras.tutoras.entity.HomeworkEntity;
import com.tutoras.tutoras.entity.HomeworkMaterialEntity;
import com.tutoras.tutoras.entity.MaterialEntity;

@Repository
public interface HomeworkMaterialRepository extends JpaRepository<HomeworkMaterialEntity, Long> {
    List<HomeworkMaterialEntity> findByHomework(HomeworkEntity homework);
    List<HomeworkMaterialEntity> findByMaterial(MaterialEntity material);
    void deleteByHomeworkAndMaterial(HomeworkEntity homework, MaterialEntity material);
} 