package com.example.tournamentmaker.tournament.result;

import com.example.tournamentmaker.statistics.FootballStatistics;
import com.example.tournamentmaker.statistics.FootballStatisticsRepository;
import com.example.tournamentmaker.statistics.MatchResult;
import com.example.tournamentmaker.statistics.Statistics;
import com.example.tournamentmaker.team.Team;
import com.example.tournamentmaker.team.TeamRepository;
import com.example.tournamentmaker.team.player.FootballPlayer;
import com.example.tournamentmaker.team.player.PlayerRepository;
import com.example.tournamentmaker.tournament.Tournament;
import com.example.tournamentmaker.tournament.TournamentRepository;
import com.example.tournamentmaker.tournament.game.Game;
import com.example.tournamentmaker.tournament.game.GameRepository;
import com.example.tournamentmaker.tournament.round.Round;
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

import static com.example.tournamentmaker.tournament.result.ResultServiceTestData.createFootballResultRequest;
import static com.example.tournamentmaker.util.PlayerUtil.createFootballPlayer;
import static com.example.tournamentmaker.util.StatisticUtil.createStatistics;
import static com.example.tournamentmaker.util.TeamUtil.createTeam;
import static com.example.tournamentmaker.util.TeamUtil.createTeamInNewTournament;
import static com.example.tournamentmaker.util.TournamentUtil.createTournament;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResultServiceTest {
    @InjectMocks
    private ResultService resultService;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private GameRepository gameRepository;
    @Mock
    private FootballStatisticsRepository footballStatisticsRepository;

    @Test
    void shouldUpdateGoalsCount() {
        //given
        int hostPoints = 3;
        int guestPoints = 2;
        FootballStatistics hostStatistics = new FootballStatistics(createTeamInNewTournament("Team A"));
        FootballStatistics guestStatistics = new FootballStatistics(createTeamInNewTournament("Team B"));
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
        Team team = createTeamInNewTournament("Team A");
        FootballPlayer player1 = createFootballPlayer(1L, "Jack", team, 5);
        FootballPlayer player2 = createFootballPlayer(3L, "Will", team, 7);
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
        Team team = createTeamInNewTournament("Team A");
        FootballPlayer player = createFootballPlayer(1L, "Jack", team, 5);
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
        Team team = createTeamInNewTournament("Team A");
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

    @Test
    void shouldLaunchFootballResult() {
        // given
        Tournament tournament = createTournament();
        String tournamentName = tournament.getName();
        String hostName = "Host";
        String guestName = "Guest";
        Team hostTeam = createTeam(1L, hostName, tournament);
        Team guestTeam = createTeam(2L, guestName, tournament);
        int turnNumber = 1;
        Round round = new Round(turnNumber, tournament);
        tournament.getRounds().add(round);
        round.getGames().add(new Game(hostTeam.getId(), guestTeam.getId(), round));

        FootballStatistics hostStatistics = new FootballStatistics(hostTeam);
        FootballStatistics guestStatistics = new FootballStatistics(guestTeam);
        FootballPlayer hostPlayer1 = createFootballPlayer(1L, "Jack", hostTeam, 1);
        FootballPlayer hostPlayer2 = createFootballPlayer(2L, "Will", hostTeam, 7);
        FootballPlayer guestPlayer1 = createFootballPlayer(3L, "Kevin", guestTeam, 1);
        FootballPlayer guestPlayer2 = createFootballPlayer(4L, "Mike", guestTeam, 10);

        FootballResultRequest request = createFootballResultRequest(tournamentName, turnNumber, hostName, guestName);
        // when
        when(tournamentRepository.findByName(tournamentName)).thenReturn(Optional.of(tournament));
        when(teamRepository.findByNameAndTournamentName(hostName, tournamentName)).thenReturn(Optional.of(hostTeam));
        when(teamRepository.findByNameAndTournamentName(guestName, tournamentName)).thenReturn(Optional.of(guestTeam));
        when(footballStatisticsRepository.findByTeamId(hostTeam.getId())).thenReturn(Optional.of(hostStatistics));
        when(footballStatisticsRepository.findByTeamId(guestTeam.getId())).thenReturn(Optional.of(guestStatistics));
        when(playerRepository.findByJerseyNumberAndTeam(1, hostTeam)).thenReturn(Optional.of(hostPlayer1));
        when(playerRepository.findByJerseyNumberAndTeam(7, hostTeam)).thenReturn(Optional.of(hostPlayer2));
        when(playerRepository.findByJerseyNumberAndTeam(1, guestTeam)).thenReturn(Optional.of(guestPlayer1));
        when(playerRepository.findByJerseyNumberAndTeam(10, guestTeam)).thenReturn(Optional.of(guestPlayer2));
        resultService.launchFootballResult(request);
        // then
        verify(footballStatisticsRepository, times(2)).save(any(FootballStatistics.class));
        verify(gameRepository, times(1)).save(any(Game.class));
    }
}