package com.tutoras.tutoras.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tutoras.tutoras.entity.CommentEntity;
import com.tutoras.tutoras.entity.HomeworkCommentEntity;
import com.tutoras.tutoras.entity.HomeworkEntity;
import com.tutoras.tutoras.entity.UserEntity;
import com.tutoras.tutoras.model.CommentRequest;
import com.tutoras.tutoras.model.CommentResponse;
import com.tutoras.tutoras.repository.CommentRepository;
import com.tutoras.tutoras.repository.HomeworkCommentRepository;
import com.tutoras.tutoras.repository.HomeworkRepository;
import com.tutoras.tutoras.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CommentService {
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private HomeworkRepository homeworkRepository;
    
    @Autowired
    private HomeworkCommentRepository homeworkCommentRepository;
    
    public List<CommentResponse> getCommentsByHomework(Long homeworkId) {
        HomeworkEntity homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new EntityNotFoundException("Homework not found with id: " + homeworkId));
        
        List<HomeworkCommentEntity> homeworkComments = homeworkCommentRepository.findByHomework(homework);
        
        return homeworkComments.stream()
                .map(hc -> mapToCommentResponse(hc.getComment()))
                .collect(Collectors.toList());
    }
    
    @Transactional
    public CommentResponse addCommentToHomework(CommentRequest request, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        
        HomeworkEntity homework = homeworkRepository.findById(request.getHomeworkId())
                .orElseThrow(() -> new EntityNotFoundException("Homework not found with id: " + request.getHomeworkId()));
        
        CommentEntity comment = new CommentEntity(request.getBody(), user);
        CommentEntity savedComment = commentRepository.save(comment);
        
        HomeworkCommentEntity homeworkComment = new HomeworkCommentEntity(homework, savedComment);
        homeworkCommentRepository.save(homeworkComment);
        
        return mapToCommentResponse(savedComment);
    }
    
    @Transactional
    public CommentResponse updateComment(Long commentId, CommentRequest request, Long userId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));
        
        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not the author of the comment");
        }
        
        comment.setBody(request.getBody());
        comment.setUpdatedAt(LocalDateTime.now());
        
        CommentEntity updatedComment = commentRepository.save(comment);
        
        return mapToCommentResponse(updatedComment);
    }
    
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));
        
        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not the author of the comment");
        }
        
        List<HomeworkCommentEntity> homeworkComments = homeworkCommentRepository.findByComment(comment);
        homeworkCommentRepository.deleteAll(homeworkComments);
        commentRepository.delete(comment);
    }
    
    private CommentResponse mapToCommentResponse(CommentEntity comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setBody(comment.getBody());
        response.setUserId(comment.getUser().getId());
        response.setUserName(comment.getUser().getFirstName() + " " + comment.getUser().getLastName());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());
        
        return response;
    }
} 