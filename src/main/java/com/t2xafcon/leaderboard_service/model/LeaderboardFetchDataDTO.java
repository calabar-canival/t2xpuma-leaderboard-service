package com.t2xafcon.leaderboard_service.model;

import lombok.Builder;

import java.util.List;

@Builder
public record LeaderboardFetchDataDTO(
      String isSuccessful,
      String message,
      List<Data> data
) {
}
