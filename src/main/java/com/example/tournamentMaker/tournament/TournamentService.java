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
        Sport sport = tournamentRequest.getSport();
        Tournament tournament = new Tournament(tournamentRequest.getName(), tournamentRequest.getType(), sport);
        switch (sport) {
            case FOOTBALL -> {
                FootballStatistics footballStatistics = new FootballStatistics();
                save(tournament, footballStatistics);
            }
            case BASKETBALL -> {
                BasketballStatistics basketballStatistics = new BasketballStatistics();
                save(tournament, basketballStatistics);
            }
        }
        tournamentRepository.save(tournament);
    }

    private void save(Tournament tournament, Statistics statistics) {
        tournament.setStatistics(statistics);
        statistics.setTournament(tournament);
        tournamentRepository.save(tournament);
    }
}
