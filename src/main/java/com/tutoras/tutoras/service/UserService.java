package com.tutoras.tutoras.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;
import com.tutoras.tutoras.entity.UserEntity;
import com.tutoras.tutoras.exception.NotFindedSuchElementException;
import com.tutoras.tutoras.model.UserResponse;
import com.tutoras.tutoras.model.UserResponse.UserData;
import com.tutoras.tutoras.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserResponse getProfile(String email) {
        UserEntity profile = userRepository.findByEmail(email).orElseThrow(() -> new NotFindedSuchElementException("User not found with email: " + email));
        return mapToUserResponse(profile);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }
    
    public UserResponse getUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return mapToUserResponse(user);
    }
    
    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }
    
    private UserResponse mapToUserResponse(UserEntity user) {
        UserData userData = new UserData();
        userData.setId(user.getId());
        userData.setEmail(user.getEmail());
        userData.setUsername(user.getUsername());
        userData.setFirstName(user.getFirstName());
        userData.setLastName(user.getLastName());
        userData.setPassword("");
        userData.setPhone(user.getPhone());
        userData.setRole(user.getRole().toValue());

        UserResponse response = new UserResponse();
        response.setUser(userData);
        return response;
    }

}
