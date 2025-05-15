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
            @RequestParam(required = true) Long studentId,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(defaultValue = "dueDate") String sort_by,
            @RequestParam(defaultValue = "desc") String sort_order,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int per_page) {
        
        // if (teacherId != null) {
            //возвращаем дз studentId от teacherId
            return ResponseEntity.status(HttpStatus.CREATED).body(homeworkService.getHomeworksForStudentFromTeacher(studentId, teacherId, page, per_page, sort_by, sort_order));
        // } else {
            // возвращаем все дз studentId
            // return homeworkService.getHomeworksByStudent(studentId, page, per_page);
        // }
    }
    
    @GetMapping("/homeworks/{homework_id}")
    public ResponseEntity<?> getHomeworkById(@PathVariable("homework_id") Long homeworkId) {
        return ResponseEntity.ok(homeworkService.getHomeworkById(homeworkId));
    }
    
    @PostMapping("/homeworks")
    public ResponseEntity<?> createHomework(@RequestBody HomeworkRequest request) {
        HomeworkResponse response = homeworkService.createHomework(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/homeworks/{homework_id}")
    public ResponseEntity<?> updateHomework(
            @PathVariable("homework_id") Long homeworkId,
            @RequestBody HomeworkRequest request) {
        return ResponseEntity.ok(homeworkService.updateHomework(homeworkId, request));
    }
    
    @DeleteMapping("/homeworks/{homework_id}")
    public ResponseEntity<?> deleteHomework(@PathVariable("homework_id") Long homeworkId) {
        homeworkService.deleteHomework(homeworkId);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/homeworks/{homework_id}/status")
    public ResponseEntity<?> updateHomeworkStatus(
            @PathVariable("homework_id") Long homeworkId,
            @RequestBody String status) {
        return ResponseEntity.ok(homeworkService.updateHomeworkStatus(homeworkId, status));
    }
} 