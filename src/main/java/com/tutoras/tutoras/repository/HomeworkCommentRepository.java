package com.tutoras.tutoras.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tutoras.tutoras.entity.CommentEntity;
import com.tutoras.tutoras.entity.HomeworkCommentEntity;
import com.tutoras.tutoras.entity.HomeworkEntity;

@Repository
public interface HomeworkCommentRepository extends JpaRepository<HomeworkCommentEntity, Long> {
    List<HomeworkCommentEntity> findByHomework(HomeworkEntity homework);
    List<HomeworkCommentEntity> findByComment(CommentEntity comment);
    void deleteByHomeworkAndComment(HomeworkEntity homework, CommentEntity comment);
}