package com.example.tournamentmaker.tournament.result;

import com.example.tournamentmaker.constans.Constans;
import com.example.tournamentmaker.statistics.FootballStatistics;
import com.example.tournamentmaker.statistics.FootballStatisticsRepository;
import com.example.tournamentmaker.statistics.MatchResult;
import com.example.tournamentmaker.statistics.Statistics;
import com.example.tournamentmaker.team.Team;
import com.example.tournamentmaker.team.TeamRepository;
import com.example.tournamentmaker.team.player.FootballPlayer;
import com.example.tournamentmaker.team.player.PlayerRepository;
import com.example.tournamentmaker.tournament.Tournament;
import com.example.tournamentmaker.tournament.TournamentRepository;
import com.example.tournamentmaker.tournament.enums.Sport;
import com.example.tournamentmaker.tournament.game.Game;
import com.example.tournamentmaker.tournament.game.GameRepository;
import com.example.tournamentmaker.tournament.round.Round;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResultService {
    private static final int FIRST_POINT_SCORED = 1;
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final FootballStatisticsRepository footballStatisticsRepository;

    public void launchFootballResult(FootballResultRequest request) {
        Optional<Tournament> optionalTournament = tournamentRepository.findByName(request.getTournamentName());
        optionalTournament.ifPresentOrElse(tournament -> {
                    if (!tournament.getSport().equals(Sport.FOOTBALL)) {
                        throw new IllegalArgumentException("This is not a football tournament");
                    }
                    Team host = findTeam(request.getHostName(), tournament);
                    Team guest = findTeam(request.getGuestName(), tournament);

                    Round round = tournament.getRounds().stream()
                            .filter(r -> Objects.equals(r.getTurn(), request.getTurn()))
                            .findAny().orElseThrow(() -> new NoSuchElementException("Specified round not found"));
                    Game game = round.getGames().stream()
                            .filter(g -> g.getHostId().equals(host.getId()))
                            .findAny().orElseThrow(() -> new NoSuchElementException("There is no such home team in round"));

                    if (!game.getGuestId().equals(guest.getId())) {
                        throw new IllegalArgumentException("There are no games of these teams in this round");
                    }

                    setAndSavePointsForBothTeams(request, game);

                    FootballStatistics hostStatistics = footballStatisticsRepository.findByTeamId(host.getId())
                            .orElseThrow(() -> new NoSuchElementException("Stats for the home team could not be found"));
                    FootballStatistics guestStatistics = footballStatisticsRepository.findByTeamId(guest.getId())
                            .orElseThrow(() -> new NoSuchElementException("Stats for the guest team could not be found"));

                    updateGoalsCount(request.getHostPoints(), request.getGuestPoints(), hostStatistics, guestStatistics);

                    MatchResult hostResult = getHostResult(request.getHostPoints(), request.getGuestPoints());
                    MatchResult guestResult = getOpposingTeamResult(hostResult);
                    addResultOfTheMatchToStatistics(hostResult, hostStatistics, guestStatistics);

                    updateRecentResults(hostStatistics, guestStatistics, hostResult, guestResult);
                    updateStatisticsForIndividualPlayers(request, host, guest, hostStatistics, guestStatistics);
                    saveStatisticRepositories(hostStatistics, guestStatistics);
                },
                () -> {
                    throw new NoSuchElementException(Constans.NO_TOURNAMENT_FOUND);
                });
    }

    private void saveStatisticRepositories(FootballStatistics hostStatistics, FootballStatistics guestStatistics) {
        footballStatisticsRepository.save(hostStatistics);
        footballStatisticsRepository.save(guestStatistics);
    }

    private void updateRecentResults(FootballStatistics hostStatistics, FootballStatistics guestStatistics,
                                     MatchResult hostResult, MatchResult guestResult) {
        updateRecentResult(hostResult, hostStatistics.getRecentMatchResults());
        updateRecentResult(guestResult, guestStatistics.getRecentMatchResults());
    }

    private void setAndSavePointsForBothTeams(FootballResultRequest request, Game game) {
        game.setHostPoints(request.getHostPoints());
        game.setGuestPoints(request.getGuestPoints());
        gameRepository.save(game);
    }

    public void updateGoalsCount(int hostPoints, int guestPoints, FootballStatistics hostStatistics, FootballStatistics guestStatistics) {
        hostStatistics.setGoalsScored(hostStatistics.getGoalsScored() + hostPoints);
        hostStatistics.setGoalsConceded(hostStatistics.getGoalsConceded() + guestPoints);
        guestStatistics.setGoalsScored(guestStatistics.getGoalsScored() + guestPoints);
        guestStatistics.setGoalsConceded(guestStatistics.getGoalsConceded() + hostPoints);
    }

    private void updateStatisticsForIndividualPlayers(FootballResultRequest request, Team host, Team guest, FootballStatistics hostStatistics, FootballStatistics guestStatistics) {
        updateAllSpecificStatisticForTeam(request.getHostStatistics(), host, hostStatistics);
        updateAllSpecificStatisticForTeam(request.getGuestStatistics(), guest, guestStatistics);
    }

    private void updateAllSpecificStatisticForTeam(FootballResultStatistics requestStatistics, Team team, FootballStatistics statistics) {
        updateSpecificStatisticInTeam(requestStatistics.getShirtNumbersWithGoal(), team,
                statistics.getPlayersIdWithGoal());
        updateSpecificStatisticInTeam(requestStatistics.getShirtNumbersWithYellowCard(), team,
                statistics.getPlayersIdWithYellowCard());
        updateSpecificStatisticInTeam(requestStatistics.getShirtNumbersWithRedCard(), team,
                statistics.getPlayersIdWithRedCard());
        updateSpecificStatisticInTeam(requestStatistics.getShirtNumbersWithCleanSlate(), team,
                statistics.getPlayersIdWithCleanSheets());
    }

    public void updateSpecificStatisticInTeam(List<Integer> jerseyNumbersList, Team team, Map<Long, Integer> specificStatistic) {
        if (jerseyNumbersList == null) {
            log.info("Lack of players to increase stats");
            return;
        }
        for (Integer number : jerseyNumbersList) {
            Optional<FootballPlayer> optionalPlayer = playerRepository.findByJerseyNumberAndTeam(number, team);
            optionalPlayer.ifPresentOrElse(player -> {
                if (specificStatistic.containsKey(player.getId())) {
                    int statisticToIncrease = specificStatistic.get(player.getId());
                    statisticToIncrease++;
                    specificStatistic.put(player.getId(), statisticToIncrease);
                } else {
                    specificStatistic.put(player.getId(), FIRST_POINT_SCORED);
                }
            }, () -> {
                throw new NoSuchElementException("There is no player with the given number in the team");
            });
        }
    }

    public MatchResult getOpposingTeamResult(MatchResult result) {
        return switch (result) {
            case WIN -> MatchResult.LOSE;
            case LOSE -> MatchResult.WIN;
            case DRAW -> MatchResult.DRAW;
        };
    }

    public void updateRecentResult(MatchResult result, List<MatchResult> recentResults) {
        recentResults.add(0, result);
        if (recentResults.size() > Constans.COLLECTED_MATCH_RESULTS_NUMBER) {
            recentResults.remove(Constans.COLLECTED_MATCH_RESULTS_NUMBER);
        }
    }

    public MatchResult getHostResult(int hostPoints, int guestPoints) {
        boolean draw = Objects.equals(hostPoints, guestPoints);
        if (draw) {
            return MatchResult.DRAW;
        }
        return hostPoints > guestPoints ? MatchResult.WIN : MatchResult.LOSE;
    }

    public void addResultOfTheMatchToStatistics(MatchResult hostResult, Statistics hostStatistics, Statistics guestStatistics) {
        switch (hostResult) {
            case WIN -> {
                hostStatistics.setCountWins(hostStatistics.getCountWins() + 1);
                guestStatistics.setCountLoses(guestStatistics.getCountLoses() + 1);
                hostStatistics.setPoints(hostStatistics.getPoints() + 3);
            }
            case LOSE -> {
                guestStatistics.setCountWins(guestStatistics.getCountWins() + 1);
                hostStatistics.setCountLoses(hostStatistics.getCountLoses() + 1);
                guestStatistics.setPoints(guestStatistics.getPoints() + 3);
            }
            case DRAW -> {
                hostStatistics.setCountDraws(hostStatistics.getCountDraws() + 1);
                guestStatistics.setCountDraws(guestStatistics.getCountDraws() + 1);
                hostStatistics.setPoints(hostStatistics.getPoints() + 1);
                guestStatistics.setPoints(guestStatistics.getPoints() + 1);
            }
        }
    }

    private Team findTeam(String teamName, Tournament tournament) {
        return teamRepository.findByNameAndTournamentName(teamName, tournament.getName()).orElseThrow(
                () -> new NoSuchElementException("No team was found with the given name for the tournament")
        );
    }
}

