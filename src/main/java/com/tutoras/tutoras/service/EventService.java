package com.tutoras.tutoras.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tutoras.tutoras.entity.EventEntity;
import com.tutoras.tutoras.entity.StudentEntity;
import com.tutoras.tutoras.entity.TeacherEntity;
import com.tutoras.tutoras.entity.UserEntity;
import com.tutoras.tutoras.model.ErrorResponse;
import com.tutoras.tutoras.repository.EventRepository;
import com.tutoras.tutoras.repository.StudentRepository;
import com.tutoras.tutoras.repository.TeacherRepository;
import com.tutoras.tutoras.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    public ResponseEntity<?> getEvents(Long userId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            ErrorResponse errorResponse = new ErrorResponse(404L, "Пользователь не найден");
            return ResponseEntity.status(404).body(errorResponse);
        }
        UserEntity user = userOptional.get();
        return ResponseEntity.ok(user.getEvents());
    }

    public ResponseEntity<?> addEvent(Long userId, LocalDateTime date, String name, LocalDateTime date_created) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            ErrorResponse errorResponse = new ErrorResponse(404L, "Пользователь не найден");
            return ResponseEntity.status(404).body(errorResponse);
        }
        UserEntity user = userOptional.get();
        EventEntity event = new EventEntity(date, date_created, name, user, null);
        eventRepository.save(event);
        
        return ResponseEntity.status(201).body(event);
    }

    public ResponseEntity<?> deleteEvent(Long userId, Long eventId) {
        Optional<EventEntity> eventOptional = eventRepository.findById(eventId);
        if (!eventOptional.isPresent()) {
            ErrorResponse errorResponse = new ErrorResponse(404L, "Событие не найдено");
            return ResponseEntity.status(404).body(errorResponse);
        }
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            ErrorResponse errorResponse = new ErrorResponse(404L, "Пользователь не найден");
            return ResponseEntity.status(404).body(errorResponse);
        }
        UserEntity user = userOptional.get();
        EventEntity event = eventOptional.get();
        List<EventEntity> events = user.getEvents();
        if (!events.contains(event)) {
            ErrorResponse errorResponse = new ErrorResponse(403L, "Данное событие у вас не найдено");
            return ResponseEntity.status(403).body(errorResponse);
        }
        eventRepository.delete(event);
        return ResponseEntity.ok().build();
        
    }

    public ResponseEntity<?> updateEntity(Long userId, Long eventId, String name, LocalDateTime date, LocalDateTime dateCreated, String description) {
        Optional<EventEntity> eventOptional = eventRepository.findById(eventId);
        if (!eventOptional.isPresent()) {
            ErrorResponse errorResponse = new ErrorResponse(404L, "Событие не найдено");
            return ResponseEntity.status(404).body(errorResponse);
        }
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            ErrorResponse errorResponse = new ErrorResponse(404L, "Пользователь не найден");
            return ResponseEntity.status(404).body(errorResponse);
        }
        UserEntity user = userOptional.get();
        EventEntity event = eventOptional.get();
        List<EventEntity> events = user.getEvents();
        if (!events.contains(event)) {
            ErrorResponse errorResponse = new ErrorResponse(403L, "Данное событие у вас не найдено");
            return ResponseEntity.status(403).body(errorResponse);
        }
        EventEntity updatedEntity = new EventEntity(eventId ,date, dateCreated, name, user, description);
        eventRepository.save(updatedEntity);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> getMyTeacherEvents(Long studentId, Long getingPersonId) {
        Optional<TeacherEntity> teacherOptional = teacherRepository.findById(getingPersonId);
        if (!teacherOptional.isPresent()) {
            ErrorResponse errorResponse = new ErrorResponse(404L, "Учитель не найден");
            return ResponseEntity.status(404).body(errorResponse);
        }
        TeacherEntity teacher = teacherOptional.get();
        StudentEntity student = studentRepository.findById(studentId).get();
        List<TeacherEntity> realTeachers = student.getTeachers();
        if (!realTeachers.contains(teacher)) {
            ErrorResponse errorResponse = new ErrorResponse(403L, "Доступ к расписанию этого учителя для вас запрещён");
            return ResponseEntity.status(403).body(errorResponse);
        }
        UserEntity verifiedTeacher = userRepository.findById(getingPersonId).get();
        return ResponseEntity.ok(verifiedTeacher.getEvents());
    }

    public ResponseEntity<?> getMyStudentEvents(Long teacherId, Long getingPersonId) {
        Optional<StudentEntity> studentOptional = studentRepository.findById(getingPersonId);
        if (!studentOptional.isPresent()) {
            ErrorResponse errorResponse = new ErrorResponse(404L, "Ученик не найден");
            return ResponseEntity.status(404).body(errorResponse);
        }
        StudentEntity student = studentOptional.get();
        TeacherEntity teacher = teacherRepository.findById(teacherId).get();
        List<StudentEntity> realStudents = teacher.getStudents();
        if (!realStudents.contains(student)) {
            ErrorResponse errorResponse = new ErrorResponse(403L, "Доступ к расписанию этого ученика для вас запрещён");
            return ResponseEntity.status(403).body(errorResponse);
        }
        UserEntity verifiedStudent = userRepository.findById(getingPersonId).get();
        return ResponseEntity.ok(verifiedStudent.getEvents());
    }
}
