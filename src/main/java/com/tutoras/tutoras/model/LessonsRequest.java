package com.tutoras.tutoras.model;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonsRequest {
    private String name;
    private OffsetDateTime date;
    private OffsetDateTime duration;
    private Long followedUserId;
}
