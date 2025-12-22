package com.t2xafcon.leaderboard_service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
@Table(name = "leaderboard")
public class Leaderboard {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "phone_number")
    String phoneNumber;
    @Column(name = "points")
    Long points;
    @Column(name = "rank")
    Integer rank;
    @Column(name = "last_synced_at")
    Instant lastSyncedAt;
}
