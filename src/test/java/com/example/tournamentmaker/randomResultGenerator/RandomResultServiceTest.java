package com.example.tournamentmaker.randomResultGenerator;

import com.example.tournamentmaker.statistics.FootballStatistics;
import com.example.tournamentmaker.statistics.FootballStatisticsRepository;
import com.example.tournamentmaker.team.Team;
import com.example.tournamentmaker.team.TeamRepository;
import com.example.tournamentmaker.team.player.FootballPlayer;
import com.example.tournamentmaker.team.player.FootballPosition;
import com.example.tournamentmaker.team.player.Player;
import com.example.tournamentmaker.tournament.Tournament;
import com.example.tournamentmaker.tournament.TournamentRepository;
import com.example.tournamentmaker.tournament.enums.TournamentType;
import com.example.tournamentmaker.tournament.game.Game;
import com.example.tournamentmaker.tournament.game.GameRepository;
import com.example.tournamentmaker.tournament.result.ResultService;
import com.example.tournamentmaker.tournament.round.CupRoundService;
import com.example.tournamentmaker.tournament.round.Round;
import com.example.tournamentmaker.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
    @Mock
    private CupRoundService cupRoundService;
    @InjectMocks
    private RandomResultService randomResultService;

    @Test
    void drawLotRoundsResults() {
        // given
        Tournament tournament = Util.createTournament();
        String tournamentName = tournament.getName();
        tournament.setTournamentType(TournamentType.LEAGUE);
        final int teamNumber = 3;
        Util.createTeams(tournament, teamNumber);
        Team exampleTeam = tournament.getTeamList().get(0);
        createPlayersAtEveryPosition(exampleTeam);
        createRoundWithOneGame(1L, 2L, tournament);
        createRoundWithOneGame(1L, 3L, tournament);
        createRoundWithOneGame(2L, 3L, tournament);
        String roundsToDraw = "1-3";
        // when
        when(tournamentRepository.findByName(tournamentName)).thenReturn(Optional.of(tournament));
        when(footballStatisticsRepository.findByTeamId(any())).thenReturn(Optional.of(new FootballStatistics()));
        when(teamRepository.findById(any())).thenReturn(Optional.of(exampleTeam));
        randomResultService.drawLotRoundsResults(new RandomResultRequest(tournamentName, roundsToDraw));
        // then
        Integer examplePoints = tournament.getRounds().get(2).getGames().get(0).getHostPoints();
        boolean hasResultBeenDrawn = (examplePoints >= 0) && (examplePoints <= 9);
        Assertions.assertTrue(hasResultBeenDrawn);
    }

    private void createRoundWithOneGame(long hostId, long guestId, Tournament tournament) {
        Round round = new Round();
        round.getGames().add(new Game(hostId, guestId, round));
        tournament.getRounds().add(round);
    }

    private void createPlayersAtEveryPosition(Team team) {
        List<Player> players = team.getPlayers();
        players.addAll(List.of(
                createPlayerAtPosition(FootballPosition.GOALKEEPER, team, 1),
                createPlayerAtPosition(FootballPosition.DEFENDER, team, 2),
                createPlayerAtPosition(FootballPosition.MIDFIELDER, team, 10),
                createPlayerAtPosition(FootballPosition.FORWARD, team, 9))
        );
    }

    private FootballPlayer createPlayerAtPosition(FootballPosition position, Team team, int jerseyNumber) {
        return new FootballPlayer("firstName", "lastName", team, jerseyNumber, position);
    }

    @Test
    void shouldContainExceptionWhenTournamentNameNotFound() {
        //given
        String name = "There is no such tournament";
        String roundsToDraw = "1-3";
        //when
        Assertions.assertThrows(NoSuchElementException.class,
                () -> randomResultService.drawLotRoundsResults(new RandomResultRequest(name, roundsToDraw)
                ));
    }
}