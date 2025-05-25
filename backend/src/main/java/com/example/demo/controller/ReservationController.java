package com.example.demo.controller;

import com.example.demo.model.Reservation;
import com.example.demo.model.Room;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.StudyRoomRepository;
import com.example.demo.repository.RoomRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class ReservationController {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    public ReservationController(ReservationRepository reservationRepository, RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }

    @GetMapping
    public List<Reservation> getAllReservations() {
        return (List<Reservation>) reservationRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody Reservation reservation) {
        Long roomId = reservation.getRoom().getId();
        LocalDateTime start = reservation.getStartTime();
        LocalDateTime end = reservation.getEndTime();

        boolean overlap = reservationRepository.existsByRoomIdAndTimeOverlap(roomId, start, end);
        if (overlap) {
            return ResponseEntity.status(409).body("이미 예약된 시간입니다.");
        }

        Optional<Room> roomOptional = roomRepository.findById(roomId);
        if (roomOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("해당 스터디룸을 찾을 수 없습니다.");
        }

        reservation.setRoom(roomOptional.get());
        return ResponseEntity.ok(reservationRepository.save(reservation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReservation(@PathVariable Long id, @RequestBody Reservation updated) {
        return reservationRepository.findById(id)
            .map(existing -> {
                existing.setReserverName(updated.getReserverName());
                existing.setStartTime(updated.getStartTime());
                existing.setEndTime(updated.getEndTime());
                existing.setRoom(updated.getRoom());
                return ResponseEntity.ok(reservationRepository.save(existing));
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
