package com.example.demo.controller;

import com.example.demo.model.StudyRoom;
import com.example.demo.repository.StudyRoomRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class StudyRoomController {

    private final StudyRoomRepository studyRoomRepository;

    public StudyRoomController(StudyRoomRepository studyRoomRepository) {
        this.studyRoomRepository = studyRoomRepository;
    }

    @GetMapping
    public List<StudyRoom> getAllRooms() {
        return (List<StudyRoom>) studyRoomRepository.findAll();
    }
}
