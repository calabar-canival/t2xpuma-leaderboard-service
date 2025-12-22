package com.t2xafcon.leaderboard_service.service;

import com.t2xafcon.leaderboard_service.events.LeaderboardDataFetchedEvent;
import com.t2xafcon.leaderboard_service.events.PhoneNumberUpdatedEvent;
import com.t2xafcon.leaderboard_service.model.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@Slf4j
public class LeaderboardServiceImpl implements LeaderboardService{

    @Value("${yellow-dot.api-key}")
    private String yellowDotApiKey;

    private final LeaderboardRepository leaderboardRepository;
    private final ApplicationEventPublisher publisher;
    private final WebClient yellowDotWebClient;
    private final UserRepository userRepository;

    public LeaderboardServiceImpl(LeaderboardRepository leaderboardRepository, ApplicationEventPublisher publisher, WebClient yellowDotWebClient, UserRepository userRepository) {
        this.leaderboardRepository = leaderboardRepository;
        this.publisher = publisher;
        this.yellowDotWebClient = yellowDotWebClient;
        this.userRepository = userRepository;
    }

    /**
     * Handles phone number update
     */
    @Override
    public void handlePhoneNumberUpdate(String phoneNumber) {

        //publishes phone number event
        publisher.publishEvent(
                new PhoneNumberUpdatedEvent(
                        phoneNumber
                )
        );

        log.info("Phone number updated!");
    }

    /**
     * persists user to database if NOT exists
     * */
    @EventListener
    @Async
    @Transactional
    public void onPhoneNumberUpdate(PhoneNumberUpdatedEvent event) {
        UserPersistResponseDTO userPersistResponseDTO = sendUserPhoneNumber(event.phoneNumber());

        User user = User.builder()
                .phoneNumber(userPersistResponseDTO.data().msisdn())
                .points(userPersistResponseDTO.data().points())
                .channel(userPersistResponseDTO.data().channel())
                .predictionSubscriptionStatus(userPersistResponseDTO.data().isActive())
                .build();

        if(!userRepository.existsByPhoneNumber(user.getPhoneNumber())){
            userRepository.save(user);
        }
    }

    /**
     * Handles persisting of user phone to yellow-dot
     */
    public UserPersistResponseDTO sendUserPhoneNumber(String phoneNumber) {
        // send phone number to yellow dot
        return yellowDotWebClient.get()
                .uri(uri -> uri.path("/auth/status").queryParam("msisdn", phoneNumber).build())
                .headers(h -> h.set("Authorization", getAuthHeader(yellowDotApiKey)))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new RuntimeException("HTTP " + clientResponse.statusCode() + ": " + body))))
                .bodyToMono(UserPersistResponseDTO.class)
                .block(Duration.ofSeconds(30));
    }

    //get yellowDot auth header
    private String getAuthHeader(String token){
        return "Bearer " + token;
    }

    /**
     * Runs every two minutes
     * Fetches leaderboard data from yellow dot
     * */
    @Scheduled(fixedDelay = 120_000)
    public void handleLeaderBoardDataFetch() {
        LeaderboardFetchDataDTO leaderboardData = yellowDotWebClient.get()
                .uri(uri -> uri.path("/api/leaderboard").queryParam("pageNumber", 1).queryParam("pageSize", 100).build())
                .headers(h -> h.set("Authorization", getAuthHeader(yellowDotApiKey)))
                .retrieve()
                .bodyToMono(LeaderboardFetchDataDTO.class)
                .block();

        publisher.publishEvent(
                new LeaderboardDataFetchedEvent(leaderboardData)
        );
    }

    /**
     * Listen to leaderboard fetch event
     * Upserts database (checks if exist, then updates fields )
     * */
    @EventListener
    @Async
    public void onLeaderboardDataFetchedEvent(LeaderboardDataFetchedEvent event) {

        event.usersFetched().data().forEach(leaderboard -> {
            leaderboardRepository.upsertLeaderboard(
                    leaderboard.msisdn(),
                    leaderboard.points(),
                    Instant.now()
            );
        });
    }

    @Override
    public List<LeaderboardDTO> getLeaderboardByAscendingOrder() {
        return leaderboardRepository
                .findAllByOrderByPointsAsc()
                .stream()
                .map(this::mapToLeaderboardDTO)
                .toList();
    }

    @Override
    public List<LeaderboardDTO> getLeaderboardByDescendingOrder() {
        return leaderboardRepository
                .findAllByOrderByPointsDesc()
                .stream()
                .map(this::mapToLeaderboardDTO)
                .toList();
    }

    @Override
    public List<LeaderboardDTO> getTopRankingUsers(int rankLimit) {
        return leaderboardRepository
                .findTopRankingUsers(rankLimit)
                .stream()
                .map(this::mapToLeaderboardDTO)
                .toList();
    }

    LeaderboardDTO mapToLeaderboardDTO(Leaderboard leaderboard) {
        return leaderboard == null ? null : new LeaderboardDTO(
                leaderboard.getPhoneNumber(),
                leaderboard.getPoints(),
                leaderboard.getRank()
        );
    }
}
