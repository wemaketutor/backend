package com.tutoras.tutoras.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tutoras.tutoras.model.CommentRequest;
import com.tutoras.tutoras.service.CommentService;

@RestController
@RequestMapping("/api")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    @GetMapping("/homeworks/{homework_id}/comments")
    public ResponseEntity<?> getCommentsByHomeworkId(@PathVariable("homework_id") Long homeworkId) {
        return ResponseEntity.ok(commentService.getCommentsByHomeworkId(homeworkId));
    }
    
    @PostMapping("/comments")
    public ResponseEntity<?> createComment(@RequestBody CommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(request));
    }
    
    @PutMapping("/comments/{comment_id}")
    public ResponseEntity<?> updateComment(
            @PathVariable("comment_id") Long commentId,
            @RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.updateComment(commentId, request));
    }
    
    @DeleteMapping("/comments/{comment_id}")
    public ResponseEntity<?> deleteComment(@PathVariable("comment_id") Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
} 