package com.example.tournamentmaker.excel.football;

import com.example.tournamentmaker.excel.SheetCreator;
import com.example.tournamentmaker.tournament.Tournament;
import com.example.tournamentmaker.tournament.enums.TournamentType;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FootballResultsSheetCreator implements SheetCreator {
    private final FootballResultsCupSheetCreator cupResultsCreator;
    private final FootballResultsLeagueSheetCreator leagueResultsCreator;
    private static final String SHEET_NAME = "Results";

    @Override
    public Sheet fillSheet(Workbook workbook, Sheet sheet, Tournament tournament) {
        if (tournament.getTournamentType() == TournamentType.CUP) {
            return cupResultsCreator.fillSheet(workbook, sheet, tournament);
        }
        return leagueResultsCreator.fillSheet(workbook, sheet, tournament);
    }

    @Override
    public String getName() {
        return SHEET_NAME;
    }
}
