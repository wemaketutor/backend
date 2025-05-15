package com.tutoras.tutoras.entity;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "lessons")
public class LessonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String subject;

    @ManyToOne
    @JoinColumn(name = "teacherId", nullable = false)
    private TeacherEntity teacher;

    private OffsetDateTime starTime;

    private OffsetDateTime endTime;

    // @ManyToMany(mappedBy = "lessons")
    // private List<StudentEntity> students;

    private String homeworkLink;

    private OffsetDateTime dateCreated;
    
    private OffsetDateTime updateAt;

    @SuppressWarnings("unused")
    private LessonEntity () {}

    public LessonEntity(Long id, OffsetDateTime date, OffsetDateTime dateCreated, String name, TeacherEntity teacher, String description) {
        this.id = id;
        this.dateCreated = dateCreated;
        this.name = name;
        this.teacher = teacher;
        this.updateAt = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS);;
        this.description = description;
    }

    public LessonEntity(LocalDateTime date, OffsetDateTime dateCreated, String name, TeacherEntity teacher, String description) {
        this.dateCreated = dateCreated;
        this.name = name;
        this.teacher = teacher;
        this.updateAt = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS);;
        this.description = description;
    }
}
