package com.example.tournamentmaker.excel.football;

import com.example.tournamentmaker.tournament.Tournament;
import com.example.tournamentmaker.tournament.enums.TournamentType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.example.tournamentmaker.excel.football.FootballTournamentData.getStringCellValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FootballResultsLeagueSheetStrategyTest {
    private final FootballResultsLeagueSheetStrategy sheetCreator = new FootballResultsLeagueSheetStrategy();
    private static final Workbook workbook = new XSSFWorkbook();
    private static final Sheet sheet = workbook.createSheet();

    @Test
    void shouldCreateSheetForFootballResultsLeague() {
        // given
        Tournament tournament = FootballTournamentData.createFootballTournament(TournamentType.LEAGUE);
        // when
        sheetCreator.fillSheet(workbook, sheet, tournament);
        // then
        checkCorrectnessInTable(tournament);
    }

    private void checkCorrectnessInTable(Tournament tournament) {
        int teamsAmount = tournament.getTeamList().size();
        List<List<String>> expectedValuesList = getExpectedValuesList();

        int colNumber = 0;
        for (List<String> expectedList : expectedValuesList) {
            int rowNumber = 1;
            for (int i = 0; i < teamsAmount - 1; i++) {
                assertEquals(expectedList.get(i), getStringCellValue(colNumber, rowNumber++, sheet));
            }
            colNumber++;
        }
    }

    private List<List<String>> getExpectedValuesList() {
        List<String> teamsPlaces = List.of("1", "2", "3", "4");
        List<String> teamNames = List.of("AC Milan", "Ajax", "Arsenal", "Bayern Munich");
        List<String> numberOfGamesPlayed = List.of("3", "3", "3", "3");
        List<String> numberOfWins = List.of("3", "2", "1", "0");
        List<String> numberOfDraws = List.of("0", "0", "0", "0");
        List<String> numberOfLoses = List.of("0", "1", "2", "3");
        List<String> numberOfPoints = List.of("9", "6", "3", "0");
        List<String> last5 = List.of("W D L ", "W D L ", "W D L ", "W D L ");
        return List.of(teamsPlaces, teamNames, numberOfGamesPlayed, numberOfWins, numberOfDraws,
                numberOfLoses, numberOfPoints, last5);
    }
}