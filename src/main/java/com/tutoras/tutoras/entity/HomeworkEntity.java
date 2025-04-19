package com.tutoras.tutoras.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "homeworks")
public class HomeworkEntity {
    
    public enum HomeworkStatus {
        NOT_DONE,
        PENDING,
        COMPLETED
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    private String description;
    
    @Column(nullable = false)
    private LocalDateTime dueDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HomeworkStatus status;
    
    private Integer grade;
    
    @Column(nullable = false)
    private Integer assessmentScale;
    
    @ManyToOne
    @JoinColumn(name = "student_id")
    private StudentEntity student;
    
    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private EventEntity lesson;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @SuppressWarnings("unused")
    private HomeworkEntity() {}
    
    public HomeworkEntity(String title, String description, LocalDateTime dueDate, 
                         HomeworkStatus status, Integer assessmentScale, 
                         StudentEntity student, EventEntity lesson) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
        this.assessmentScale = assessmentScale;
        this.student = student;
        this.lesson = lesson;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
} 