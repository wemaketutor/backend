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
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/lessons")
    public ResponseEntity<LessonResponse> createLesson(@AuthenticationPrincipal UserPrincipal principal, @RequestBody @Validated LessonsRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(lessonService.createLesson(principal.getUserId(), request.getName(), request.getDate(), request.getDuration(), request.getFollowedUserId()));
    }
    
    
}
