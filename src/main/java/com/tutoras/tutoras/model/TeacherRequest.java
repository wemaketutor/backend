package com.tutoras.tutoras.model;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class TeacherRequest {
    private String email;
    private List<String> subjects;
}
