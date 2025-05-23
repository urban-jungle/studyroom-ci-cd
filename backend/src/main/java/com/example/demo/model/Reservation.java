package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reserverName;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    // Getters and Setters
    public Long getId() { return id; }
    public String getReserverName() { return reserverName; }
    public void setReserverName(String reserverName) { this.reserverName = reserverName; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
}
