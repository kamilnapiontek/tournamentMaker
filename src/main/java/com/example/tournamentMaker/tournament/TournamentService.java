package com.example.tournamentMaker.tournament;

import com.example.tournamentMaker.statistics.BasketballStatistics;
import com.example.tournamentMaker.statistics.FootballStatistics;
import com.example.tournamentMaker.statistics.Statistics;
import com.example.tournamentMaker.tournament.enums.Sport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TournamentService {
    private final TournamentRepository tournamentRepository;

    public void createTournament(TournamentRequest tournamentRequest) {
        Tournament tournament = new Tournament(
                tournamentRequest.getName(), tournamentRequest.getType(), tournamentRequest.getSport());
        tournamentRepository.save(tournament);
    }
}
