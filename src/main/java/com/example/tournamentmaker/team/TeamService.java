package com.example.tournamentmaker.team;

import com.example.tournamentmaker.constans.Constans;
import com.example.tournamentmaker.statistics.FootballStatistics;
import com.example.tournamentmaker.team.exception.TournamentRegistrationException;
import com.example.tournamentmaker.team.player.FootballPlayer;
import com.example.tournamentmaker.team.player.FootballPosition;
import com.example.tournamentmaker.team.player.PlayerRepository;
import com.example.tournamentmaker.tournament.Tournament;
import com.example.tournamentmaker.tournament.TournamentRepository;
import com.example.tournamentmaker.tournament.enums.Sport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class TeamService {
    private final TeamRepository teamRepository;
    private final TournamentRepository tournamentRepository;
    private final PlayerRepository playerRepository;

    void createTeam(TeamRequest teamRequest) {
        Optional<Tournament> tournament = tournamentRepository.findByName(teamRequest.tournamentName());
        tournament.ifPresentOrElse(t -> {
            if (t.isRegistrationCompleted()) {
                throw new TournamentRegistrationException("Registration for this tournament is now closed");
            }
            Sport sport = t.getSport();
            if (sport == Sport.FOOTBALL) {
                createTeamWithStatistics(teamRequest.teamName(), t);
            }
        }, () -> {
            throw new NoSuchElementException(Constans.NO_TOURNAMENT_FOUND);
        });
    }

    void addFootballPlayer(FootballPlayerRequest request) {
        Optional<Team> team = teamRepository.findByName(request.getTeamName());
        team.ifPresentOrElse(t -> {
            if (isThereNoPlayerWithNumber(t, request.getJerseyNumber())) {
                createFootballPlayer(request.getFirstName(), request.getLastName(), t, request.getJerseyNumber(),
                        request.getFootballPosition());
                teamRepository.save(t);
            } else
                throw new IllegalArgumentException("Player with" + request.getJerseyNumber() + "already exist in team");

        }, () -> {
            throw new NoSuchElementException(Constans.NO_TOURNAMENT_FOUND);
        });
    }

    void createFootballTeamsWithPlayers(FootballTeamsAndPlayersRequest request) {
        Optional<Tournament> tournament = tournamentRepository.findByName(request.tournamentName());
        tournament.ifPresentOrElse(t -> {
            if (t.isRegistrationCompleted()) {
                throw new TournamentRegistrationException("Registration for this tournament is now closed");
            }
            for (FootballTeamRequest teamRequest : request.teams()) {
                Team team = createTeamWithStatistics(teamRequest.teamName(), t);

                for (FootballPlayerRequestWithoutGivingTeamName playerRequest : teamRequest.players()) {
                    if (isThereNoPlayerWithNumber(team, playerRequest.jerseyNumber())) {
                        createFootballPlayer(playerRequest.firstName(), playerRequest.lastName(), team,
                                playerRequest.jerseyNumber(), playerRequest.footballPosition());
                    } else
                        throw new IllegalArgumentException("Player with" + playerRequest.jerseyNumber() + "already exist in team");
                }
                teamRepository.save(team);
            }
        }, () -> {
            throw new NoSuchElementException(Constans.NO_TOURNAMENT_FOUND);
        });
    }

    private boolean isThereNoPlayerWithNumber(Team team, Integer jerseyNumber) {
        return team.getPlayers().stream()
                .map(FootballPlayer.class::cast)
                .noneMatch(footballPlayer -> Objects.equals(footballPlayer.getJerseyNumber(), jerseyNumber));
    }

    private Team createTeamWithStatistics(String teamName, Tournament tournament) {
        Team team = new Team(teamName, tournament);
        FootballStatistics footballStatistics = new FootballStatistics(team);
        team.setStatistics(footballStatistics);
        teamRepository.save(team);
        return team;
    }

    private void createFootballPlayer(String firstName, String lastName, Team team, int jerseyNumber, FootballPosition position) {
        FootballPlayer player = new FootballPlayer(firstName, lastName, team, jerseyNumber, position);
        playerRepository.save(player);
        team.getPlayers().add(player);
    }
}
