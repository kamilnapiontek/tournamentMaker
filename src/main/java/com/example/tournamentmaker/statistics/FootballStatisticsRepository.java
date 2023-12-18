package com.example.tournamentmaker.statistics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FootballStatisticsRepository extends JpaRepository<FootballStatistics,Long> {
    Optional<FootballStatistics> findByTeamId(Long id);
}
