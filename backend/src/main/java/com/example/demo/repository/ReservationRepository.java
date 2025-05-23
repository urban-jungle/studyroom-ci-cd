package com.example.demo.repository;

import com.example.demo.model.Reservation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;

public interface ReservationRepository extends CrudRepository<Reservation, Long> {

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reservation r " +
           "WHERE r.room.id = :roomId AND " +
           "((:startTime < r.endTime) AND (:endTime > r.startTime))")
    boolean existsByRoomIdAndTimeOverlap(Long roomId, LocalDateTime startTime, LocalDateTime endTime);
}
