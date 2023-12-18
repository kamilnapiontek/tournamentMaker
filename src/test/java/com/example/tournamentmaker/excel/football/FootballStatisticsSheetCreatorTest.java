package com.example.tournamentmaker.excel.football;

import com.example.tournamentmaker.statistics.FootballStatistics;
import com.example.tournamentmaker.statistics.FootballStatisticsRepository;
import com.example.tournamentmaker.team.Team;
import com.example.tournamentmaker.team.player.FootballPlayer;
import com.example.tournamentmaker.team.player.PlayerRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.tournamentmaker.excel.football.FootballTournamentData.createFootballTournament;
import static com.example.tournamentmaker.excel.football.FootballTournamentData.getStringCellValue;
import static com.example.tournamentmaker.util.PlayerUtil.createPlayerListForEachTeam;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FootballStatisticsSheetCreatorTest {
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private FootballStatisticsRepository footballStatisticsRepository;
    @InjectMocks
    FootballStatisticsSheetCreator sheetCreator;
    private static final Workbook workbook = new XSSFWorkbook();
    private static final Sheet sheet = workbook.createSheet();
    private static final int ROW_AMOUNT_IN_TABLE = 4;
    private static final int COLUMN_AMOUNT_IN_TABLE = 4;
    private static final int ROW_NUMBER_WHERE_FIRST_AND_SECOND_TABLE_START = 1;
    private static final int ROW_NUMBER_WHERE_THIRD_AND_FOURTH_TABLE_START = 13;
    private static final int COLUMN_NUMBER_WHERE_FIRST_AND_THIRD_TABLE_START = 0;
    private static final int COLUMN_NUMBER_WHERE_SECOND_AND_FOURTH_TABLE_START = 5;

    @Test
    void shouldFillSheetWithStatistics() {
        // given
        Tournament tournament = createFootballTournament(TournamentType.LEAGUE);
        List<Team> teamList = tournament.getTeamList();
        List<FootballStatistics> statistics = createStatisticList();
        int howManyPlayersForTeam = 4;
        List<FootballPlayer> players = createPlayerListForEachTeam(teamList, howManyPlayersForTeam);
        // when
        for (int i = 0; i < teamList.size(); i++) {
            when(footballStatisticsRepository.findByTeamId(i + 1L)).thenReturn(Optional.of(statistics.get(i)));
        }
        for (int i = 0; i < players.size(); i++) {
            when(playerRepository.findById(i + 1L)).thenReturn(Optional.of(players.get(i)));
        }
        sheetCreator.fillSheet(workbook, sheet, tournament);
        // then
        checkCorrectnessInTables();
    }

    private void checkCorrectnessInTables() {
        List<String> expectedPlayerNames = List.of(
                "AC Milan 1 player", "Ajax 5 player", "Arsenal 9 player", "Bayern Munich 13 player");
        checkCorrectnessInTable(expectedPlayerNames,
                ROW_NUMBER_WHERE_FIRST_AND_SECOND_TABLE_START, COLUMN_NUMBER_WHERE_FIRST_AND_THIRD_TABLE_START);

        expectedPlayerNames = List.of(
                "AC Milan 2 player", "Ajax 6 player", "Arsenal 10 player", "Bayern Munich 14 player");
        checkCorrectnessInTable(expectedPlayerNames,
                ROW_NUMBER_WHERE_FIRST_AND_SECOND_TABLE_START, COLUMN_NUMBER_WHERE_SECOND_AND_FOURTH_TABLE_START);

        expectedPlayerNames = List.of(
                "AC Milan 3 player", "Ajax 7 player", "Arsenal 11 player", "Bayern Munich 15 player");
        checkCorrectnessInTable(expectedPlayerNames,
                ROW_NUMBER_WHERE_THIRD_AND_FOURTH_TABLE_START, COLUMN_NUMBER_WHERE_FIRST_AND_THIRD_TABLE_START);

        expectedPlayerNames = List.of(
                "AC Milan 4 player", "Ajax 8 player", "Arsenal 12 player", "Bayern Munich 16 player");
        checkCorrectnessInTable(expectedPlayerNames,
                ROW_NUMBER_WHERE_THIRD_AND_FOURTH_TABLE_START, COLUMN_NUMBER_WHERE_SECOND_AND_FOURTH_TABLE_START);
    }

    private void checkCorrectnessInTable(List<String> expectedPlayerNames, int rowNumber, int colNumber) {
        List<String> expectedPlayerPlaces = List.of("1", "2", "3", "4");
        List<String> expectedNumberOfEvents = List.of("4", "3", "2", "1");
        List<String> expectedPlayerTeams = List.of("AC Milan", "Ajax", "Arsenal", "Bayern Munich");

        List<List<String>> expectedValuesList = List.of(expectedPlayerPlaces, expectedPlayerNames, expectedPlayerTeams,
                expectedNumberOfEvents);

        for (List<String> expectedList : expectedValuesList) {
            for (int i = 0; i < COLUMN_AMOUNT_IN_TABLE; i++) {
                assertEquals(expectedList.get(i), getStringCellValue(colNumber, rowNumber++, sheet));
            }
            rowNumber = rowNumber - ROW_AMOUNT_IN_TABLE;
            colNumber++;
        }
    }

    private List<FootballStatistics> createStatisticList() {
        List<FootballStatistics> statistics = new ArrayList<>();
        long playerId = 1L;
        for (int i = 4; i > 0; i--) {
            statistics.add(createStatisticAndAddEvents(playerId, i));
            playerId = playerId + 4L;
        }
        return statistics;
    }

    private FootballStatistics createStatisticAndAddEvents(long playerId, int numberOfEvens) {
        FootballStatistics statistics = new FootballStatistics();
        statistics.getPlayersIdWithGoal().put(playerId++, numberOfEvens);
        statistics.getPlayersIdWithYellowCard().put(playerId++, numberOfEvens);
        statistics.getPlayersIdWithRedCard().put(playerId++, numberOfEvens);
        statistics.getPlayersIdWithCleanSheets().put(playerId, numberOfEvens);
        return statistics;
    }
}