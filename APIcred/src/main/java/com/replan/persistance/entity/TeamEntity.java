package com.replan.persistance.entity;

import com.replan.business.mapper.UuidToBytesConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "Team")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID" ,
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Convert(converter = UuidToBytesConverter.class)
    @Column(
            name = "id",
            updatable = false,
            nullable = false,
            columnDefinition = "CHAR(36)"
    )
    private UUID id;

    @Column(name = "team_name", nullable = false)
    private String teamName;

    @Column(name = "game_name",nullable = false)
    private String gameName;

    @Convert(converter = UuidToBytesConverter.class)
    @Column(name = "owner_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID ownerId;
}
