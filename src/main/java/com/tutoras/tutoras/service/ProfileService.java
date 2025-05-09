package com.tutoras.tutoras.service;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tutoras.tutoras.entity.UserEntity;
import com.tutoras.tutoras.model.ErrorResponse;
import com.tutoras.tutoras.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ImageService imageService;
    private final UserRepository userRepository;

    public ResponseEntity<?> updateProfile(Long userId, String firstName, String lastName, String extraInfo, MultipartFile avatar) {
        String avatarFileName = null;
        if (avatar != null) {
            try {
                avatarFileName = imageService.saveImage("./uploads/images/avatars", avatar);
            } catch (IOException e) {
                ErrorResponse errorResponse = new ErrorResponse(401L, "Ошибка загрузки изображения");
                return ResponseEntity.status(401).body(errorResponse);
            }
        }
        UserEntity user = userRepository.findById(userId).get();
        String existedAvatarPath = user.getAvatar();
        if (existedAvatarPath != null && avatarFileName == null) {
            avatarFileName = existedAvatarPath;
        }
        UserEntity updatedEntity = new UserEntity(userId, firstName, lastName, extraInfo, avatarFileName, user.getEmail(), user.getPassword(), user.getRole(), user.getEvents(), user.getEventsAsFollower());
        userRepository.save(updatedEntity);
        return ResponseEntity.ok().build();

    }
}
