package com.tutoras.tutoras.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "lesson_students")
public class LessonStudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lessonId", referencedColumnName = "id", nullable = false)
    private LessonEntity lesson;

    @ManyToOne
    @JoinColumn(name = "studentId", referencedColumnName = "id", nullable = false)
    private StudentEntity student;

    @SuppressWarnings("unused")
    private LessonStudentEntity () {}
}
