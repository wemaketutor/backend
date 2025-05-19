package com.tutoras.tutoras.repository;

import org.springframework.stereotype.Repository;
import com.tutoras.tutoras.entity.LessonStudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface LessonStudentRepository extends JpaRepository<LessonStudentEntity, Long>{}
