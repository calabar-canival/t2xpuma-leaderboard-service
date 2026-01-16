package com.t2xafcon.leaderboard_service.model;

import lombok.Builder;

import java.util.List;

@Builder
public record LeaderboardData(
        List<Data> data
) {
}
