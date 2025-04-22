package com.replan.persistance.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

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
    @Column(
            name = "id",
            updatable = false,
            nullable = false,
            columnDefinition = "CHAR(36)"
    )
    private String id;

    @Column(name = "team_name", nullable = false)
    private String teamName;

    @Column(name = "game_name",nullable = false)
    private String gameName;

    @Column(name = "owner_id", nullable = false)
    private String ownerId;
}
