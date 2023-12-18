package com.example.tournamentmaker.tournament;

import com.example.tournamentmaker.tournament.game.Game;
import com.example.tournamentmaker.tournament.round.Round;
import com.example.tournamentmaker.tournament.round.RoundRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static com.example.tournamentmaker.util.TeamUtil.createTeams;
import static com.example.tournamentmaker.util.TournamentUtil.createTournament;

@ExtendWith(MockitoExtension.class)
class LeagueScheduleTest {
    @InjectMocks
    private LeagueSchedule leagueSchedule;
    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private RoundRepository roundRepository;

    @ParameterizedTest
    @MethodSource("resultsDataWhenEvenTeamsNumber")
    void shouldCreateLeagueScheduleWhenEvenTeamsNumber(final int roundNumber, final int gameNumber,
                                                       final long expectedHostId, final long expectedGuestId) {
        // given
        final Tournament tournament = createTournament();
        final int teamsNumber = 4;
        createTeams(tournament, teamsNumber);
        // when
        leagueSchedule.createSchedule(tournament);
        // then
        final Round round = tournament.getRounds().get(roundNumber);
        final Game game = round.getGames().get(gameNumber);
        Assertions.assertEquals(expectedHostId, game.getHostId());
        Assertions.assertEquals(expectedGuestId, game.getGuestId());
    }

    private static Stream<Arguments> resultsDataWhenEvenTeamsNumber() {
        return Stream.of(
                Arguments.of(0, 0, 2L, 1L),
                Arguments.of(0, 1, 4L, 3L),
                Arguments.of(1, 0, 3L, 1L),
                Arguments.of(1, 1, 2L, 4L),
                Arguments.of(2, 0, 4L, 1L),
                Arguments.of(2, 1, 3L, 2L)
        );
    }

    @ParameterizedTest
    @MethodSource("resultsDataWhenOddTeamsNumber")
    void shouldCreateLeagueScheduleWhenOddTeamsNumber(int roundNumber, int gameNumber,
                                                      long expectedHostId, long expectedGuestId) {
        // given
        Tournament tournament = createTournament();
        int teamsNumber = 5;
        createTeams(tournament, teamsNumber);
        // when
        leagueSchedule.createSchedule(tournament);
        // then
        Round round = tournament.getRounds().get(roundNumber);
        Game game = round.getGames().get(gameNumber);
        Assertions.assertEquals(expectedHostId, game.getHostId());
        Assertions.assertEquals(expectedGuestId, game.getGuestId());
    }

    private static Stream<Arguments> resultsDataWhenOddTeamsNumber() {
        return Stream.of(
                Arguments.of(0, 0, 2L, 1L),
                Arguments.of(0, 1, 4L, 3L),
                Arguments.of(1, 0, 1L, 3L),
                Arguments.of(1, 1, 2L, 5L),
                Arguments.of(2, 0, 3L, 5L),
                Arguments.of(2, 1, 1L, 4L),
                Arguments.of(3, 0, 5L, 4L),
                Arguments.of(3, 1, 3L, 2L),
                Arguments.of(4, 0, 4L, 2L),
                Arguments.of(4, 1, 5L, 1L)
        );
    }
}