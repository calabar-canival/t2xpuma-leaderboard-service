package com.t2xafcon.leaderboard_service.events;

import com.t2xafcon.leaderboard_service.model.LeaderboardFetchDataDTO;

import java.util.List;

public record LeaderboardDataFetchedEvent(
        LeaderboardFetchDataDTO usersFetched
) {
}
