package com.replan.business.mapper;

import com.replan.domain.objects.SessionStatus;
import com.replan.domain.requests.CreateReviewSessionRequest;
import com.replan.domain.responses.CreateReviewSessionResponse;
import com.replan.domain.responses.ReviewSessionResponse;
import com.replan.persistance.entity.ReviewSessionEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReviewSessionMapper {
    private ReviewSessionMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static ReviewSessionEntity toEntity(CreateReviewSessionRequest request) {
        ReviewSessionEntity entity = new ReviewSessionEntity();
        entity.setTeamId(UUID.fromString(request.getTeamId()));
        entity.setVideoUrl(request.getVideoUrl());
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setCurrentTimestamp(0L);
        entity.setIsPlaying(false);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setStatus(SessionStatus.ACTIVE);
        return entity;
    }

    public static CreateReviewSessionResponse toCreateResponse(ReviewSessionEntity entity) {
        return new CreateReviewSessionResponse(
                entity.getId().toString(),
                entity.getTeamId().toString(),
                entity.getVideoUrl(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getCurrentTimestamp(),
                entity.getIsPlaying(),
                entity.getCreatedBy().toString()
        );
    }

    public static ReviewSessionResponse toResponse(ReviewSessionEntity entity) {
        return new ReviewSessionResponse(
                entity.getId().toString(),
                entity.getTeamId().toString(),
                entity.getVideoUrl(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getCurrentTimestamp(),
                entity.getIsPlaying(),
                entity.getCreatedBy().toString(),
                entity.getStatus().toString(),
                0 // activeParticipants
        );
    }

    public static ReviewSessionResponse toResponseWithParticipantCount(ReviewSessionEntity entity, Integer activeParticipants) {
        return new ReviewSessionResponse(
                entity.getId().toString(),
                entity.getTeamId().toString(),
                entity.getVideoUrl(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getCurrentTimestamp(),
                entity.getIsPlaying(),
                entity.getCreatedBy().toString(),
                entity.getStatus().toString(),
                activeParticipants
        );
    }
}