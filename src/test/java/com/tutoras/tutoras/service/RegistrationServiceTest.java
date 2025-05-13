package com.tutoras.tutoras.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;

import com.tutoras.tutoras.entity.UserEntity;
import com.tutoras.tutoras.model.ConflictErrorResponse;
import com.tutoras.tutoras.model.RegistrationResponse;
import com.tutoras.tutoras.model.ValidationErrorResponse;
import com.tutoras.tutoras.repository.StudentRepository;
import com.tutoras.tutoras.repository.TeacherRepository;
import com.tutoras.tutoras.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistrationService registrationService;

    @SuppressWarnings("null")
    @Test
    void attemptRegistration_ShouldReturnUserEmailTextAndCreatedStatus() {
        String email = "teacher@gmail.com";
        String password = "12345";
        String role = "ROLE_TEACHER";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        UserEntity savedUser = new UserEntity(email, "encodedPassword", role);
        savedUser.setId(1L);

        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);

        ResponseEntity<?> response = registrationService.attemptRegistration(email, password, role);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof RegistrationResponse);
        RegistrationResponse registrationResponse = (RegistrationResponse) response.getBody();
        assertEquals(email, registrationResponse.getText());
    }

    @SuppressWarnings("null")
    @Test 
    void attemptRegistration_EmailAlreadyExist_ShouldReturnConflictErrorResponseAndConflictStatus(){
        String email = "teacher@gmail.com";
        String password = "12345";
        String role = "ROLE_TEACHER";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new UserEntity(email, password, role)));

        ResponseEntity<?> response = registrationService.attemptRegistration(email, password, role);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody() instanceof ConflictErrorResponse);
        ConflictErrorResponse conflictErrorResponse = (ConflictErrorResponse) response.getBody();
        assertEquals("Email already exists", conflictErrorResponse.getDetail());
    }

    @SuppressWarnings("null")
    @Test
    void attemptRegistration_InvalidEmail_ShouldReturnValidationErrorResponseAndUnprocessableEntityStatus() {
        String email = "invalid-email";
        String password = "12345";
        String role = "ROLE_TEACHER";
        
        ResponseEntity<?> response = registrationService.attemptRegistration(email, password, role);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody() instanceof ValidationErrorResponse);
        ValidationErrorResponse validationErrorResponse = (ValidationErrorResponse) response.getBody();
        assertEquals("email", validationErrorResponse.getField());
        assertEquals("The mail is incorrect", validationErrorResponse.getDetail());
    }
}
