package com.example.tournamentmaker.excel;

import com.example.tournamentmaker.constans.Constans;
import com.example.tournamentmaker.excel.football.FootballCupExcelService;
import com.example.tournamentmaker.excel.football.FootballLeagueWriterService;
import com.example.tournamentmaker.tournament.Tournament;
import com.example.tournamentmaker.tournament.TournamentRepository;
import com.example.tournamentmaker.tournament.enums.Sport;
import com.example.tournamentmaker.tournament.enums.TournamentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
class ExcelService {
    private final TournamentRepository tournamentRepository;
    private final FootballCupExcelService footballCupExcelService;
    private final FootballLeagueWriterService footballLeagueWriterService;

    boolean writeTournamentInformationInExcel(String tournamentName) {
        Tournament tournament = tournamentRepository.findByName(tournamentName).orElseThrow(
                () -> new NoSuchElementException(Constans.NO_TOURNAMENT_FOUND));
        Sport sport = tournament.getSport();
        TournamentType type = tournament.getTournamentType();

        if (sport == Sport.FOOTBALL) {
            if (type == TournamentType.CUP) {
                return footballCupExcelService.writeTournamentInformation(tournament);
            }
            return footballLeagueWriterService.writeTournamentInformation(tournament);
        }
        return false;
    }
}
