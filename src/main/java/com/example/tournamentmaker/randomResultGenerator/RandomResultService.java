package com.example.tournamentmaker.randomResultGenerator;

import com.example.tournamentmaker.constans.Constans;
import com.example.tournamentmaker.statistics.FootballStatistics;
import com.example.tournamentmaker.statistics.FootballStatisticsRepository;
import com.example.tournamentmaker.statistics.MatchResult;
import com.example.tournamentmaker.team.Team;
import com.example.tournamentmaker.team.TeamRepository;
import com.example.tournamentmaker.team.player.FootballPlayer;
import com.example.tournamentmaker.team.player.FootballPosition;
import com.example.tournamentmaker.tournament.Tournament;
import com.example.tournamentmaker.tournament.TournamentRepository;
import com.example.tournamentmaker.tournament.enums.TournamentType;
import com.example.tournamentmaker.tournament.game.Game;
import com.example.tournamentmaker.tournament.game.GameRepository;
import com.example.tournamentmaker.tournament.result.ResultService;
import com.example.tournamentmaker.tournament.round.CupRoundService;
import com.example.tournamentmaker.tournament.round.Round;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.example.tournamentmaker.randomResultGenerator.RandomUtil.*;

@Slf4j
@Service
@RequiredArgsConstructor
class RandomResultService {
    private final TournamentRepository tournamentRepository;
    private final GameRepository gameRepository;
    private final FootballStatisticsRepository footballStatisticsRepository;
    private final TeamRepository teamRepository;
    private final ResultService resultService;
    private final CupRoundService cupRoundService;
    private static final int MIN_YELLOW_CARDS_IN_MATCH = 0;
    private static final int MAX_YELLOW_CARDS_IN_MATCH = 3;

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
                if (tournament.getTournamentType() == TournamentType.CUP && !isLastRound(tournament, round)) {
                    cupRoundService.createNextRoundSchedule(tournament, round);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                log.error("There is no round with index " + (roundNumber - 1) +
                        " check if a tournament schedule has been created");
            }
        });
    }

    private boolean isLastRound(Tournament tournament, Round round) {
        return tournament.getRounds().size() == round.getTurn();
    }

    private void drawLotFootballRoundResult(Round round) {
        round.getGames().forEach(game -> {
            if (!cupRoundService.isBye(game)) {
                drawLotFootballGameResult(game);
            }
        });
    }

    private void drawLotFootballGameResult(Game game) {
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

        updateSpecificStatistics(hostPoints, guestPoints, hostStatistics, guestStatistics, host, guest);

        MatchResult hostResult = resultService.getHostResult(hostPoints, guestPoints);
        MatchResult guestResult = resultService.getOpposingTeamResult(hostResult);
        resultService.addResultOfTheMatchToStatistics(hostResult, hostStatistics, guestStatistics);

        resultService.updateRecentResult(hostResult, hostStatistics.getRecentMatchResults());
        resultService.updateRecentResult(guestResult, guestStatistics.getRecentMatchResults());

        footballStatisticsRepository.save(hostStatistics);
        footballStatisticsRepository.save(guestStatistics);
    }

    private void updateSpecificStatistics(int hostPoints, int guestPoints, FootballStatistics hostStatistics, FootballStatistics guestStatistics, Team host, Team guest) {
        updateSpecificStatisticsForTeam(hostPoints, guestPoints, hostStatistics, host);
        updateSpecificStatisticsForTeam(guestPoints, hostPoints, guestStatistics, guest);
    }

    private void updateSpecificStatisticsForTeam(int points, int opponentPoints, FootballStatistics statistics, Team team) {
        List<Integer> jerseyNumbersGoalScorers = drawLotJerseyNumbersGoalScorers(points, team);
        List<Integer> jerseyNumbersWithYellowCard = drawLotUniqueJerseyNumbers(generateRandomNumber
                (MIN_YELLOW_CARDS_IN_MATCH, MAX_YELLOW_CARDS_IN_MATCH), team);
        List<Integer> jerseyNumbersWithRedCard = drawLotUniqueJerseyNumbers(getRandomRedCardCount(), team);

        updateStatisticsForTeam(statistics, team, jerseyNumbersGoalScorers, jerseyNumbersWithYellowCard,
                jerseyNumbersWithRedCard);
        updateCleanSheetStatistics(opponentPoints, statistics, team);
    }

    private void updateCleanSheetStatistics(int opponentPoints, FootballStatistics footballStatistics, Team team) {
        if (teamScoredZeroGoals(opponentPoints)) {
            resultService.updateSpecificStatisticInTeam(List.of(findJerseyNumberPlayerInSpecificPosition
                    (team, FootballPosition.GOALKEEPER)), team, footballStatistics.getPlayersIdWithCleanSheets());
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
                .map(FootballPlayer.class::cast)
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
                .map(FootballPlayer.class::cast)
                .limit(playersCount)
                .map(FootballPlayer::getJerseyNumber)
                .toList();
    }

    private List<Integer> drawLotJerseyNumbersGoalScorers(int points, Team team) {
        List<Integer> jerseyNumbersGoalScorers = new ArrayList<>();

        for (int i = 0; i < points; i++) {
            FootballPosition position = randomizePositionOfGoalScorer();
            int jerseyNumber = team.getPlayers().stream()
                    .map(FootballPlayer.class::cast)
                    .filter(footballPlayer -> footballPlayer.getFootballPosition().equals(position))
                    .findAny()
                    .orElseThrow(NoSuchElementException::new)
                    .getJerseyNumber();

            jerseyNumbersGoalScorers.add(jerseyNumber);
        }
        return jerseyNumbersGoalScorers;
    }
}
