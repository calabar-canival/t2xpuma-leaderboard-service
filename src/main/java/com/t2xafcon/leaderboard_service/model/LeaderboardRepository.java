package com.t2xafcon.leaderboard_service.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface LeaderboardRepository extends JpaRepository<Leaderboard, Integer> {
    List<Leaderboard> findAllByOrderByPointsDesc();
    List<Leaderboard> findAllByOrderByPointsAsc();


    @Query(value = """
    SELECT
        ranked.id,
        ranked.phone_number,
        ranked.points,
        ranked.last_synced_at,
        ranked.rank
    FROM (
        SELECT
            l.id,
            l.phone_number,
            l.points,
            l.last_synced_at,
            DENSE_RANK() OVER (ORDER BY l.points DESC) AS rank
        FROM leaderboard l
    ) ranked
    WHERE ranked.rank <= :rankLimit
    ORDER BY ranked.rank, ranked.points DESC
    """, nativeQuery = true)
    List<Leaderboard> findTopRankingUsers(@Param("rankLimit") int rankLimit);


    @Modifying
    @Query(value = """
            INSERT INTO leaderboard (phone_number, points, last_synced_at)
            VALUES (:phoneNumber, :points, :lastSyncedAt)
            ON CONFLICT (phoneNumber)
            DO UPDATE SET
                points = EXCLUDED.points,
                last_synced_at = EXCLUDED.last_synced_at
            WHERE leaderboard.points <> EXCLUDED.points
            """, nativeQuery = true)
    void upsertLeaderboard(
            @Param("phoneNumber") String phoneNumber,
            @Param("points") Integer points,
            @Param("last_synced_at") Instant lastSyncedAt
    );
}
