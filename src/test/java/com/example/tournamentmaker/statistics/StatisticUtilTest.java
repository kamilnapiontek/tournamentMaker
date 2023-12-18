package com.example.tournamentmaker.statistics;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.example.tournamentmaker.statistics.StatisticUtil.getRecentMatchResultsString;
import static com.example.tournamentmaker.util.StatisticUtil.createStatistics;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticUtilTest {

    @Test
    void shouldReturnFirstLettersOfLastMatches() {
        // given
        final Statistics statistics = createStatistics(0, 0, 0, 0);
        statistics.setRecentMatchResults(List.of(
                MatchResult.WIN, MatchResult.WIN, MatchResult.LOSE, MatchResult.WIN, MatchResult.LOSE));
        // when
        String resultsString = getRecentMatchResultsString(statistics);
        // then
        assertEquals("W W L W L", resultsString);
    }
}