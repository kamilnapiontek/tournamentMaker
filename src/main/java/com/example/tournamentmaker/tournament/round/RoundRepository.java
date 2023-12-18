package com.example.tournamentmaker.tournament.round;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoundRepository extends JpaRepository<Round, Long> {
}
