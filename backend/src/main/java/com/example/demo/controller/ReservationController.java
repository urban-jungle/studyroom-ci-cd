package com.example.demo.controller;

import com.example.demo.model.Reservation;
import com.example.demo.model.Room;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.RoomRepository; // ✅ 수정
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class ReservationController {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository; // ✅ 수정

    public ReservationController(ReservationRepository reservationRepository, RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository; // ✅ 수정
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

        // ✅ 이 부분 깔끔하게 수정
        roomRepository.findById(roomId).ifPresent(reservation::setRoom);

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
                reservationRepository.save(existing);
                return ResponseEntity.ok(existing);
            }).orElse(ResponseEntity.notFound().build());
    }
}
