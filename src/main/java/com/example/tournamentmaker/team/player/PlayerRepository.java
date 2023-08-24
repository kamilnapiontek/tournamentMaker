package com.example.tournamentmaker.team.player;

import com.example.tournamentmaker.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {
    Optional<FootballPlayer> findByJerseyNumberAndTeam(int jerseyNumber, Team team);
    Optional<FootballPlayer> findById(Long id);
}
