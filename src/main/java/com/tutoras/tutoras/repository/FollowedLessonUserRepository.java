package com.tutoras.tutoras.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tutoras.tutoras.entity.EventEntity;
import com.tutoras.tutoras.entity.FollowedLessonUserEntity;
import com.tutoras.tutoras.entity.StudentEntity;

@Repository
public interface FollowedLessonUserRepository extends JpaRepository<FollowedLessonUserEntity, Long> {
    List<FollowedLessonUserEntity> findByLesson(EventEntity lesson);
    List<FollowedLessonUserEntity> findByStudent(StudentEntity student);
    void deleteByLessonAndStudent(EventEntity lesson, StudentEntity student);
} 