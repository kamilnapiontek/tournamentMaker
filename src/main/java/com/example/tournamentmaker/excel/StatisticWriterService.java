package com.example.tournamentmaker.excel;

import com.example.tournamentmaker.constans.Constans;
import com.example.tournamentmaker.statistics.FootballStatistics;
import com.example.tournamentmaker.statistics.FootballStatisticsRepository;
import com.example.tournamentmaker.team.Team;
import com.example.tournamentmaker.team.player.FootballPlayer;
import com.example.tournamentmaker.team.player.PlayerRepository;
import com.example.tournamentmaker.tournament.Tournament;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class StatisticWriterService {
    private final PlayerRepository playerRepository;
    private final FootballStatisticsRepository footballStatisticsRepository;
    private static final int MAX_ROWS_AMOUNT = 11;

    void writeStatistic(Sheet sheet, int rowStart, int colStart, Tournament tournament) {
        ExcelUtil.createRows(sheet, MAX_ROWS_AMOUNT);
        List<Team> teamList = tournament.getTeamList();

        createStatisticTable(sheet, rowStart, colStart, teamList);
    }

    private void createStatisticTable(Sheet sheet, int rowStart, int colStart, List<Team> teamList) {
        Map<Long, Integer> allGoalScorers = new HashMap<>();

        for (Team team : teamList) {
            FootballStatistics stats = footballStatisticsRepository.findByTeamId(team.getId()).orElseThrow(() -> {
                throw new NoSuchElementException(Constans.NO_TEAM_FOUND);
            });
            Map<Long, Integer> playersGoalMap = stats.getPlayersIdWithGoal();

            for (Map.Entry<Long, Integer> entry : playersGoalMap.entrySet()) {
                Long playerId = entry.getKey();
                Integer goals = entry.getValue();
                allGoalScorers.merge(playerId, goals, Integer::sum);
            }
        }
        Map<Long, Integer> topTen = allGoalScorers.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        for (Long playerId : topTen.keySet()) {
            FootballPlayer player = playerRepository.findById(playerId).orElseThrow(
                    () -> {
                        throw new NoSuchElementException("Can't find player with given id");
                    }
            );
            Row row = sheet.getRow(rowStart);

            Cell cell = row.createCell(colStart);
            cell.setCellValue(player.getFirstName() + " " + player.getLastName());

            cell = row.createCell(colStart + 1);
            cell.setCellValue(player.getTeam().getName());

            cell = row.createCell(colStart + 2);
            cell.setCellValue(topTen.get(playerId));

            rowStart++;
        }
    }
}
