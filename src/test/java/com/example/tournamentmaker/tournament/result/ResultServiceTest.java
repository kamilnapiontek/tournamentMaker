package com.example.tournamentmaker.tournament.result;

import com.example.tournamentmaker.statistics.FootballStatistics;
import com.example.tournamentmaker.statistics.MatchResult;
import com.example.tournamentmaker.statistics.Statistics;
import com.example.tournamentmaker.team.Team;
import com.example.tournamentmaker.team.player.FootballPlayer;
import com.example.tournamentmaker.team.player.PlayerRepository;
import com.example.tournamentmaker.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

// RZUĆ OKIEM TUTAJ :)

@ExtendWith(MockitoExtension.class)
class ResultServiceTest {
    @InjectMocks
    private ResultService resultService;
    @Mock
    private PlayerRepository playerRepository;

    @Test
    void shouldUpdateGoalsCount() {
        //given
        int hostPoints = 3;
        int guestPoints = 2;
        FootballStatistics hostStatistics = new FootballStatistics(Util.createTeam("Team A"));
        FootballStatistics guestStatistics = new FootballStatistics(Util.createTeam("Team B"));
        //when
        resultService.updateGoalsCount(hostPoints, guestPoints, hostStatistics, guestStatistics);
        //then
        Assertions.assertAll(
                () -> assertEquals(hostPoints, hostStatistics.getGoalsScored()),
                () -> assertEquals(hostPoints, guestStatistics.getGoalsConceded()),
                () -> assertEquals(guestPoints, guestStatistics.getGoalsScored()),
                () -> assertEquals(guestPoints, hostStatistics.getGoalsConceded())
        );
    }

    @Test
    void shouldUpdateSpecificStatisticInTeam() {
        //given
        List<Integer> jerseyNumbersList = List.of(5, 5, 7);
        Team team = Util.createTeam("Team A");
        FootballPlayer player1 = Util.createFootballPlayer(1L, "Jack", team, 5);
        FootballPlayer player2 = Util.createFootballPlayer(3L, "Will", team, 7);
        Map<Long, Integer> specificStatistic = new HashMap<>();
        specificStatistic.put(1L, 1);
        specificStatistic.put(3L, 22);
        //when
        when(playerRepository.findByJerseyNumberAndTeam(5, team)).thenReturn(Optional.of(player1));
        when(playerRepository.findByJerseyNumberAndTeam(7, team)).thenReturn(Optional.of(player2));
        resultService.updateSpecificStatisticInTeam(jerseyNumbersList, team, specificStatistic);
        //then
        assertEquals(3, specificStatistic.get(player1.getId()));
        assertEquals(23, specificStatistic.get(player2.getId()));
    }

    @Test
    void shouldUpdateSpecificStatisticInTeamWhenNoPlayersInSpecificStatisticMap() {
        //given
        List<Integer> jerseyNumbersList = List.of(5, 5, 5);
        Team team = Util.createTeam("Team A");
        FootballPlayer player = Util.createFootballPlayer(1L, "Jack", team, 5);
        Map<Long, Integer> specificStatistic = new HashMap<>();
        //when
        when(playerRepository.findByJerseyNumberAndTeam(5, team)).thenReturn(Optional.of(player));
        resultService.updateSpecificStatisticInTeam(jerseyNumbersList, team, specificStatistic);
        //then
        int numberOfGoalsScoredByPlayerWithNumber5 = 3;
        assertEquals(numberOfGoalsScoredByPlayerWithNumber5, specificStatistic.get(player.getId()));
    }

    @Test
    void shouldContainExceptionWhenPlayerWithJerseyNumberNotFound() {
        //given
        List<Integer> jerseyNumbersList = List.of(5);
        Team team = Util.createTeam("Team A");
        //when
        Assertions.assertThrows(NoSuchElementException.class, () ->
                resultService.updateSpecificStatisticInTeam(jerseyNumbersList, team, new HashMap<>())
        );
    }

    @Test
    void shouldGetOpposingTeamResult() {
        // given
        MatchResult win = MatchResult.WIN;
        MatchResult draw = MatchResult.DRAW;
        MatchResult lose = MatchResult.LOSE;
        // when
        MatchResult whenWin = resultService.getOpposingTeamResult(win);
        MatchResult whenDraw = resultService.getOpposingTeamResult(draw);
        MatchResult whenLose = resultService.getOpposingTeamResult(lose);
        // then
        Assertions.assertAll(
                () -> assertEquals(MatchResult.LOSE, whenWin),
                () -> assertEquals(MatchResult.DRAW, whenDraw),
                () -> assertEquals(MatchResult.WIN, whenLose)
        );
    }

    @Test
    void shouldUpdateRecentResult() {
        // given
        MatchResult lastResult = MatchResult.DRAW;
        List<MatchResult> recentResults = new ArrayList<>(List.of(
                MatchResult.WIN, MatchResult.LOSE, MatchResult.LOSE, MatchResult.WIN, MatchResult.LOSE));
        // when
        resultService.updateRecentResult(lastResult, recentResults);
        // then
        List<MatchResult> updatedListExpected = List.of(
                MatchResult.DRAW, MatchResult.WIN, MatchResult.LOSE, MatchResult.LOSE, MatchResult.WIN);
        Assertions.assertArrayEquals(updatedListExpected.toArray(), recentResults.toArray());
    }

    @ParameterizedTest
    @MethodSource("resultsData")
    void shouldGetHostResult(int hostPoints, int guestPoints, MatchResult expectedResult) {
        // when
        MatchResult hostResult = resultService.getHostResult(hostPoints, guestPoints);
        // then
        Assertions.assertEquals(expectedResult, hostResult);
    }

    private static Stream<Arguments> resultsData() {
        return Stream.of(
                Arguments.of(3, 2, MatchResult.WIN),
                Arguments.of(5, 5, MatchResult.DRAW),
                Arguments.of(0, 4, MatchResult.LOSE)
        );
    }

    @Test
    void shouldAddResultOfTheMatchToStatisticsWhenHostWin() {
        // given
        MatchResult hostResult = MatchResult.WIN;
        Statistics hostStatistics = createStatistics(2, 6, 4, 10);
        Statistics guestStatistics = createStatistics(0, 0, 2, 2);
        // when
        resultService.addResultOfTheMatchToStatistics(hostResult, hostStatistics, guestStatistics);
        // then
        Assertions.assertAll(
                () -> assertEquals(3, hostStatistics.getCountWins()),
                () -> assertEquals(13, hostStatistics.getPoints()),
                () -> assertEquals(1, guestStatistics.getCountLoses()),
                () -> assertEquals(2, guestStatistics.getPoints())
        );
    }

    @Test
    void shouldAddResultOfTheMatchToStatisticsWhenHostDraw() {
        // given
        MatchResult hostResult = MatchResult.DRAW;
        Statistics hostStatistics = createStatistics(2, 6, 4, 10);
        Statistics guestStatistics = createStatistics(0, 0, 2, 2);
        // when
        resultService.addResultOfTheMatchToStatistics(hostResult, hostStatistics, guestStatistics);
        // then
        Assertions.assertAll(
                () -> assertEquals(5, hostStatistics.getCountDraws()),
                () -> assertEquals(11, hostStatistics.getPoints()),
                () -> assertEquals(3, guestStatistics.getCountDraws()),
                () -> assertEquals(3, guestStatistics.getPoints())
        );
    }

    private Statistics createStatistics(int countWins, int countLoses, int countDraws, int points) {
        return new Statistics(null, countWins, countLoses, countDraws, points);
    }
}