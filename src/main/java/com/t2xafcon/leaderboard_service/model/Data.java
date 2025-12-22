package com.t2xafcon.leaderboard_service.model;

public record Data (
        String msisdn,
        Integer points,
        String channel,
        boolean isActive
) {
}
