package com.example.tournamentmaker.statistics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class StatisticServiceTest {
    @InjectMocks
    private StatisticService statisticService;

    @Test
    void shouldReturnFirstLettersOfLastMatches() {
        // given
        Statistics statistics = new Statistics(null, 0, 0, 0, 0);
        List<MatchResult> resultList = List.of(MatchResult.WIN, MatchResult.WIN,
                MatchResult.LOSE, MatchResult.WIN, MatchResult.LOSE);
        statistics.setRecentMatchResults(List.of(MatchResult.WIN, MatchResult.WIN,
                MatchResult.LOSE, MatchResult.WIN, MatchResult.LOSE));
        // when
        String resultsString = statisticService.getRecentMatchResultsString(statistics);
        // then
        Assertions.assertEquals("W W L W L", resultsString);
    }
}