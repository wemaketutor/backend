package com.tutoras.tutoras.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tutoras.tutoras.entity.MaterialEntity;
import com.tutoras.tutoras.entity.TeacherEntity;

@Repository
public interface MaterialRepository extends JpaRepository<MaterialEntity, Long> {
    List<MaterialEntity> findByTeacher(TeacherEntity teacher);
    List<MaterialEntity> findByIsPublic(Boolean isPublic);
    List<MaterialEntity> findByTeacherAndIsPublic(TeacherEntity teacher, Boolean isPublic);
} 