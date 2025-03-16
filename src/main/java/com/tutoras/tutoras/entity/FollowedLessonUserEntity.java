package com.tutoras.tutoras.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "followed_lesson_users")
public class FollowedLessonUserEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private EventEntity lesson;
    
    @ManyToOne
    @JoinColumn(name = "student_id")
    private StudentEntity student;
    
    @SuppressWarnings("unused")
    private FollowedLessonUserEntity() {}
    
    public FollowedLessonUserEntity(EventEntity lesson, StudentEntity student) {
        this.lesson = lesson;
        this.student = student;
    }
} 