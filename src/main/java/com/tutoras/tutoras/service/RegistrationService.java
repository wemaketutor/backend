package com.tutoras.tutoras.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.tutoras.tutoras.entity.Role;
import com.tutoras.tutoras.entity.StudentEntity;
import com.tutoras.tutoras.entity.TeacherEntity;
import com.tutoras.tutoras.entity.UserEntity;
import com.tutoras.tutoras.exception.ConflictException;
import com.tutoras.tutoras.exception.ValidationException;
import com.tutoras.tutoras.model.RegistrationResponse;
import com.tutoras.tutoras.repository.StudentRepository;
import com.tutoras.tutoras.repository.TeacherRepository;
import com.tutoras.tutoras.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email != null && email.matches(emailRegex);
    }    

    public RegistrationResponse attemptRegistration(String email, String password, Role role) {
        if (!isValidEmail(email)) {
            throw new ValidationException("email", "The mail is incorrect");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ConflictException("Email already exists");
        }

        String encodedPassword = passwordEncoder.encode(password);
        UserEntity user = new UserEntity(email, encodedPassword, role);
        userRepository.save(user);

        if (Role.TEACHER.equals(role)) {
            TeacherEntity teacher = new TeacherEntity(user);
            teacherRepository.save(teacher);
            user.setTeacherId(teacher.getId());
        } else if (Role.STUDENT.equals(role)) {
            StudentEntity student = new StudentEntity(user);
            studentRepository.save(student);
            user.setStudentId(student.getId());
        }
        userRepository.save(user);
        
        return RegistrationResponse.builder().text(user.getEmail()).build();
    }
}
