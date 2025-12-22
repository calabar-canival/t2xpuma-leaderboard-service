package com.t2xafcon.leaderboard_service.model;

import lombok.Builder;

@Builder
public record LeaderboardRankDTO(
        String phoneNumber,
        Long points,
        Integer rank
) {
}
