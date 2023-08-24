package com.example.tournamentmaker.team;

import com.example.tournamentmaker.constans.Constans;
import com.example.tournamentmaker.statistics.FootballStatistics;
import com.example.tournamentmaker.team.exception.TournamentRegistrationException;
import com.example.tournamentmaker.team.player.FootballPlayer;
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
        Optional<Tournament> tournament = tournamentRepository.findByName(teamRequest.getTournamentName());
        tournament.ifPresentOrElse(t -> {
            if (t.isRegistrationCompleted())
                throw new TournamentRegistrationException("Registration for this tournament is now closed");
            else {
                Sport sport = t.getSport();
                switch (sport) {
                    case FOOTBALL -> {
                        Team team = new Team(teamRequest.getTeamName(), t);
                        FootballStatistics footballStatistics = new FootballStatistics(team);
                        team.setStatistics(footballStatistics);
                        teamRepository.save(team);
                    }
                    case BASKETBALL -> {
                        //TODO
                    }
                }
            }

        }, () -> {
            throw new NoSuchElementException(Constans.NO_TOURNAMENT_FOUND);
        });
    }

    void addFootballPlayer(FootballPlayerRequest request) {
        Optional<Team> team = teamRepository.findByName(request.getTeamName());
        team.ifPresentOrElse(t -> {
            if (!isPlayerWithNumber(t, request.getJerseyNumber())) {
                FootballPlayer player = new FootballPlayer(request.getFirstName(), request.getLastName(),
                        t, request.getJerseyNumber(), request.getFootballPosition());
                playerRepository.save(player);
                t.getPlayers().add(player);
                teamRepository.save(t);
            } else
                throw new IllegalArgumentException("Player with" + request.getJerseyNumber() + "already exist in team");

        }, () -> {
            throw new NoSuchElementException(Constans.NO_TOURNAMENT_FOUND);
        });
    }

    private boolean isPlayerWithNumber(Team team, Integer jerseyNumber) {
        return team.getPlayers().stream()
                .map(player -> (FootballPlayer) player)
                .anyMatch(footballPlayer -> Objects.equals(footballPlayer.getJerseyNumber(), jerseyNumber));
    }

    public void createFootballTeamsWithPlayers(FootballTeamsAndPlayersRequest request) {
        Optional<Tournament> tournament = tournamentRepository.findByName(request.getTournamentName());
        tournament.ifPresentOrElse(t -> {
            if (t.isRegistrationCompleted())
                throw new TournamentRegistrationException("Registration for this tournament is now closed");
            else {
                for (FootballTeamRequest teamRequest : request.getTeams()) {
                    Team team = new Team(teamRequest.getTeamName(), t);
                    FootballStatistics footballStatistics = new FootballStatistics(team);
                    team.setStatistics(footballStatistics);
                    teamRepository.save(team);

                    for (FootballPlayerRequestWithoutGivingTeamName playerRequest : teamRequest.getPlayers()) {
                        if (!isPlayerWithNumber(team, playerRequest.getJerseyNumber())) {
                            FootballPlayer player = new FootballPlayer(playerRequest.getFirstName(), playerRequest.getLastName(),
                                    team, playerRequest.getJerseyNumber(), playerRequest.getFootballPosition());
                            playerRepository.save(player);
                            team.getPlayers().add(player);
                        } else
                            throw new IllegalArgumentException("Player with" + playerRequest.getJerseyNumber() + "already exist in team");
                    }
                    teamRepository.save(team);
                }
            }
        }, () -> {
            throw new NoSuchElementException(Constans.NO_TOURNAMENT_FOUND);
        });
    }
}
