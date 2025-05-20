package com.tutoras.tutoras.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
@Data
public class TeacherResponse {
    private List<StudentInResponse> students;
}
