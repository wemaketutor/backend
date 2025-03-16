package com.tutoras.tutoras.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tutoras.tutoras.model.*;
import com.tutoras.tutoras.service.HomeworkService;

@RestController
public class HomeworkController {
    
    @Autowired
    private HomeworkService homeworkService;
    
    @GetMapping("/homeworks")
    public ResponseEntity<HomeworksResponse> getAllHomeworks(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int per_page) {
        
        if (studentId != null) {
            return ResponseEntity.ok(homeworkService.getHomeworksByStudent(studentId, page, per_page));
        } else if (status != null) {
            return ResponseEntity.ok(homeworkService.getHomeworksByStatus(status, page, per_page));
        } else {
            return ResponseEntity.ok(homeworkService.getAllHomeworks(page, per_page));
        }
    }
    
    @GetMapping("/homeworks/{homework_id}")
    public ResponseEntity<HomeworkResponse> getHomeworkById(@PathVariable("homework_id") Long homeworkId) {
        return ResponseEntity.ok(homeworkService.getHomeworkById(homeworkId));
    }
    
    @PostMapping("/homeworks")
    public ResponseEntity<HomeworkResponse> createHomework(@RequestBody HomeworkRequest request) {
        HomeworkResponse response = homeworkService.createHomework(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/homeworks/{homework_id}")
    public ResponseEntity<HomeworkResponse> updateHomework(
            @PathVariable("homework_id") Long homeworkId,
            @RequestBody HomeworkRequest request) {
        return ResponseEntity.ok(homeworkService.updateHomework(homeworkId, request));
    }
    
    @DeleteMapping("/homeworks/{homework_id}")
    public ResponseEntity<Void> deleteHomework(@PathVariable("homework_id") Long homeworkId) {
        homeworkService.deleteHomework(homeworkId);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/homeworks/{homework_id}/status")
    public ResponseEntity<HomeworkResponse> updateHomeworkStatus(
            @PathVariable("homework_id") Long homeworkId,
            @RequestBody String status) {
        return ResponseEntity.ok(homeworkService.updateHomeworkStatus(homeworkId, status));
    }
} 