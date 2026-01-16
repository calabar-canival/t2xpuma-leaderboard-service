package com.t2xafcon.leaderboard_service.model;

import lombok.Builder;

import java.util.List;

@Builder
public record LeaderboardFetchDataDTO(
      Boolean isSuccessful,
      String message,
      LeaderboardData data
) {
}
