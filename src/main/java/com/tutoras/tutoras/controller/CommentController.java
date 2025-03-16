package com.tutoras.tutoras.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.tutoras.tutoras.entity.UserEntity;
import com.tutoras.tutoras.model.CommentRequest;
import com.tutoras.tutoras.model.CommentResponse;
import com.tutoras.tutoras.service.CommentService;
import com.tutoras.tutoras.service.UserService;

@RestController
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/homeworks/{homework_id}/comments")
    public ResponseEntity<List<CommentResponse>> getCommentsByHomework(@PathVariable("homework_id") Long homeworkId) {
        return ResponseEntity.ok(commentService.getCommentsByHomework(homeworkId));
    }
    
    @PostMapping("/comments")
    public ResponseEntity<CommentResponse> addComment(
            @RequestBody CommentRequest request,
            Authentication authentication) {
        UserEntity user = userService.getUserByEmail(authentication.getName());
        CommentResponse response = commentService.addCommentToHomework(request, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/comments/{comment_id}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable("comment_id") Long commentId,
            @RequestBody CommentRequest request,
            Authentication authentication) {
        UserEntity user = userService.getUserByEmail(authentication.getName());
        try {
            CommentResponse response = commentService.updateComment(commentId, request, user.getId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    
    @DeleteMapping("/comments/{comment_id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable("comment_id") Long commentId,
            Authentication authentication) {
        UserEntity user = userService.getUserByEmail(authentication.getName());
        try {
            commentService.deleteComment(commentId, user.getId());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
} 