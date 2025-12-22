package com.t2xafcon.leaderboard_service.service;

import com.t2xafcon.leaderboard_service.events.LeaderboardDataFetchedEvent;
import com.t2xafcon.leaderboard_service.model.LeaderboardDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LeaderboardService {
    void handlePhoneNumberUpdate(String phoneNumber);
    List<LeaderboardDTO> getLeaderboardByAscendingOrder();
    List<LeaderboardDTO> getLeaderboardByDescendingOrder();
    List<LeaderboardDTO> getTopRankingUsers(int rankLimit);

}
