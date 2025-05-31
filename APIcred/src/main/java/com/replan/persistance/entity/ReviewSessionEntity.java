package com.replan.persistance.entity;


import com.replan.business.mapper.UuidToBytesConverter;
import com.replan.domain.objects.SessionStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table (name = "ReviewSession")
@Data
@NoArgsConstructor
public class ReviewSessionEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Convert(converter = UuidToBytesConverter.class)
    @Column(name= "id", updatable = false, nullable = false, columnDefinition = "BINARY(16")
    private UUID id;

    @Convert(converter = UuidToBytesConverter.class)
    @Column(name = "team_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID teamId;

    @Column(name = "video_url", nullable = false, length = 500)
    private String videoUrl;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "current_timestamp", nullable = false)
    private Long currentTimestamp = 0L;

    @Column(name = "is_playing", nullable = false)
    private Boolean isPlaying = false;

    @Convert(converter = UuidToBytesConverter.class)
    @Column(name = "created_by", nullable = false, columnDefinition = "BINARY(16)")
    private UUID createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SessionStatus status = SessionStatus.ACTIVE;
}
