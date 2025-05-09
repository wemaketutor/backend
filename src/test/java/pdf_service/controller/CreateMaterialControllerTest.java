package pdf_service.controller;


import com.tutoras.tutoras.security.UserPrincipal;
import com.tutoras.tutoras.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pdf_service.model.CreateMaterialRequest;
import pdf_service.serivce.CreateMaterialService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateMaterialControllerTest {

    @Mock
    private CreateMaterialService createMaterialService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private CreateMaterialController createMaterialController;

    private UserPrincipal createTeacherPrincipal() {
        return UserPrincipal.builder()
                .userId(1L)
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_TEACHER")))
                .build();
    }

    private UserPrincipal createStudentPrincipal() {
        return UserPrincipal.builder()
                .userId(2L)
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT")))
                .build();
    }

    private CreateMaterialRequest createValidRequest() {
        return CreateMaterialRequest.builder()
                .sources_id(List.of(1L, 2L))
                .build();
    }

    @Test
    void createMaterial_UnauthorizedWhenPrincipalNull() {
        ResponseEntity<?> response = createMaterialController.createMaterial(null, createValidRequest());

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("User not authenticated", response.getBody());
    }

    @Test
    void createMaterial_ForbiddenWhenNotTeacher() {
        UserPrincipal student = createStudentPrincipal();
        ResponseEntity<?> response = createMaterialController.createMaterial(student, createValidRequest());

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Only teachers can create materials", response.getBody());
    }

    @Test
    void createMaterial_BadRequestWhenRequestNull() {
        UserPrincipal teacher = createTeacherPrincipal();
        ResponseEntity<?> response = createMaterialController.createMaterial(teacher, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid request data", response.getBody());
    }

    @Test
    void createMaterial_Success() {
        UserPrincipal teacher = createTeacherPrincipal();
        CreateMaterialRequest request = createValidRequest();
        Long expectedId = 1L;

        when(createMaterialService.createMaterial(anyLong(), any()))
                .thenReturn(expectedId);

        ResponseEntity<?> response = createMaterialController.createMaterial(teacher, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedId, response.getBody());
    }
}