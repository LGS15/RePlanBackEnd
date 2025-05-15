package com.replan.persistance;


import com.replan.persistance.entity.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TeamRepository extends JpaRepository<TeamEntity, String> {
    List<TeamEntity> findByOwnerId(String ownerId);
}
