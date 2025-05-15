package com.tutoras.tutoras.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;

import com.tutoras.tutoras.entity.Role;
import com.tutoras.tutoras.entity.UserEntity;
import com.tutoras.tutoras.exception.ConflictException;
import com.tutoras.tutoras.exception.ValidationException;
import com.tutoras.tutoras.model.RegistrationResponse;
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

    @Test
    void attemptRegistration_ShouldReturnUserEmailTextAndCreatedStatus() {
        String email = "teacher@gmail.com";
        String password = "12345";
        String role = "teacher";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        UserEntity savedUser = new UserEntity(email, "encodedPassword", Role.fromString(role));
        savedUser.setId(1L);

        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);

        RegistrationResponse registrationResponse = registrationService.attemptRegistration(email, password, Role.fromString(role));

        assertEquals(email, registrationResponse.getText());

        verify(userRepository).save(any(UserEntity.class));
    }

    @Test 
    void attemptRegistration_EmailAlreadyExist_ShouldReturnConflictErrorResponseAndConflictStatus(){
        String email = "teacher@gmail.com";
        String password = "12345";
        String role = "teacher";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new UserEntity(email, password, Role.fromString(role))));

        ConflictException exception = assertThrows(ConflictException.class, () -> {registrationService.attemptRegistration(email, password, Role.fromString(role));});

        assertEquals("Email already exists", exception.getDetail());
    }

    @Test
    void attemptRegistration_InvalidEmail_ShouldReturnValidationErrorResponseAndUnprocessableEntityStatus() {
        String email = "invalid-email";
        String password = "12345";
        String role = "teacher";
        
        ValidationException exception = assertThrows(ValidationException.class, () -> {registrationService.attemptRegistration(email, password, Role.fromString(role));});

        assertEquals("email", exception.getField());
        assertEquals("The mail is incorrect", exception.getMessage());
    }
}
