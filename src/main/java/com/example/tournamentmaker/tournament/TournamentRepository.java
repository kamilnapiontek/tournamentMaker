package com.example.tournamentmaker.tournament;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TournamentRepository extends JpaRepository<Tournament,Long> {
    Optional<Tournament> findByName(String name);
}
