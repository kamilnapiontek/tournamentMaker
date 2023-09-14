package com.example.tournamentmaker.excel.football;

import com.example.tournamentmaker.constans.Constans;
import com.example.tournamentmaker.excel.ExcelUtil;
import com.example.tournamentmaker.statistics.FootballStatistics;
import com.example.tournamentmaker.statistics.FootballStatisticsRepository;
import com.example.tournamentmaker.team.Team;
import com.example.tournamentmaker.team.player.FootballPlayer;
import com.example.tournamentmaker.team.player.PlayerRepository;
import com.example.tournamentmaker.tournament.Tournament;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class FootballStatisticWriterService {
    private final PlayerRepository playerRepository;
    private final FootballStatisticsRepository footballStatisticsRepository;
    private static final int MAX_ROWS_AMOUNT = 30;
    private static final int STATISTIC_TABLE_WIDTH_WITH_FREE_SPACE = 5;
    private static final int STATISTIC_TABLE_LENGTH_WITH_FREE_SPACE = 12;
    private static final int PLACE_COLUMN_EXCEL_WIDTH = 3;
    private static final int PLAYER_COLUMN_EXCEL_WIDTH = 26;
    private static final int TEAM_COLUMN_EXCEL_WIDTH = 20;
    private static final int STATISTIC_COLUMN_EXCEL_WIDTH = 12;


    void writeStatistic(Workbook workbook, Sheet sheet, int rowStart, int colStart, Tournament tournament) {
        ExcelUtil.createRows(sheet, MAX_ROWS_AMOUNT);
        CellStyle borderCellStyle = ExcelUtil.createCellStyle(workbook, IndexedColors.GOLD.getIndex());
        CellStyle fillingTableCellStyle = ExcelUtil.createCellStyle(workbook, IndexedColors.LEMON_CHIFFON.getIndex());

        List<Team> teamList = tournament.getTeamList();
        int secondTableColStart = colStart + STATISTIC_TABLE_WIDTH_WITH_FREE_SPACE;

        createStatisticTable(sheet, rowStart, colStart, teamList, FootballStatisticType.GOALS,
                borderCellStyle, fillingTableCellStyle);
        createStatisticTable(sheet, rowStart, secondTableColStart, teamList, FootballStatisticType.YELLOW_CARDS,
                borderCellStyle, fillingTableCellStyle);

        rowStart += STATISTIC_TABLE_LENGTH_WITH_FREE_SPACE;

        createStatisticTable(sheet, rowStart, colStart, teamList, FootballStatisticType.RED_CARDS,
                borderCellStyle, fillingTableCellStyle);
        createStatisticTable(sheet, rowStart, secondTableColStart, teamList, FootballStatisticType.CLEAN_SHEETS,
                borderCellStyle, fillingTableCellStyle);
    }

    private void createStatisticTable(Sheet sheet, int rowStart, int colStart, List<Team> teamList,
                                      FootballStatisticType type, CellStyle borderCellStyle, CellStyle fillingTableCellStyle) {


        setColumnsWidth(sheet, colStart);
        crateColumnsHeaders(sheet, rowStart, colStart, type, borderCellStyle);
        rowStart++;

        int place = 1;
        Map<Long, Integer> topTen = getTopTenPlayersInSpecificStatistic(teamList, type);

        for (Long playerId : topTen.keySet()) {
            FootballPlayer player = playerRepository.findById(playerId).orElseThrow(
                    () -> {
                        throw new NoSuchElementException("Can't find player with given id");
                    }
            );
            Row row = sheet.getRow(rowStart);

            List<String> stringsToWrittenForPlayer = List.of(Integer.toString(place++), player.getFirstName() + " " +
                    player.getLastName(), player.getTeam().getName(), topTen.get(playerId).toString());

            AtomicInteger col = new AtomicInteger(colStart);
            stringsToWrittenForPlayer.forEach(string ->
                    ExcelUtil.createCell(row, col.getAndIncrement(), fillingTableCellStyle, string));
            rowStart++;
        }
    }

    private Map<Long, Integer> getTopTenPlayersInSpecificStatistic(List<Team> teamList, FootballStatisticType type) {
        Map<Long, Integer> allPlayersWithSpecificStatistic = new HashMap<>();

        for (Team team : teamList) {
            FootballStatistics statistics = footballStatisticsRepository.findByTeamId(team.getId()).orElseThrow(() -> {
                throw new NoSuchElementException(Constans.NO_TEAM_FOUND);
            });

            Map<Long, Integer> playersMap = getStatistic(statistics, type);

            for (Map.Entry<Long, Integer> entry : playersMap.entrySet()) {
                Long playerId = entry.getKey();
                Integer goals = entry.getValue();
                allPlayersWithSpecificStatistic.merge(playerId, goals, Integer::sum);
            }
        }
        return allPlayersWithSpecificStatistic.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private void crateColumnsHeaders(Sheet sheet, int rowStart, int colStart, FootballStatisticType type, CellStyle borderCellStyle) {
        Row row = sheet.getRow(rowStart);
        AtomicInteger col = new AtomicInteger(colStart);
        List<String> headers = List.of("", "Player", "Team", getStatisticName(type));
        headers.forEach(s -> ExcelUtil.createCell(row, col.getAndIncrement(), borderCellStyle, s));
    }

    private void setColumnsWidth(Sheet sheet, int colStart) {
        sheet.setColumnWidth(colStart++, PLACE_COLUMN_EXCEL_WIDTH * ExcelUtil.COLUMN_WIDTH_UNIT);
        sheet.setColumnWidth(colStart++, PLAYER_COLUMN_EXCEL_WIDTH * ExcelUtil.COLUMN_WIDTH_UNIT);
        sheet.setColumnWidth(colStart++, TEAM_COLUMN_EXCEL_WIDTH * ExcelUtil.COLUMN_WIDTH_UNIT);
        sheet.setColumnWidth(colStart, STATISTIC_COLUMN_EXCEL_WIDTH * ExcelUtil.COLUMN_WIDTH_UNIT);
    }

    private String getStatisticName(FootballStatisticType type) {
        switch (type) {
            case GOALS -> {
                return "Goals";
            }
            case YELLOW_CARDS -> {
                return "Yellow cards";
            }
            case RED_CARDS -> {
                return "Red cards";
            }
            default -> {
                return "Clean sheets";
            }
        }
    }

    private Map<Long, Integer> getStatistic(FootballStatistics statistics, FootballStatisticType type) {
        switch (type) {
            case GOALS -> {
                return statistics.getPlayersIdWithGoal();
            }
            case YELLOW_CARDS -> {
                return statistics.getPlayersIdWithYellowCard();
            }
            case RED_CARDS -> {
                return statistics.getPlayersIdWithRedCard();
            }
            default -> {
                return statistics.getPlayersIdWithCleanSheets();
            }
        }
    }
}
