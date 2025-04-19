package com.tutoras.tutoras.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tutoras.tutoras.entity.HomeworkEntity;
import com.tutoras.tutoras.entity.StudentEntity;
import com.tutoras.tutoras.entity.HomeworkEntity.HomeworkStatus;

@Repository
public interface HomeworkRepository extends JpaRepository<HomeworkEntity, Long> {
    List<HomeworkEntity> findByStudent(StudentEntity student);
    List<HomeworkEntity> findByStudentAndStatus(StudentEntity student, HomeworkStatus status);
    List<HomeworkEntity> findByStatus(HomeworkStatus status);
} 