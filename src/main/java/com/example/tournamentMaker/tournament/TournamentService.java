package com.example.tournamentMaker.tournament;

import com.example.tournamentMaker.statistics.BasketballStatistics;
import com.example.tournamentMaker.statistics.FootballStatistics;
import com.example.tournamentMaker.statistics.Statistics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TournamentService {
    private final TournamentRepository tournamentRepository;

    public void createTournament(TournamentRequest tournamentRequest) {
        Tournament tournament = new Tournament(tournamentRequest.getName(), tournamentRequest.getType(),
                tournamentRequest.getSport());
        switch (tournamentRequest.getSport()) {
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
        // MOŻLIWE, ŻE ZAPIS Z DRUGIEJ STRONY KONIECZNY
    }
}
