package com.example.tournamentmaker.team;

import com.example.tournamentmaker.team.exception.TournamentRegistrationException;
import com.example.tournamentmaker.team.player.Player;
import com.example.tournamentmaker.team.player.PlayerRepository;
import com.example.tournamentmaker.tournament.Tournament;
import com.example.tournamentmaker.tournament.TournamentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static com.example.tournamentmaker.team.TeamServiceTestData.createFootballPlayerRequest;
import static com.example.tournamentmaker.team.TeamServiceTestData.getFootballTeamsAndPlayersRequest;
import static com.example.tournamentmaker.util.TournamentUtil.creteTournament;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {
    @InjectMocks
    private TeamService teamService;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private PlayerRepository playerRepository;
    private static final String EXAMPLE_TOURNAMENT_NAME = "Tournament";
    private static final String EXAMPLE_TEAM_NAME = "FC Barcelona";
    private static final int EXAMPLE_NUMBER = 7;

    @Test
    void shouldCreateTeam() {
        // given
        final String tournamentName = EXAMPLE_TOURNAMENT_NAME;
        Optional<Tournament> tournament = Optional.of(creteTournament(tournamentName));
        TeamRequest request = new TeamRequest(tournamentName, EXAMPLE_TEAM_NAME);
        // when
        when(tournamentRepository.findByName(tournamentName)).thenReturn(tournament);
        teamService.createTeam(request);
        // then
        verify(teamRepository, times(1)).save(any(Team.class));
    }

    @Test
    void shouldThrowExceptionWhenTournamentDoesNotExist_createTeam() {
        // given
        final String tournamentName = "NonExistentTournament";
        // when
        Assertions.assertThrows(NoSuchElementException.class, () ->
                teamService.createTeam(new TeamRequest(tournamentName, EXAMPLE_TEAM_NAME))
        );
    }

    @Test
    void shouldThrowExceptionWhenRegistrationClosed() {
        // given
        final String tournamentName = EXAMPLE_TOURNAMENT_NAME;
        Optional<Tournament> tournament = Optional.of(creteTournament(tournamentName));
        tournament.get().setRegistrationCompleted(true);
        // when
        when(tournamentRepository.findByName(tournamentName)).thenReturn(tournament);
        Assertions.assertThrows(
                TournamentRegistrationException.class, () ->
                        teamService.createTeam(new TeamRequest(tournamentName, EXAMPLE_TEAM_NAME))
        );
    }

    @Test
    void shouldAddFootballPlayer() {
        // given
        final String teamName = EXAMPLE_TEAM_NAME;
        final Tournament tournament = creteTournament(EXAMPLE_TOURNAMENT_NAME);
        // when
        when(teamRepository.findByName(teamName)).thenReturn(Optional.of(new Team(teamName, tournament)));
        teamService.addFootballPlayer(createFootballPlayerRequest(teamName, EXAMPLE_NUMBER));
        // then
        verify(playerRepository, times(1)).save(any(Player.class));
    }

    @Test
    void shouldThrowExceptionWhenPlayerWithJerseyNumberAlreadyExistInTeam() {
        // given
        final String teamName = EXAMPLE_TEAM_NAME;
        final Tournament tournament = creteTournament(EXAMPLE_TOURNAMENT_NAME);
        // when
        when(teamRepository.findByName(teamName)).thenReturn(Optional.of(new Team(teamName, tournament)));
        teamService.addFootballPlayer(createFootballPlayerRequest(teamName, EXAMPLE_NUMBER));
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                teamService.addFootballPlayer(createFootballPlayerRequest(teamName, EXAMPLE_NUMBER)));
    }

    @Test
    void shouldThrowExceptionWhenTournamentDoesNotExist_addFootballPlayer() {
        // given
        FootballPlayerRequest request = createFootballPlayerRequest(EXAMPLE_TEAM_NAME, EXAMPLE_NUMBER);
        // when
        Assertions.assertThrows(NoSuchElementException.class, () ->
                teamService.addFootballPlayer(request));
    }

    @Test
    void shouldCreateFootballTeamsWithPlayers() {
        // given
        final String tournamentName = EXAMPLE_TOURNAMENT_NAME;
        final Tournament tournament = creteTournament(tournamentName);
        FootballTeamsAndPlayersRequest request = getFootballTeamsAndPlayersRequest(tournament);
        // when
        when(tournamentRepository.findByName(tournamentName)).thenReturn(Optional.of(tournament));
        teamService.createFootballTeamsWithPlayers(request);
        // then
        Assertions.assertAll(
                () -> verify(teamRepository, times(4)).save(any(Team.class)),
                () -> verify(playerRepository, times(4)).save(any(Player.class))
        );
    }

    @Test
    void shouldThrowExceptionWhenTournamentDoesNotExist_createFootballTeamsWithPlayers() {
        // given
        final String tournamentName = "NonExistentTournament";
        // when
        Assertions.assertThrows(NoSuchElementException.class, () ->
                teamService.createFootballTeamsWithPlayers(
                        getFootballTeamsAndPlayersRequest(creteTournament(tournamentName)))
        );
    }
}