package com.example.tournamentmaker.randomResultGenerator;

import com.example.tournamentmaker.statistics.FootballStatistics;
import com.example.tournamentmaker.statistics.FootballStatisticsRepository;
import com.example.tournamentmaker.team.Team;
import com.example.tournamentmaker.team.TeamRepository;
import com.example.tournamentmaker.tournament.Tournament;
import com.example.tournamentmaker.tournament.TournamentRepository;
import com.example.tournamentmaker.tournament.enums.TournamentType;
import com.example.tournamentmaker.tournament.game.GameRepository;
import com.example.tournamentmaker.tournament.result.ResultService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static com.example.tournamentmaker.constans.Constans.NO_TOURNAMENT_FOUND;
import static com.example.tournamentmaker.randomResultGenerator.RandomResultServiceTestData.createPlayersAtEveryPosition;
import static com.example.tournamentmaker.randomResultGenerator.RandomResultServiceTestData.createRounds;
import static com.example.tournamentmaker.util.TeamUtil.createTeams;
import static com.example.tournamentmaker.util.TournamentUtil.createTournament;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RandomResultServiceTest {
    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private GameRepository gameRepository;
    @Mock
    private FootballStatisticsRepository footballStatisticsRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private ResultService resultService;
    @InjectMocks
    private RandomResultService randomResultService;
    private static final String ROUNDS_TO_DRAW = "1-3";

    @Test
    void shouldContainExceptionWhenTournamentNameNotFound() {
        //given
        String name = "There is no such tournament";
        //when
        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> randomResultService.drawLotRoundsResults(new RandomResultRequest(name, ROUNDS_TO_DRAW)
                ));
        assertEquals(NO_TOURNAMENT_FOUND, exception.getMessage());
    }

    @Test
    void drawLotRoundsResults() {
        // given
        Tournament tournament = createTournament();
        String tournamentName = tournament.getName();
        tournament.setTournamentType(TournamentType.LEAGUE);
        final int teamNumber = 3;
        createTeams(tournament, teamNumber);
        Team exampleTeam = tournament.getTeamList().get(0);
        createPlayersAtEveryPosition(exampleTeam);
        createRounds(tournament);
        RandomResultRequest request = new RandomResultRequest(tournamentName, ROUNDS_TO_DRAW);
        // when
        when(tournamentRepository.findByName(tournamentName)).thenReturn(Optional.of(tournament));
        when(footballStatisticsRepository.findByTeamId(any())).thenReturn(Optional.of(new FootballStatistics()));
        when(teamRepository.findById(any())).thenReturn(Optional.of(exampleTeam));
        randomResultService.drawLotRoundsResults(request);
        // then
        Integer examplePoints = tournament.getRounds().get(2).getGames().get(0).getHostPoints();
        boolean hasResultBeenDrawn = (examplePoints >= 0) && (examplePoints <= 9);
        Assertions.assertTrue(hasResultBeenDrawn);
        verify(teamRepository, times(6)).findById(any());
    }
}