package com.example.tournamentmaker.util;

import com.example.tournamentmaker.statistics.FootballStatistics;
import com.example.tournamentmaker.statistics.Statistics;
import com.example.tournamentmaker.tournament.Tournament;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StatisticUtil {
    public static Statistics createStatistics(int countWins, int countLoses, int countDraws, int points) {
        return new Statistics(null, countWins, countLoses, countDraws, points);
    }

    public static void createAllFootballStatisticsForTournament(Tournament tournament) {
        tournament.getTeamList().forEach(team -> {
            FootballStatistics statistics = new FootballStatistics(team);
            team.setStatistics(statistics);
        });
    }
}
