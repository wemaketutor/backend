package com.tutoras.tutoras.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tutoras.tutoras.entity.LessonStudentEntity;

@Repository
public interface LessonStudentRepository extends JpaRepository<LessonStudentEntity, Long> {
}
