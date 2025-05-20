package com.tutoras.tutoras.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tutoras.tutoras.entity.LessonEntity;
import com.tutoras.tutoras.entity.LessonStudentEntity;
import com.tutoras.tutoras.entity.StudentEntity;

@Repository
public interface LessonStudentRepository extends JpaRepository<LessonStudentEntity, Long> {
    List<LessonStudentEntity> findByLesson(LessonEntity lesson);
    List<LessonStudentEntity> findByStudent(StudentEntity student);
    void deleteByLessonAndStudent(LessonEntity lesson, StudentEntity student);
    void deleteByLessonId(Long lessonId);
    boolean existsByLessonIdAndStudentId(Long lessonId, Long studentId);
} 