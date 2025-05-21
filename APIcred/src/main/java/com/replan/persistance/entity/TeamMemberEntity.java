package com.replan.persistance.entity;

import com.replan.domain.objects.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "TeamMember",
uniqueConstraints = {
@UniqueConstraint(columnNames = {"team_id", "user_id"})
    })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberEntity {
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
            columnDefinition = "BINARY(16)"
    )
    private UUID id;

    @Column(name = "team_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID teamId;

    @Column(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID userId;


    @Enumerated(EnumType.STRING)
    @Column(name="role", nullable = false)
    private Role role;

}
