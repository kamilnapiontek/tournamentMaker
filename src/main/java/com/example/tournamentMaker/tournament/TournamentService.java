package com.example.tournamentMaker.tournament;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class TournamentService {
    private final TournamentRepository tournamentRepository;

    void createTournament(TournamentRequest tournamentRequest) {
        Tournament tournament = new Tournament(
                tournamentRequest.getName(), tournamentRequest.getType(), tournamentRequest.getSport());
        tournamentRepository.save(tournament);
    }

    void finishRegistration(String tournamentName) {
        Optional<Tournament> optionalTournament = tournamentRepository.findByName(tournamentName);
        optionalTournament.ifPresentOrElse(tournament -> {
                    tournament.setRegistrationComplete(true);
                    tournamentRepository.save(tournament);
                },
                () -> {
                    throw new NoSuchElementException("No tournament with the given name was found");
                });
    }
}
