package com.tutoras.tutoras.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "materials")
public class MaterialEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String subject;
    
    private String description;
    
    @Column(nullable = false)
    private String fileUrl;
    
    @Column(nullable = false)
    private Boolean isPublic;
    
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private TeacherEntity teacher;
    
    @SuppressWarnings("unused")
    private MaterialEntity() {}
    
    public MaterialEntity(String title, String subject, String description, 
                         String fileUrl, Boolean isPublic, TeacherEntity teacher) {
        this.title = title;
        this.subject = subject;
        this.description = description;
        this.fileUrl = fileUrl;
        this.isPublic = isPublic;
        this.teacher = teacher;
    }
} 