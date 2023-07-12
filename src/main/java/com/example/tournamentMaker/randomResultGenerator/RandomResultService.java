package com.example.tournamentMaker.randomResultGenerator;

import com.example.tournamentMaker.constans.Constans;
import com.example.tournamentMaker.statistics.FootballStatistics;
import com.example.tournamentMaker.statistics.FootballStatisticsRepository;
import com.example.tournamentMaker.statistics.MatchResult;
import com.example.tournamentMaker.team.Team;
import com.example.tournamentMaker.team.TeamRepository;
import com.example.tournamentMaker.team.player.FootballPlayer;
import com.example.tournamentMaker.team.player.FootballPosition;
import com.example.tournamentMaker.tournament.Tournament;
import com.example.tournamentMaker.tournament.TournamentRepository;
import com.example.tournamentMaker.tournament.enums.Sport;
import com.example.tournamentMaker.tournament.game.GameRepository;
import com.example.tournamentMaker.tournament.result.ResultService;
import com.example.tournamentMaker.tournament.round.Round;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class RandomResultService {
    private final TournamentRepository tournamentRepository;
    private final GameRepository gameRepository;
    private final FootballStatisticsRepository footballStatisticsRepository;
    private final ResultService resultService;
    private final TeamRepository teamRepository;
    private static final int MIN_YELLOW_CARDS_IN_MATCH = 0;
    private static final int MAX_YELLOW_CARDS_IN_MATCH = 3;
    private static final int ZERO_PERCENT = 0;
    private static final int ONE_HUNDRED_PERCENT = 100;

    public void drawRoundsResults(RandomResultRequest request) {
        Tournament tournament = tournamentRepository.findByName(request.getTournamentName())
                .orElseThrow(() -> {
                    throw new NoSuchElementException(Constans.NO_TOURNAMENT_FOUND);
                });
        String[] drawRounds = request.getRoundsToDraw().split("-");
        int firstRound = Integer.parseInt(drawRounds[0]);
        int lastRound = Integer.parseInt(drawRounds[1]);

        Sport sport = tournament.getSport();
        switch (sport) {
            case FOOTBALL -> drawFootballRounds(firstRound, lastRound, tournament);
        }
    }

    private void drawFootballRounds(int firstRound, int lastRound, Tournament tournament) {
        for (int i = firstRound; i <= lastRound; i++) {
            drawFootballRoundResult(i - 1, tournament);
        }
    }

    private void drawFootballRoundResult(int roundNumber, Tournament tournament) {
        Round round = tournament.getRounds().get(roundNumber);
        round.getGames().forEach(game -> {
            int hostPoints = getRandomGoalsNumber();
            int guestPoints = getRandomGoalsNumber();

            game.setHostPoints(hostPoints);
            game.setGuestPoints(guestPoints);
            gameRepository.save(game);

            FootballStatistics hostStatistics = footballStatisticsRepository.findByTeamId(game.getHostId())
                    .orElseThrow(() -> new NoSuchElementException("Stats for the home team could not be found"));
            FootballStatistics guestStatistics = footballStatisticsRepository.findByTeamId(game.getGuestId())
                    .orElseThrow(() -> new NoSuchElementException("Stats for the guest team could not be found"));

            resultService.updateGoalsCount(hostPoints, guestPoints, hostStatistics, guestStatistics);

            Team host = teamRepository.findById(game.getHostId())
                    .orElseThrow(() -> new NoSuchElementException(Constans.NO_TEAM_FOUND));
            Team guest = teamRepository.findById(game.getGuestId())
                    .orElseThrow(() -> new NoSuchElementException(Constans.NO_TEAM_FOUND));

            List<Integer> hostJerseyNumbersGoalScorers = drawJerseyNumbersGoalScorers(hostPoints, host);
            List<Integer> guestJerseyNumbersGoalScorers = drawJerseyNumbersGoalScorers(guestPoints, guest);
            List<Integer> hostJerseyNumbersWithYellowCard = drawUniqueJerseyNumbers(Util.generateRandomNumber
                    (MIN_YELLOW_CARDS_IN_MATCH, MAX_YELLOW_CARDS_IN_MATCH), host);
            List<Integer> guestJerseyNumbersWithYellowCard = drawUniqueJerseyNumbers(Util.generateRandomNumber
                    (MIN_YELLOW_CARDS_IN_MATCH, MAX_YELLOW_CARDS_IN_MATCH), guest);
            List<Integer> hostJerseyNumbersWithRedCard = drawUniqueJerseyNumbers(getRandomRedCardCount(), host);
            List<Integer> guestJerseyNumberWithRedCard = drawUniqueJerseyNumbers(getRandomRedCardCount(), guest);

            resultService.updateSpecificStatisticInTeam(hostJerseyNumbersGoalScorers, host,
                    hostStatistics.getPlayersIdWithGoal());
            resultService.updateSpecificStatisticInTeam(hostJerseyNumbersWithYellowCard, host,
                    hostStatistics.getPlayersIdWithYellowCard());
            resultService.updateSpecificStatisticInTeam(hostJerseyNumbersWithRedCard, host,
                    hostStatistics.getPlayersIdWithRedCard());
            resultService.updateSpecificStatisticInTeam(guestJerseyNumbersGoalScorers, guest,
                    guestStatistics.getPlayersIdWithGoal());
            resultService.updateSpecificStatisticInTeam(guestJerseyNumbersWithYellowCard, guest,
                    guestStatistics.getPlayersIdWithYellowCard());
            resultService.updateSpecificStatisticInTeam(guestJerseyNumberWithRedCard, guest,
                    guestStatistics.getPlayersIdWithRedCard());

            if (teamScoredZeroGoals(hostPoints)) {
                resultService.updateSpecificStatisticInTeam(List.of(findGoalKeeperJerseyNumber(guest)), guest,
                        guestStatistics.getPlayersIdWithCleanSheets());
            }
            if (teamScoredZeroGoals(guestPoints)) {
                resultService.updateSpecificStatisticInTeam(List.of(findGoalKeeperJerseyNumber(host)), host,
                        hostStatistics.getPlayersIdWithCleanSheets());
            }

            MatchResult hostResult = resultService.getHostResult(hostPoints, guestPoints);
            MatchResult guestResult = resultService.getOpposingTeamResult(hostResult);
            resultService.addResultOfTheMatchToStatistics(hostResult, hostStatistics, guestStatistics);

            resultService.updateRecentResult(hostResult, hostStatistics.getRecentMatchResults());
            resultService.updateRecentResult(guestResult, guestStatistics.getRecentMatchResults());

            footballStatisticsRepository.save(hostStatistics);
            footballStatisticsRepository.save(guestStatistics);
        });
    }

    private int findGoalKeeperJerseyNumber(Team team) {
        return team.getPlayers().stream()
                .map(player -> (FootballPlayer) player)
                .filter(footballPlayer -> footballPlayer.getFootballPosition().equals(FootballPosition.GOALKEEPER))
                .findAny()
                .orElseThrow(NoSuchElementException::new)
                .getJerseyNumber();
    }

    private boolean teamScoredZeroGoals(int goals) {
        return goals == 0;
    }

    private List<Integer> drawUniqueJerseyNumbers(int playersCount, Team team) {
        return team.getPlayers().stream()
                .map(player -> (FootballPlayer) player)
                .limit(playersCount)
                .map(FootballPlayer::getJerseyNumber)
                .collect(Collectors.toList());
    }

    private List<Integer> drawJerseyNumbersGoalScorers(int points, Team team) {
        List<Integer> jerseyNumbersGoalScorers = new ArrayList<>();

        for (int i = 0; i < points; i++) {
            FootballPosition position = randomizePositionOfGoalScorer();
            int jerseyNumber = team.getPlayers().stream()
                    .map(player -> (FootballPlayer) player)
                    .filter(footballPlayer -> footballPlayer.getFootballPosition().equals(position))
                    .findAny()
                    .orElseThrow(NoSuchElementException::new)
                    .getJerseyNumber();

            jerseyNumbersGoalScorers.add(jerseyNumber);
        }
        return jerseyNumbersGoalScorers;
    }

    private FootballPosition randomizePositionOfGoalScorer() {
        int randomNumber = Util.generateRandomNumber(ZERO_PERCENT, ONE_HUNDRED_PERCENT);
        if (randomNumber < 40) return FootballPosition.FORWARD;
        if (randomNumber < 80) return FootballPosition.MIDFIELDER;
        if (randomNumber < 100) return FootballPosition.DEFENDER;
        return FootballPosition.GOALKEEPER;
    }

    private int getRandomGoalsNumber() {
        int randomNumber = Util.generateRandomNumber(ZERO_PERCENT, ONE_HUNDRED_PERCENT);
        if (randomNumber < 20) return 0;
        if (randomNumber < 42) return 1;
        if (randomNumber < 64) return 2;
        if (randomNumber < 80) return 3;
        if (randomNumber < 90) return 4;
        if (randomNumber < 94) return 5;
        if (randomNumber < 97) return 6;
        if (randomNumber < 98) return 7;
        if (randomNumber < 99) return 8;
        if (randomNumber < 100) return 9;
        return 10;
    }

    private int getRandomRedCardCount() {
        int randomNumber = Util.generateRandomNumber(ZERO_PERCENT, ONE_HUNDRED_PERCENT);
        if (randomNumber < 95) return 0;
        if (randomNumber < 99) return 1;
        return 2;
    }
}
