package com.example.tournamentmaker.excel.football;

import com.example.tournamentmaker.statistics.MatchResult;
import com.example.tournamentmaker.statistics.Statistics;
import com.example.tournamentmaker.team.Team;
import com.example.tournamentmaker.tournament.Tournament;
import com.example.tournamentmaker.tournament.enums.Sport;
import com.example.tournamentmaker.tournament.enums.TournamentType;
import com.example.tournamentmaker.tournament.game.Game;
import com.example.tournamentmaker.tournament.round.Round;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static com.example.tournamentmaker.constans.Constans.POINTS_FOR_WINNING_IN_FOOTBALL;
import static com.example.tournamentmaker.util.StatisticUtil.createAllFootballStatisticsForTournament;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FootballTournamentData {
    public static Tournament createFootballTournament(TournamentType type) {
        Tournament tournament = new Tournament("Tournament", type, Sport.FOOTBALL);
        createTeams(tournament);
        if (type == TournamentType.CUP) {
            createRoundsWithResultsForCup(tournament);
        } else {
            createAllFootballStatisticsForTournament(tournament);
            enterStatisticsForLeague(tournament);
        }
        return tournament;
    }

    private static void enterStatisticsForLeague(Tournament tournament) {
        List<Team> teamList = tournament.getTeamList();
        final int countDraws = 0;
        int countWins = 3;
        int countLoses = 0;

        for (Team team : teamList) {
            Statistics statistics = team.getStatistics();
            statistics.setPoints(countWins * POINTS_FOR_WINNING_IN_FOOTBALL);
            statistics.setCountWins(countWins--);
            statistics.setCountLoses(countLoses++);
            statistics.setCountDraws(countDraws);
            statistics.setRecentMatchResults(List.of(MatchResult.WIN, MatchResult.DRAW, MatchResult.LOSE));
        }
    }

    private static void createRoundsWithResultsForCup(Tournament tournament) {
        List<Round> rounds = tournament.getRounds();
        int turnAmount = 3;
        int gamesAmount = 4;
        int teamIdJump = 1;

        for (int i = 1; i <= turnAmount; i++) {
            createRound(rounds, i, gamesAmount, teamIdJump, tournament);
            gamesAmount = gamesAmount / 2;
            teamIdJump = teamIdJump * 2;
        }
    }

    private static void createRound(List<Round> rounds, int turn, int gamesAmount, int teamIdJump,
                                    Tournament tournament) {
        Round round = new Round(turn, tournament);
        long gameId = 1L;
        long teamId = 1L;
        for (int i = 0; i < gamesAmount; i++) {
            Game game = new Game(teamId, teamId + teamIdJump, round);
            game.setHostPoints(2);
            game.setGuestPoints(1);
            game.setId(gameId);
            gameId++;
            teamId += teamIdJump * 2L;
            round.getGames().add(game);
        }
        rounds.add(round);
    }

    private static void createTeams(Tournament tournament) {
        List<Team> teams = tournament.getTeamList();
        List<String> teamNames = getTeamNames(tournament.getTournamentType());
        AtomicLong id = new AtomicLong(1);
        teamNames.forEach(name -> {
            Team team = new Team(name, tournament);
            team.setId(id.getAndIncrement());
            teams.add(team);
        });
    }

    private static List<String> getTeamNames(TournamentType type) {
        if (type == TournamentType.CUP) {
            return List.of("AC Milan", "Ajax", "Arsenal", "Bayern Munich", "FC Barcelona",
                    "Real Madrid", "Manchester United", "Zenit Saint Petersburg");
        }
        return List.of("AC Milan", "Ajax", "Arsenal", "Bayern Munich");
    }

    public static String getStringCellValue(int colNumber, int rowNumber, Sheet sheet) {
        Cell cell = sheet.getRow(rowNumber).getCell(colNumber);
        DataFormatter dataFormatter = new DataFormatter();
        return dataFormatter.formatCellValue(cell);
    }
}
