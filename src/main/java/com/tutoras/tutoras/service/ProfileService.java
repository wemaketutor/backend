package com.tutoras.tutoras.service;
import org.springframework.stereotype.Service;

import com.tutoras.tutoras.entity.Role;
import com.tutoras.tutoras.entity.StudentEntity;
import com.tutoras.tutoras.entity.TeacherEntity;
import com.tutoras.tutoras.entity.UserEntity;
import com.tutoras.tutoras.exception.ConflictException;
import com.tutoras.tutoras.exception.NotFindedSuchElementException;
import com.tutoras.tutoras.model.ProfileRequest;
import com.tutoras.tutoras.repository.StudentRepository;
import com.tutoras.tutoras.repository.TeacherRepository;
import com.tutoras.tutoras.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    public ProfileRequest updateProfile(Long userId, String username, String firstName, String lastName, String email, String password, String phone, Role role) {
        if (userId == null) {
            throw new IllegalArgumentException("ID не может быть null");
        }
        UserEntity existingUser = userRepository.findById(userId).orElseThrow(() -> new NotFindedSuchElementException("User not found with id: " + userId));
        if (!existingUser.getEmail().equals(email) && userRepository.findByEmail(email).isPresent()) {
            throw new ConflictException("Already exist user with email:" + email);
        }

        if (username != null && !username.isEmpty()) {existingUser.setUsername(username);} else {username = existingUser.getUsername();}
        if (firstName != null && !firstName.isEmpty()) {existingUser.setFirstName(firstName);} else {firstName = existingUser.getFirstName();}
        if (lastName != null && !lastName.isEmpty()) {existingUser.setLastName(lastName);} else {lastName = existingUser.getLastName();}
        if (email != null && !email.isEmpty()) {existingUser.setEmail(email);} else {email = existingUser.getEmail();}
        if (phone != null && !phone.isEmpty()) {existingUser.setPhone(phone);} else {phone = existingUser.getPhone();}
        if (role != null) {existingUser.setRole(role);} else {role = existingUser.getRole();}

        userRepository.save(existingUser);
        if (existingUser.getTeacherId() == null) {
            TeacherEntity teacher = new TeacherEntity(existingUser);
            teacherRepository.save(teacher);
            existingUser.setTeacherId(teacher.getId());
        }
        if (existingUser.getStudentId() == null) {
            StudentEntity student = new StudentEntity(existingUser);
            studentRepository.save(student);
            existingUser.setStudentId(student.getId());
        }
        userRepository.save(existingUser);
        return ProfileRequest.builder()
                .id(userId)
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password("")
                .phone(phone)
                .role(role.toValue())
                .build();
    }

    public void attemptDeleteProfile(Long userId) {
        UserEntity existingUser = userRepository.findById(userId).orElseThrow(() -> new NotFindedSuchElementException("User not found with id: " + userId));

        userRepository.delete(existingUser);
    }
}
