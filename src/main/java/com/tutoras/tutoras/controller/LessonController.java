package com.tutoras.tutoras.controller;

import org.springframework.web.bind.annotation.RestController;

import com.tutoras.tutoras.model.LessonResponse;
import com.tutoras.tutoras.model.LessonsRequest;
import com.tutoras.tutoras.model.LessonsResponse;
import com.tutoras.tutoras.security.UserPrincipal;
import com.tutoras.tutoras.service.LessonService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequiredArgsConstructor
public class LessonController extends BaseController {

    private final LessonService lessonService;
    
    @GetMapping("/lessons")
    public ResponseEntity<LessonsResponse> getLessons(
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestParam(name = "page", defaultValue = "1") Integer page,
        @RequestParam(name = "per_page", defaultValue = "100") Integer perPage,
        @RequestParam(name = "sort_by", defaultValue = "dataCreated") String sortBy,
        @RequestParam(name = "sort_order", defaultValue = "asc") String sortOrder
        ) {
            return ResponseEntity.status(HttpStatus.OK).body(lessonService.getLessons(principal.getUserId()));
    }

    @GetMapping("/lessons/{id}")
    public ResponseEntity<LessonResponse> getLessonById(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable("id") Long id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(lessonService.getLessonById(id));
    }

    @PostMapping("/lessons")
    public ResponseEntity<LessonResponse> createLesson(@AuthenticationPrincipal UserPrincipal principal, @RequestBody @Validated LessonsRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(lessonService.createLesson(principal.getUserId(), request.getName(), request.getDate(), request.getDuration(), request.getFollowedUserId()));
    }
    
    @PutMapping("/lessons/{id}")
    public ResponseEntity<LessonResponse> updateLesson(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable("id") Long id,
        @RequestBody @Validated LessonsRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(lessonService.updateLesson(principal.getUserId(), id, request));
    }
    
    @DeleteMapping("/lessons/{id}")
    public ResponseEntity<Void> deleteLesson(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable("id") Long id
    ) {
        lessonService.deleteLesson(principal.getUserId(), id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    
    @GetMapping("/teacher/{teacherId}/lessons")
    public ResponseEntity<LessonsResponse> getTeacherLessons(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable("teacherId") Long teacherId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(lessonService.getTeacherLessons(teacherId));
    }
    
    @PostMapping("/lessons/{lessonId}/take")
    public ResponseEntity<LessonResponse> takeLesson(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable("lessonId") Long lessonId,
        @RequestParam("studentId") Long studentId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(lessonService.takeLesson(lessonId, studentId));
    }
    
    @PostMapping("/lessons/{lessonId}/switch")
    public ResponseEntity<LessonResponse> switchLesson(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable("lessonId") Long lessonId,
        @RequestParam("studentId") Long studentId,
        @RequestParam("otherStudentId") Long otherStudentId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(lessonService.switchLesson(lessonId, studentId, otherStudentId));
    }
    
    @PostMapping("/lessons/{lessonId}/approve-switch")
    public ResponseEntity<LessonResponse> approveSwitchLesson(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable("lessonId") Long lessonId,
        @RequestParam("studentId") Long studentId,
        @RequestParam("approve") Boolean approve
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(lessonService.approveSwitchLesson(lessonId, studentId, approve));
    }
}
