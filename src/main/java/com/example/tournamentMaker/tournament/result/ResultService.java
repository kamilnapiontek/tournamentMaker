package com.example.tournamentMaker.tournament.result;

import com.example.tournamentMaker.constans.Constans;
import com.example.tournamentMaker.statistics.FootballStatistics;
import com.example.tournamentMaker.statistics.MatchResult;
import com.example.tournamentMaker.statistics.Statistics;
import com.example.tournamentMaker.statistics.StatisticsRepository;
import com.example.tournamentMaker.team.Team;
import com.example.tournamentMaker.team.TeamRepository;
import com.example.tournamentMaker.team.player.FootballPlayer;
import com.example.tournamentMaker.team.player.PlayerRepository;
import com.example.tournamentMaker.tournament.Tournament;
import com.example.tournamentMaker.tournament.TournamentRepository;
import com.example.tournamentMaker.tournament.enums.Sport;
import com.example.tournamentMaker.tournament.game.Game;
import com.example.tournamentMaker.tournament.game.GameRepository;
import com.example.tournamentMaker.tournament.round.Round;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ResultService {
    private static final int FIRST_POINT_SCORED = 1;
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final StatisticsRepository statisticsRepository;

    public void launchFootballResult(FootballResultRequest footballResultRequest) {
        Optional<Tournament> optionalTournament = tournamentRepository.findByName(footballResultRequest.getTournamentName());
        optionalTournament.ifPresentOrElse(tournament -> {
                    if (!tournament.getSport().equals(Sport.FOOTBALL)) {
                        throw new IllegalArgumentException("This is not a football tournament");
                    }
                    Team host = findTeam(footballResultRequest.getHostName(), tournament);
                    Team guest = findTeam(footballResultRequest.getGuestName(), tournament);

                    Round round = tournament.getRounds().stream()
                            .filter(r -> Objects.equals(r.getTurn(), footballResultRequest.getTurn()))
                            .findAny().orElseThrow(() -> new NoSuchElementException("Specified round not found"));
                    Game game = round.getGames().stream()
                            .filter(g -> g.getHostId().equals(host.getId()))
                            .findAny().orElseThrow(() -> new NoSuchElementException("There is no such home team in round"));

                    if (!game.getGuestId().equals(guest.getId())) {
                        throw new IllegalArgumentException("There are no games of these teams in this round");
                    }

                    game.setHostPoints(footballResultRequest.getHostPoints());
                    game.setGuestPoints(footballResultRequest.getGuestPoints());
                    gameRepository.save(game);

                    FootballStatistics hostStatistics = (FootballStatistics) host.getStatistics();
                    FootballStatistics guestStatistics = (FootballStatistics) guest.getStatistics();

                    MatchResult hostResult = getHostResult(footballResultRequest);
                    MatchResult guestResult = getOpposingTeamResult(hostResult);
                    addResultOfTheMatchToStatistics(hostResult, hostStatistics, guestStatistics);

                    updateRecentResult(hostResult, hostStatistics.getRecentMatchResults());
                    updateRecentResult(guestResult, guestStatistics.getRecentMatchResults());

                    updateStatisticsForIndividualPlayers(footballResultRequest, host, guest, hostStatistics, guestStatistics);

                    statisticsRepository.save(hostStatistics);
                    statisticsRepository.save(guestStatistics);
                },
                () -> {
                    throw new NoSuchElementException(Constans.NO_TOURNAMENT_FOUND);
                });
    }

    private void updateStatisticsForIndividualPlayers(FootballResultRequest footballResultRequest, Team host, Team guest, FootballStatistics hostStatistics, FootballStatistics guestStatistics) {
        updateSpecificStatisticInTeam(footballResultRequest.getHostStatistics().getShirtNumbersWithGoal(),
                host, hostStatistics.getPlayersIdWithGoal());
        updateSpecificStatisticInTeam(footballResultRequest.getHostStatistics().getShirtNumbersWithYellowCard(),
                host, hostStatistics.getPlayersIdWithYellowCard());
        updateSpecificStatisticInTeam(footballResultRequest.getHostStatistics().getShirtNumbersWithRedCard(),
                host, hostStatistics.getPlayersIdWithRedCard());
        updateSpecificStatisticInTeam(footballResultRequest.getHostStatistics().getGetShirtNumbersWithCleanSlate(),
                host, hostStatistics.getPlayersIdWithCleanSheets());

        updateSpecificStatisticInTeam(footballResultRequest.getGuestStatistics().getShirtNumbersWithGoal(),
                guest, guestStatistics.getPlayersIdWithGoal());
        updateSpecificStatisticInTeam(footballResultRequest.getGuestStatistics().getShirtNumbersWithYellowCard(),
                guest, guestStatistics.getPlayersIdWithYellowCard());
        updateSpecificStatisticInTeam(footballResultRequest.getGuestStatistics().getShirtNumbersWithRedCard(),
                guest, guestStatistics.getPlayersIdWithRedCard());
        updateSpecificStatisticInTeam(footballResultRequest.getGuestStatistics().getGetShirtNumbersWithCleanSlate(),
                guest, guestStatistics.getPlayersIdWithCleanSheets());
    }

    private void updateSpecificStatisticInTeam(List<Integer> jerseyNumbersToIncrease, Team team, Map<Long, Integer> specificStatistic) {
        for (Integer number : jerseyNumbersToIncrease) {
            Optional<FootballPlayer> optionalPlayer = playerRepository.findByJerseyNumberAndTeam(number, team);
            optionalPlayer.ifPresentOrElse(p -> {
                if (specificStatistic.containsKey(p.getId())) {
                    int currentValue = specificStatistic.get(p.getId());
                    specificStatistic.put(p.getId(), ++currentValue);
                } else {
                    specificStatistic.put(p.getId(), FIRST_POINT_SCORED);
                }
            }, () -> {
                throw new NoSuchElementException("There is no player with the given number in the team");
            });


        }
    }

    private MatchResult getOpposingTeamResult(MatchResult result) {
        switch (result) {
            case WIN -> {
                return MatchResult.LOSE;
            }
            case LOSE -> {
                return MatchResult.WIN;
            }
            case DRAW -> {
                return MatchResult.DRAW;
            }
            default -> throw new IllegalArgumentException("The correct result of the match was not given");
        }
    }

    private void updateRecentResult(MatchResult result, List<MatchResult> recentResult) {
        if (recentResult.size() < Constans.COLLECTED_MATCH_RESULTS_NUMBER) {
            recentResult.add(0, result);
        } else {
            recentResult.add(0, result);
            recentResult.remove(Constans.COLLECTED_MATCH_RESULTS_NUMBER);
        }
    }

    private MatchResult getHostResult(FootballResultRequest footballResultRequest) {
        boolean draw = Objects.equals(footballResultRequest.getHostPoints(), footballResultRequest.getGuestPoints());
        if (draw) {
            return MatchResult.DRAW;
        }
        boolean homeTeamWin = footballResultRequest.getHostPoints() > footballResultRequest.getGuestPoints();
        if (homeTeamWin) {
            return MatchResult.WIN;
        }
        return MatchResult.LOSE;
    }

    private void addResultOfTheMatchToStatistics(MatchResult hostResult, Statistics hostStatistics, Statistics guestStatistics) {
        switch (hostResult) {
            case WIN -> {
                hostStatistics.setCountWins(hostStatistics.getCountWins() + 1);
                guestStatistics.setCountLoses(guestStatistics.getCountLoses() + 1);
            }
            case LOSE -> {
                guestStatistics.setCountWins(guestStatistics.getCountWins() + 1);
                hostStatistics.setCountLoses(hostStatistics.getCountLoses() + 1);
            }
            case DRAW -> {
                hostStatistics.setCountDraws(hostStatistics.getCountDraws() + 1);
                guestStatistics.setCountDraws(guestStatistics.getCountDraws() + 1);
            }
        }
    }

    private Team findTeam(String teamName, Tournament tournament) {
        return teamRepository.findByNameAndTournamentName(teamName, tournament.getName()).orElseThrow(
                () -> new NoSuchElementException("No team was found with the given name for the tournament")
        );
    }
}
