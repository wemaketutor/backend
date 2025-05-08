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

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
                .email("teacher@example.com")
                .password("password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_TEACHER")))
                .build();
    }

    private UserPrincipal createStudentPrincipal() {
        return UserPrincipal.builder()
                .userId(2L)
                .email("student@example.com")
                .password("password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT")))
                .build();
    }

    @Test
    void createMaterial_ShouldReturnUnauthorized_WhenPrincipalIsNull() {
        ResponseEntity<?> response = createMaterialController.createMaterial(null, new CreateMaterialRequest());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("User not authenticated", response.getBody());
    }

    @Test
    void createMaterial_ShouldReturnForbidden_WhenUserIsNotTeacher() {
        UserPrincipal studentPrincipal = createStudentPrincipal();
        CreateMaterialRequest request = new CreateMaterialRequest();

        ResponseEntity<?> response = createMaterialController.createMaterial(studentPrincipal, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Only teachers can create materials", response.getBody());
    }

    @Test
    void createMaterial_ShouldReturnBadRequest_WhenRequestIsNull() {
        UserPrincipal teacherPrincipal = createTeacherPrincipal();

        ResponseEntity<?> response = createMaterialController.createMaterial(teacherPrincipal, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid request data", response.getBody());
    }

    @Test
    void createMaterial_ShouldReturnMaterialId_WhenSuccessful() {
        UserPrincipal teacherPrincipal = createTeacherPrincipal();
        CreateMaterialRequest request = new CreateMaterialRequest();
        Long expectedMaterialId = 1L;

        when(createMaterialService.createMaterial(anyLong(), any(CreateMaterialRequest.class)))
                .thenReturn(expectedMaterialId);

        ResponseEntity<?> response = createMaterialController.createMaterial(teacherPrincipal, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedMaterialId, response.getBody());
    }
}