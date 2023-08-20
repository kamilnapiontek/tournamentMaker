package com.example.tournamentMaker.excel;

import com.example.tournamentMaker.constans.Constans;
import com.example.tournamentMaker.tournament.Tournament;
import com.example.tournamentMaker.tournament.TournamentRepository;
import com.example.tournamentMaker.tournament.enums.Sport;
import com.example.tournamentMaker.tournament.enums.TournamentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
class ExcelWriterService {
    private final TournamentRepository tournamentRepository;
    private final FootballCupWriterService footballCupWriterService;
    private final FootballLeagueWriterService footballLeagueWriterService;

    boolean writeTournamentInformationInExcel(String tournamentName) {
        Tournament tournament = tournamentRepository.findByName(tournamentName).orElseThrow(
                () -> {
                    throw new NoSuchElementException(Constans.NO_TOURNAMENT_FOUND);
                });
        Sport sport = tournament.getSport();
        TournamentType type = tournament.getTournamentType();

        if (sport == Sport.FOOTBALL) {
            if (type == TournamentType.CUP) {
                return footballCupWriterService.writeTournamentInformation(tournament);
            } else {
                return footballLeagueWriterService.writeTournamentInformation(tournament);
            }
        }
        return false;
    }
}
