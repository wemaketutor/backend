package com.tutoras.tutoras.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tutoras.tutoras.entity.CommentEntity;
import com.tutoras.tutoras.entity.UserEntity;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByUser(UserEntity user);
} 