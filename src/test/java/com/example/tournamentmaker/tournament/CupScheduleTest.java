package com.example.tournamentmaker.tournament;

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

import static com.example.tournamentmaker.util.Util.createTeams;
import static com.example.tournamentmaker.util.Util.createTournament;

@ExtendWith(MockitoExtension.class)
class CupScheduleTest {
    @InjectMocks
    private CupSchedule cupSchedule;
    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private RoundRepository roundRepository;

    @ParameterizedTest
    @MethodSource("resultsData")
    void shouldCreateCupSchedule(int teamsNumber, int expectedRoundsNumber) {
        // given
        Tournament tournament = createTournament();
        createTeams(tournament, teamsNumber);
        // when
        cupSchedule.createSchedule(tournament);
        // then
        Assertions.assertEquals(expectedRoundsNumber, tournament.getRounds().size());
    }

    private static Stream<Arguments> resultsData() {
        return Stream.of(
                Arguments.of(4, 2),
                Arguments.of(5, 3),
                Arguments.of(16, 4),
                Arguments.of(17, 5),
                Arguments.of(153, 8)
        );
    }
}