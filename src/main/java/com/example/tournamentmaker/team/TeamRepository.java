package com.example.tournamentmaker.team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team,Long> {
    Optional<Team> findByName(String name);
    Optional<Team> findByNameAndTournamentName(String teamName, String tournamentName);
}
