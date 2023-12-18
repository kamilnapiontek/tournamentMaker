package com.example.tournamentmaker.excel.football;

import com.example.tournamentmaker.team.Team;
import com.example.tournamentmaker.team.TeamRepository;
import com.example.tournamentmaker.tournament.Tournament;
import com.example.tournamentmaker.tournament.enums.TournamentType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FootballResultsCupSheetStrategyTest {
    @Mock
    private TeamRepository teamRepository;
    @InjectMocks
    private FootballResultsCupSheetStrategy sheetCreator;
    private static final Workbook workbook = new XSSFWorkbook();
    private static final Sheet sheet = workbook.createSheet();

    @Test
    void shouldCreateSheetForFootballResultsCup() {
        // given
        Tournament tournament = FootballTournamentData.createFootballTournament(TournamentType.CUP);
        List<Team> teamList = tournament.getTeamList();
        List<List<String>> expectedTeamNames = getExpectedTeamNamesInEachRound();
        int roundsAmount = 4;
        List<Integer> colWhereExpectTeamNamesInRound = List.of(0, 2, 4, 6);
        List<Integer> rowWhereExpectFirstTeamNameInRound = List.of(0, 1, 3, 7);
        List<Integer> spaceBetweenTeamNamesInRound = List.of(2, 4, 8, 16);
        // when
        for (int i = 0; i < teamList.size(); i++) {
            when(teamRepository.findById(i + 1L)).thenReturn(Optional.of(teamList.get(i)));
        }
        sheetCreator.fillSheet(workbook, sheet, tournament);
        // then
        for (int i = 0; i < roundsAmount; i++) {
            checkIfTeamNamesInRoundMatch(expectedTeamNames.get(i), colWhereExpectTeamNamesInRound.get(i),
                    rowWhereExpectFirstTeamNameInRound.get(i), spaceBetweenTeamNamesInRound.get(i));
        }
    }

    private void checkIfTeamNamesInRoundMatch(List<String> expectedTeamNamesInRound, int colNumber, int rowNumber,
                                              int spaceBetweenTeamNames) {
        AtomicInteger row = new AtomicInteger(rowNumber);
        expectedTeamNamesInRound.forEach(expectedTeamName ->
                assertEquals(expectedTeamName,
                        sheet.getRow(row.getAndAdd(spaceBetweenTeamNames)).getCell(colNumber).getStringCellValue()));

    }

    private List<List<String>> getExpectedTeamNamesInEachRound() {
        List<String> expectedTeamNamesInFirstRound = List.of("AC Milan", "Ajax", "Arsenal", "Bayern Munich", "FC Barcelona",
                "Real Madrid", "Manchester United", "Zenit Saint Petersburg");
        List<String> expectedTeamNamesInSecondRound = List.of("AC Milan", "Arsenal", "FC Barcelona", "Manchester United");
        List<String> expectedTeamNamesInThirdRound = List.of("AC Milan", "FC Barcelona");
        List<String> expectedWinner = List.of("AC Milan");
        return List.of(expectedTeamNamesInFirstRound, expectedTeamNamesInSecondRound,
                expectedTeamNamesInThirdRound, expectedWinner);
    }
}