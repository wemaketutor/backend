package com.tutoras.tutoras.entity;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    private OffsetDateTime startTime;

    private OffsetDateTime endTime;

    @Transient
    private List<Long> studentIds;

    private String homeworkLink;

    private OffsetDateTime dateCreated;
    
    private OffsetDateTime updateAt;

    @OneToMany(mappedBy = "lesson")
    @JsonIgnore
    private List<LessonStudentEntity> lessonStudent;

    @ManyToOne
    @JoinColumn(name = "teacherId", nullable = false)
    @JsonIgnore
    private TeacherEntity teacher;

    @SuppressWarnings("unused")
    private LessonEntity () {}

    public LessonEntity(String name, OffsetDateTime date, OffsetDateTime duration, List<Long> followedUserId, TeacherEntity teacher) {
        this.name = name;
        this.startTime = date;
        this.endTime = duration;
        this.studentIds = followedUserId; 
        this.teacher = teacher;
        this.dateCreated = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS);;
        this.updateAt = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS);;
    }

    public List<Long> getStudentIds() {
        if (studentIds == null) {
            if (lessonStudent == null) {
                return Collections.emptyList();
            }
            studentIds = lessonStudent.stream()
                .map(ls -> ls.getStudent().getId())
                .collect(Collectors.toList());
        }
        return studentIds;
    }
}
