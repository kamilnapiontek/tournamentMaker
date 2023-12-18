package com.example.tournamentmaker.statistics;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StatisticUtil {
    public static int getGamesCount(Statistics statistics) {
        return statistics.getCountWins() + statistics.getCountDraws() + statistics.getCountLoses();
    }

    public static String getRecentMatchResultsString(Statistics statistics) {
        StringBuilder resultsString = new StringBuilder();
        List<MatchResult> recentMatchResults = statistics.getRecentMatchResults();
        recentMatchResults.forEach(result -> {
            resultsString.append(getFirstLetter(result));
            resultsString.append(" ");
        });
        return resultsString.toString().trim();
    }

    private static String getFirstLetter(MatchResult result) {
        switch (result) {
            case WIN -> {
                return "W";
            }
            case LOSE -> {
                return "L";
            }
            default -> {
                return "D";
            }
        }
    }
}
