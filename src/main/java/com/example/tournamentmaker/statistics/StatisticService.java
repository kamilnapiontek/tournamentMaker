package com.example.tournamentmaker.statistics;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
public class StatisticService {
    public int getGamesCount(Statistics statistics) {
        return statistics.getCountWins() + statistics.getCountDraws() + statistics.getCountLoses();
    }
    public String getRecentMatchResultsString(Statistics statistics) {
        StringBuilder resultsString = new StringBuilder();
        List<MatchResult> recentMatchResults = statistics.getRecentMatchResults();
        recentMatchResults.forEach(result -> {
            resultsString.append(getFirstLetter(result));
            resultsString.append(" ");
        });
        return resultsString.toString().trim();
    }

    private String getFirstLetter(MatchResult result) {
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
