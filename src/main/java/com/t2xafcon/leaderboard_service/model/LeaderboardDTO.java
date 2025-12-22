package com.t2xafcon.leaderboard_service.model;

import lombok.Builder;

@Builder
public record LeaderboardDTO(
        String phoneNumber,
        Long points,
        Integer rank
) {
}
