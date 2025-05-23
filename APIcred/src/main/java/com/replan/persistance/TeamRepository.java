package com.replan.persistance;


import com.replan.persistance.entity.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;


public interface TeamRepository extends JpaRepository<TeamEntity, UUID> {
    List<TeamEntity> findByOwnerId(UUID ownerId);

}
