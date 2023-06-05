package com.example.tournamentMaker.team;

import com.example.tournamentMaker.statistics.FootballStatistics;
import com.example.tournamentMaker.team.player.FootballPlayer;
import com.example.tournamentMaker.team.player.PlayerRepository;
import com.example.tournamentMaker.tournament.Tournament;
import com.example.tournamentMaker.tournament.TournamentRepository;
import com.example.tournamentMaker.tournament.enums.Sport;
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
            Sport sport = t.getSport();
            switch (sport) {
                case FOOTBALL -> {
                    Team team = new Team(teamRequest.getTeamName(), t);
                    FootballStatistics footballStatistics = new FootballStatistics(team, 0, 0, 0, 0, 0);
                    team.setStatistics(footballStatistics);
                    teamRepository.save(team);
                }
            }
        }, () -> {
            throw new NoSuchElementException("No tournament with the given name was found");
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

                // możliwe, że zapis z 2 stron
            } else throw new IllegalArgumentException("Player with this number already exist in team");

        }, () -> {
            throw new NoSuchElementException("No tournament with the given name was found");
        });
    }

    private boolean isPlayerWithNumber(Team team, Integer jerseyNumber) {
        return team.getPlayers().stream()
                .map(player -> (FootballPlayer) player)
                .anyMatch(footballPlayer -> Objects.equals(footballPlayer.getJerseyNumber(), jerseyNumber));
    }
}
