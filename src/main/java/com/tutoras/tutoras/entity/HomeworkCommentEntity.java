package com.tutoras.tutoras.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "homeworks_comments")
public class HomeworkCommentEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "homework_id")
    private HomeworkEntity homework;
    
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private CommentEntity comment;
    
    @SuppressWarnings("unused")
    private HomeworkCommentEntity() {}
    
    public HomeworkCommentEntity(HomeworkEntity homework, CommentEntity comment) {
        this.homework = homework;
        this.comment = comment;
    }
} 