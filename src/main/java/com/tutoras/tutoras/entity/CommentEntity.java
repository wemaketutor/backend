package com.tutoras.tutoras.entity;

import java.time.OffsetDateTime;

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
    
    @Column(nullable = false)
    private String content;
    
    @ManyToOne
    @JoinColumn(name = "homework_id")
    private HomeworkEntity homework;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    
    private OffsetDateTime createdAt;
    
    private OffsetDateTime updatedAt;
    
    @SuppressWarnings("unused")
    private CommentEntity() {}
    
    public CommentEntity(String content, HomeworkEntity homework, UserEntity user) {
        this.content = content;
        this.homework = homework;
        this.user = user;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }
} 