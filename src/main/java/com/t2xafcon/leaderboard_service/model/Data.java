package com.t2xafcon.leaderboard_service.model;

import java.time.LocalDateTime;

public record Data (
        Integer id,
        String msisdn,
        Integer points,
        LocalDateTime created_at,
        LocalDateTime updated_at
) {
}
