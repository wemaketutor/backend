package com.tutoras.tutoras.model;

import java.util.List;

import com.tutoras.tutoras.entity.TeacherEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudentResponse {
    private List<TeacherEntity> teachers;

}
