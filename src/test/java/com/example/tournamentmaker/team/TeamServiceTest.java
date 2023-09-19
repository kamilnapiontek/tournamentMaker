package com.example.tournamentmaker.team;

import com.example.tournamentmaker.team.exception.TournamentRegistrationException;
import com.example.tournamentmaker.team.player.FootballPosition;
import com.example.tournamentmaker.team.player.Player;
import com.example.tournamentmaker.team.player.PlayerRepository;
import com.example.tournamentmaker.tournament.Tournament;
import com.example.tournamentmaker.tournament.TournamentRepository;
import com.example.tournamentmaker.tournament.enums.Sport;
import com.example.tournamentmaker.tournament.enums.TournamentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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

    @Test
    void shouldCreateTeam() {
        // given
        final String tournamentName = "Tournament";
        final String teamName = "FC Barcelona";
        Optional<Tournament> tournament = Optional.of(createFootballCup(tournamentName));
        // when
        when(tournamentRepository.findByName(tournamentName)).thenReturn(tournament);
        teamService.createTeam(new TeamRequest(tournamentName, teamName));
        // then
        verify(teamRepository, times(1)).save(any(Team.class));
    }

    @Test
    void shouldThrowExceptionWhenTournamentDoesNotExist_createTeam() {
        // given
        final String tournamentName = "NonExistentTournament";
        final String teamName = "FC Barcelona";
        // when
        Assertions.assertThrows(NoSuchElementException.class, () ->
                teamService.createTeam(new TeamRequest(tournamentName, teamName))
        );
    }

    @Test
    void shouldThrowExceptionWhenRegistrationClosed() {
        // given
        final String tournamentName = "Tournament";
        final String teamName = "FC Barcelona";
        Optional<Tournament> tournament = Optional.of(createFootballCup(tournamentName));
        tournament.get().setRegistrationCompleted(true);
        // when
        when(tournamentRepository.findByName(tournamentName)).thenReturn(tournament);
        Assertions.assertThrows(
                TournamentRegistrationException.class, () ->
                        teamService.createTeam(new TeamRequest(tournamentName, teamName))
        );
    }

    @Test
    void shouldAddFootballPlayer() {
        // given
        final String teamName = "FC Barcelona";
        final Tournament tournament = createFootballCup("Tournament");
        // when
        when(teamRepository.findByName(teamName)).thenReturn(Optional.of(new Team(teamName, tournament)));
        teamService.addFootballPlayer(createFootballPlayerRequest(teamName, 7));
        // then
        verify(playerRepository, times(1)).save(any(Player.class));
    }

    @Test
    void shouldThrowExceptionWhenPlayerWithJerseyNumberAlreadyExistInTeam() {
        // given
        final String teamName = "FC Barcelona";
        final Tournament tournament = createFootballCup("Tournament");
        // when
        when(teamRepository.findByName(teamName)).thenReturn(Optional.of(new Team(teamName, tournament)));
        teamService.addFootballPlayer(createFootballPlayerRequest(teamName, 7));
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                teamService.addFootballPlayer(createFootballPlayerRequest(teamName, 7)));
    }

    @Test
    void shouldThrowExceptionWhenTournamentDoesNotExist_addFootballPlayer() {
        // given
        final String teamName = "FC Barcelona";
        // when
        Assertions.assertThrows(NoSuchElementException.class, () ->
                teamService.addFootballPlayer(createFootballPlayerRequest(teamName, 7)));
    }

    @Test
    void shouldCreateFootballTeamsWithPlayers() {
        // given
        final String tournamentName = "Tournament";
        final Tournament tournament = createFootballCup(tournamentName);
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
                        getFootballTeamsAndPlayersRequest(createFootballCup(tournamentName)))
        );
    }

    private FootballTeamsAndPlayersRequest getFootballTeamsAndPlayersRequest(Tournament tournament) {
        return new FootballTeamsAndPlayersRequest(tournament.getName(),
                List.of(
                        createFootballTeamRequest("FC Barcelona", List.of(
                                createFootballPlayer("Lionel", "Messi", 10, FootballPosition.FORWARD),
                                createFootballPlayer("Andrés", "Iniesta", 8, FootballPosition.MIDFIELDER))),
                        createFootballTeamRequest("Real Madrid", List.of(
                                createFootballPlayer("Cristiano", "Ronaldo", 7, FootballPosition.FORWARD),
                                createFootballPlayer("Iker", "Casillas", 1, FootballPosition.GOALKEEPER)))));
    }

    private FootballTeamRequest createFootballTeamRequest(String teamName, List<FootballPlayerRequestWithoutGivingTeamName> list) {
        return new FootballTeamRequest(teamName, list);
    }

    private FootballPlayerRequestWithoutGivingTeamName createFootballPlayer(
            String firstName, String lastName, Integer jerseyNumber, FootballPosition position) {
        return new FootballPlayerRequestWithoutGivingTeamName(firstName, lastName, jerseyNumber, position);
    }

    private Tournament createFootballCup(String tournamentName) {
        return new Tournament(tournamentName, TournamentType.CUP, Sport.FOOTBALL);
    }

    private FootballPlayerRequest createFootballPlayerRequest(String teamName, int jerseyNumber) {
        return new FootballPlayerRequest(
                teamName, "John", "Snow", jerseyNumber, "MIDFIELDER");
    }
}