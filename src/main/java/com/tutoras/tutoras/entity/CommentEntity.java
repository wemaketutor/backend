package com.tutoras.tutoras.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "comments")
public class CommentEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @SuppressWarnings("unused")
    private CommentEntity() {}
    
    public CommentEntity(String body, UserEntity user) {
        this.body = body;
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
} 