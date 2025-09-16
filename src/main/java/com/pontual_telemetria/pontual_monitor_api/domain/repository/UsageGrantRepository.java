package com.pontual_telemetria.pontual_monitor_api.domain.repository;

import com.pontual_telemetria.pontual_monitor_api.domain.model.regulatory.UsageGrant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsageGrantRepository extends JpaRepository<UsageGrant, Long> {
    @EntityGraph(attributePaths = {"monthlyGrants", "location"})
    List<UsageGrant> getAllByLocationId(Long locationId);

    @EntityGraph(attributePaths = "monthlyGrants")
    List<UsageGrant> findAllByExternalIdOrderByStartDateDesc(Long externalId);

    Optional<UsageGrant> findTopByLocationIdOrderByStartDateDesc(Long locationId);
}
