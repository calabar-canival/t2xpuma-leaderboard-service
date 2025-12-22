package com.t2xafcon.leaderboard_service.model;

import lombok.Builder;

@Builder
public record UserPersistResponseDTO(
        boolean isSuccessful,
        String message,
        Data data
) {
}
