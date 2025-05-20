package com.tutoras.tutoras.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tutoras.tutoras.entity.CommentEntity;
import com.tutoras.tutoras.entity.HomeworkEntity;
import com.tutoras.tutoras.entity.UserEntity;
import com.tutoras.tutoras.model.CommentRequest;
import com.tutoras.tutoras.model.CommentResponse;
import com.tutoras.tutoras.model.CommentsResponse;
import com.tutoras.tutoras.repository.CommentRepository;
import com.tutoras.tutoras.repository.HomeworkRepository;
import com.tutoras.tutoras.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CommentService {
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private HomeworkRepository homeworkRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public CommentsResponse getCommentsByHomeworkId(Long homeworkId) {
        List<CommentEntity> comments = commentRepository.findByHomeworkId(homeworkId);
        
        List<CommentResponse> responseComments = comments.stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());
        
        CommentsResponse response = new CommentsResponse();
        response.setComments(responseComments);
        response.setTotalCount(responseComments.size());
        
        return response;
    }
    
    @Transactional
    public CommentResponse createComment(CommentRequest request) {
        HomeworkEntity homework = homeworkRepository.findById(request.getHomeworkId())
                .orElseThrow(() -> new EntityNotFoundException("Homework not found with id: " + request.getHomeworkId()));
        
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));
        
        CommentEntity comment = new CommentEntity(request.getContent(), homework, user);
        
        CommentEntity savedComment = commentRepository.save(comment);
        
        return mapToCommentResponse(savedComment);
    }
    
    @Transactional
    public CommentResponse updateComment(Long id, CommentRequest request) {
        CommentEntity comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + id));
        
        if (request.getContent() != null) {
            comment.setContent(request.getContent());
        }
        
        comment.setUpdatedAt(OffsetDateTime.now());
        
        CommentEntity updatedComment = commentRepository.save(comment);
        
        return mapToCommentResponse(updatedComment);
    }
    
    @Transactional
    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new EntityNotFoundException("Comment not found with id: " + id);
        }
        
        commentRepository.deleteById(id);
    }
    
    private CommentResponse mapToCommentResponse(CommentEntity comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        
        if (comment.getHomework() != null) {
            response.setHomeworkId(comment.getHomework().getId());
        }
        
        if (comment.getUser() != null) {
            response.setUserId(comment.getUser().getId());
            response.setUserFullName(comment.getUser().getFirstName() + " " + comment.getUser().getLastName());
            response.setUserRole(comment.getUser().getRole() != null ? comment.getUser().getRole().name() : null);
        }
        
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());
        
        return response;
    }
} 