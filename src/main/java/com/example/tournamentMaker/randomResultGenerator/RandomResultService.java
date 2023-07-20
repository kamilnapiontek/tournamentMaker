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
import com.example.tournamentMaker.tournament.game.GameRepository;
import com.example.tournamentMaker.tournament.result.ResultService;
import com.example.tournamentMaker.tournament.round.Round;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    private final Logger logger = LoggerFactory.getLogger(RandomResultService.class);

    public void drawLotRoundsResults(RandomResultRequest request) {
        Tournament tournament = tournamentRepository.findByName(request.getTournamentName())
                .orElseThrow(() -> {
                    throw new NoSuchElementException(Constans.NO_TOURNAMENT_FOUND);
                });
        String[] rounds = request.getRoundsToDraw().split("-");
        int firstRound = Integer.parseInt(rounds[0]);
        int lastRound = Integer.parseInt(rounds[1]);

        drawLotFootballRounds(firstRound, lastRound, tournament);
    }

    private void drawLotFootballRounds(int firstRound, int lastRound, Tournament tournament) {
        IntStream.rangeClosed(firstRound - 1, lastRound - 1).forEach(roundNumber -> {
            try {
                Round round = tournament.getRounds().get(roundNumber);
                drawLotFootballRoundResult(round);
            } catch (ArrayIndexOutOfBoundsException e) {
                logger.error("There is no round with index " + (roundNumber - 1));
            }
        });
    }

    private void drawLotFootballRoundResult(Round round) {
        round.getGames().forEach(game -> {
            int hostPoints = RandomUtil.getRandomGoalsNumber();
            int guestPoints = RandomUtil.getRandomGoalsNumber();

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

            updateSpecificStatistics(hostPoints, guestPoints, hostStatistics, guestStatistics, host, guest);

            MatchResult hostResult = resultService.getHostResult(hostPoints, guestPoints);
            MatchResult guestResult = resultService.getOpposingTeamResult(hostResult);
            resultService.addResultOfTheMatchToStatistics(hostResult, hostStatistics, guestStatistics);

            resultService.updateRecentResult(hostResult, hostStatistics.getRecentMatchResults());
            resultService.updateRecentResult(guestResult, guestStatistics.getRecentMatchResults());

            footballStatisticsRepository.save(hostStatistics);
            footballStatisticsRepository.save(guestStatistics);
        });
    }

    private void updateSpecificStatistics(int hostPoints, int guestPoints, FootballStatistics hostStatistics, FootballStatistics guestStatistics, Team host, Team guest) {
        List<Integer> hostJerseyNumbersGoalScorers = drawLotJerseyNumbersGoalScorers(hostPoints, host);
        List<Integer> guestJerseyNumbersGoalScorers = drawLotJerseyNumbersGoalScorers(guestPoints, guest);
        List<Integer> hostJerseyNumbersWithYellowCard = drawLotUniqueJerseyNumbers(RandomUtil.generateRandomNumber
                (MIN_YELLOW_CARDS_IN_MATCH, MAX_YELLOW_CARDS_IN_MATCH), host);
        List<Integer> guestJerseyNumbersWithYellowCard = drawLotUniqueJerseyNumbers(RandomUtil.generateRandomNumber
                (MIN_YELLOW_CARDS_IN_MATCH, MAX_YELLOW_CARDS_IN_MATCH), guest);
        List<Integer> hostJerseyNumbersWithRedCard = drawLotUniqueJerseyNumbers(RandomUtil.getRandomRedCardCount(), host);
        List<Integer> guestJerseyNumberWithRedCard = drawLotUniqueJerseyNumbers(RandomUtil.getRandomRedCardCount(), guest);

        updateStatisticsForTeam(hostStatistics, host, hostJerseyNumbersGoalScorers,
                hostJerseyNumbersWithYellowCard, hostJerseyNumbersWithRedCard);
        updateStatisticsForTeam(guestStatistics, guest, guestJerseyNumbersGoalScorers,
                guestJerseyNumbersWithYellowCard, guestJerseyNumberWithRedCard);

        if (teamScoredZeroGoals(hostPoints)) {
            resultService.updateSpecificStatisticInTeam(List.of(findJerseyNumberPlayerInSpecificPosition
                    (guest, FootballPosition.GOALKEEPER)), guest, guestStatistics.getPlayersIdWithCleanSheets());
        }
        if (teamScoredZeroGoals(guestPoints)) {
            resultService.updateSpecificStatisticInTeam(List.of(findJerseyNumberPlayerInSpecificPosition
                    (host, FootballPosition.GOALKEEPER)), host, hostStatistics.getPlayersIdWithCleanSheets());
        }
    }

    private void updateStatisticsForTeam(FootballStatistics footballStatistics, Team team,
                                         List<Integer> jerseyNumbersGoalScorers,
                                         List<Integer> jerseyNumbersWithYellowCard,
                                         List<Integer> jerseyNumbersWithRedCard) {
        resultService.updateSpecificStatisticInTeam(jerseyNumbersGoalScorers, team,
                footballStatistics.getPlayersIdWithGoal());
        resultService.updateSpecificStatisticInTeam(jerseyNumbersWithYellowCard, team,
                footballStatistics.getPlayersIdWithYellowCard());
        resultService.updateSpecificStatisticInTeam(jerseyNumbersWithRedCard, team,
                footballStatistics.getPlayersIdWithRedCard());
    }

    private int findJerseyNumberPlayerInSpecificPosition(Team team, FootballPosition position) {
        return team.getPlayers().stream()
                .map(player -> (FootballPlayer) player)
                .filter(footballPlayer -> footballPlayer.getFootballPosition().equals(position))
                .findAny()
                .orElseThrow(NoSuchElementException::new)
                .getJerseyNumber();
    }

    private boolean teamScoredZeroGoals(int goals) {
        return goals == 0;
    }

    private List<Integer> drawLotUniqueJerseyNumbers(int playersCount, Team team) {
        return team.getPlayers().stream()
                .map(player -> (FootballPlayer) player)
                .limit(playersCount)
                .map(FootballPlayer::getJerseyNumber)
                .collect(Collectors.toList());
    }

    private List<Integer> drawLotJerseyNumbersGoalScorers(int points, Team team) {
        List<Integer> jerseyNumbersGoalScorers = new ArrayList<>();

        for (int i = 0; i < points; i++) {
            FootballPosition position = RandomUtil.randomizePositionOfGoalScorer();
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
}
