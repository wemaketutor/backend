package com.tutoras.tutoras.controller;

import org.springframework.web.bind.annotation.RestController;

import com.tutoras.tutoras.model.EventRequest;
import com.tutoras.tutoras.security.UserPrincipal;
import com.tutoras.tutoras.service.EventService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/myevents")
    public ResponseEntity<?> getEvents(@AuthenticationPrincipal UserPrincipal principal) {
        return eventService.getEvents(principal.getUserId());
    }

    @PostMapping("/addevent")
    public ResponseEntity<?> addEvent(@AuthenticationPrincipal UserPrincipal principal, @RequestBody @Validated EventRequest request) {
        return eventService.addEvent(principal.getUserId(), request.getDate(), request.getName(), request.getDate_created());
    }
    
    @DeleteMapping("/delete/event/{event_id}")
    public ResponseEntity<?> deleteEvent(@AuthenticationPrincipal UserPrincipal principal, @PathVariable("event_id") Long studentId) {
        return eventService.deleteEvent(principal.getUserId(), studentId);
    }

    @PutMapping("/update/event")
    public ResponseEntity<?> putMethodName(@AuthenticationPrincipal UserPrincipal principal, @RequestBody @Validated EventRequest request) {
        return eventService.updateEntity(principal.getUserId(), request.getEvent_id(), request.getName(), request.getDate(), request.getDate_created(), request.getDescription());
    }

    @GetMapping("/myteacher/{teacher_id}/events")
    public ResponseEntity<?> getMyTeacherEvents(@AuthenticationPrincipal UserPrincipal principal, @PathVariable("teacher_id") Long teacherId) {
        return eventService.getMyTeacherEvents(principal.getUserId(), teacherId);
    }
    
    @GetMapping("/mystudent/{student_id}/events")
    public ResponseEntity<?> getMyStudentEvents(@AuthenticationPrincipal UserPrincipal principal, @PathVariable("student_id") Long studentId) {
        return eventService.getMyStudentEvents(principal.getUserId(), studentId);
    }
    

}
