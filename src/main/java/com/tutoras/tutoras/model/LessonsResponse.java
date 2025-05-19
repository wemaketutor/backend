package com.tutoras.tutoras.model;

import java.util.List;

import com.tutoras.tutoras.entity.LessonEntity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LessonsResponse {
    List<LessonEntity> ownerLessons;
    List<LessonEntity> followerLessons;
}
