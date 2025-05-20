package com.tutoras.tutoras.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tutoras.tutoras.model.*;
import com.tutoras.tutoras.service.HomeworkService;

@RestController
@RequestMapping("/api")
public class HomeworkController {
    
    @Autowired
    private HomeworkService homeworkService;
    
    @GetMapping("/homeworks")
    public ResponseEntity<HomeworksResponse> getAllHomeworks(
            @RequestParam(name = "studentId", required = false) Long studentId,
            @RequestParam(name = "teacherId", required = false) Long teacherId,
            @RequestParam(name = "sort_by", defaultValue = "dueDate") String sort_by,
            @RequestParam(name = "sort_order", defaultValue = "desc") String sort_order,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "per_page", defaultValue = "10") int per_page) {
        
        // Если указан studentId, используем фильтрацию по студенту
        if (studentId != null) {
            // Если также указан teacherId, фильтруем по обоим
            if (teacherId != null) {
                return ResponseEntity.ok(homeworkService.getHomeworksForStudentFromTeacher(studentId, teacherId, page, per_page, sort_by, sort_order));
            } else {
                // Если только studentId, фильтруем только по студенту
                return ResponseEntity.ok(homeworkService.getHomeworksForStudent(studentId, page, per_page, sort_by, sort_order));
            }
        } 
        // Если указан только teacherId, фильтруем по учителю
        else if (teacherId != null) {
            return ResponseEntity.ok(homeworkService.getHomeworksForTeacher(teacherId, page, per_page, sort_by, sort_order));
        }
        // Если указан статус, фильтруем по нему
        else if (status != null) {
            return ResponseEntity.ok(homeworkService.getHomeworksByStatus(status, page, per_page));
        }
        // Иначе возвращаем все доступные домашние задания
        else {
            return ResponseEntity.ok(homeworkService.getAllHomeworks(page, per_page, sort_by, sort_order));
        }
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
            @RequestBody StatusRequest status) {
        return ResponseEntity.ok(homeworkService.updateHomeworkStatus(homeworkId, status.getStatus()));
    }
} 