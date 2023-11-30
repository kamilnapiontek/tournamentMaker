package com.example.tournamentmaker.statistics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.example.tournamentmaker.statistics.StatisticUtil.getRecentMatchResultsString;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class StatisticUtilTest {

    @Test
    void shouldReturnFirstLettersOfLastMatches() {
        // given
        Statistics statistics = new Statistics(null, 0, 0, 0, 0);
        statistics.setRecentMatchResults(List.of(
                MatchResult.WIN, MatchResult.WIN, MatchResult.LOSE, MatchResult.WIN, MatchResult.LOSE));
        // when
        String resultsString = getRecentMatchResultsString(statistics);
        // then
        assertEquals("W W L W L", resultsString);
    }
}