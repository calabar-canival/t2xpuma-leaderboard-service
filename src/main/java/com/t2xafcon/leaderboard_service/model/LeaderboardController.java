package com.t2xafcon.leaderboard_service.model;

import com.t2xafcon.leaderboard_service.config.ApiResponseBody;
import com.t2xafcon.leaderboard_service.service.LeaderboardService;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Validated
@Tag(name = "Leaderboard", description = "Manages APIs for leaderboard data")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @PostMapping("/msisdn/{phoneNumber}")
    @ApiResponse(description = "Receives phone number from frontend", responseCode = "200",
            content = @Content(contentSchema = @Schema(implementation = HashMap.class))
    )
    public ResponseEntity<ApiResponseBody<Map<String, String>>> getUserPhoneNumber(
            @PathVariable String phoneNumber
    ){
        Map<String, String> response = new HashMap<>();
        try {
            //pass into phone number update event
            leaderboardService.handlePhoneNumberUpdate(phoneNumber);
            response.put("success", "Phone number updated successfully");
            return ResponseEntity.ok(ApiResponseBody.success(response));
        } catch (RuntimeException e) {
            response.put("error", e.getMessage());
            List<String> errors = new ArrayList<>();
            errors.add(response.get("error"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponseBody.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), errors));

        }
    }

    @GetMapping("/leaderboard/top-ranking/{rankLimit}")
    @ApiResponse(responseCode = "200", description = "Gets top ranking leaderboard entry with ties", content = @Content(array = @ArraySchema(
            schema = @Schema(implementation = LeaderboardDTO.class))))
    public ResponseEntity<ApiResponseBody<List<LeaderboardDTO>>> getTopRankingUsers(@PathVariable @Min(1) int rankLimit) {
        List<LeaderboardDTO> response =  leaderboardService.getTopRankingUsers(rankLimit);
        return ResponseEntity.ok(ApiResponseBody.success(response));
    }

    @GetMapping("/leaderboard/asc")
    @ApiResponse(responseCode = "200", description = "Gets leaderboard data in ascending order", content = @Content(array = @ArraySchema(
            schema = @Schema(implementation = LeaderboardDTO.class))))
    public ResponseEntity<ApiResponseBody<List<LeaderboardDTO>>> getLeaderboardAsc() {
        List<LeaderboardDTO> response = leaderboardService.getLeaderboardByAscendingOrder();
        return ResponseEntity.ok(ApiResponseBody.success(response));
    }

    @GetMapping("/leaderboard/desc")
    @ApiResponse(responseCode = "200", description = "Gets leaderboard data in descending order", content = @Content(array = @ArraySchema(
            schema = @Schema(implementation = LeaderboardDTO.class))))
    public ResponseEntity<ApiResponseBody<List<LeaderboardDTO>>> getLeaderboardByDescendingOrder() {
        List<LeaderboardDTO> response = leaderboardService.getLeaderboardByDescendingOrder();
        return ResponseEntity.ok(ApiResponseBody.success(response));
    }
}
