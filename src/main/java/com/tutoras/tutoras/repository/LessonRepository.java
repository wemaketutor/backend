package com.tutoras.tutoras.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tutoras.tutoras.entity.LessonEntity;

@Repository
public interface LessonRepository extends JpaRepository<LessonEntity, Long>{}
