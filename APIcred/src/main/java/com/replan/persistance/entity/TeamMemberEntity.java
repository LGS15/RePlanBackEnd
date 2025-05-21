package com.replan.persistance.entity;

import com.replan.domain.objects.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

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
            columnDefinition = "CHAR(36)"
    )
    private String id;

    @Column(name = "team_id", nullable = false, columnDefinition = "CHAR(36)")
    private String teamId;

    @Column(name = "user_id", nullable = false, columnDefinition ="CHAR(36)" )
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name="role", nullable = false)
    private Role role;

}
