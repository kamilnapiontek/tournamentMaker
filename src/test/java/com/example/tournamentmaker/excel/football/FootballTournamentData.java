package com.example.tournamentmaker.excel.football;

import com.example.tournamentmaker.team.Team;
import com.example.tournamentmaker.team.player.FootballPlayer;
import com.example.tournamentmaker.team.player.FootballPosition;
import com.example.tournamentmaker.team.player.Player;
import com.example.tournamentmaker.tournament.Tournament;
import com.example.tournamentmaker.tournament.enums.Sport;
import com.example.tournamentmaker.tournament.enums.TournamentType;
import com.example.tournamentmaker.tournament.game.Game;
import com.example.tournamentmaker.tournament.round.Round;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FootballTournamentData {
    public static Tournament createFootallTournament(TournamentType type) {
        Tournament tournament = new Tournament("Tournament", type, Sport.FOOTBALL);
        createTeams(tournament);
        addPlayerInEachPositionForTeam(tournament.getTeamList());
        if (type == TournamentType.CUP) {
            createRoundsWithResultsForCup(tournament);
        } else {
            //TODO
        }
        return tournament;
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

    private static void addPlayerInEachPositionForTeam(List<Team> teamList) {
        FootballPosition[] footballPositions = FootballPosition.values();
        AtomicLong id = new AtomicLong(1);
        teamList.forEach(team -> {
            List<Player> players = team.getPlayers();
            int jerseyNumber = 1;
            for (FootballPosition position : footballPositions) {
                FootballPlayer player = new FootballPlayer(team.getName(), position.toString(), team, jerseyNumber++, position);
//                player.setId(id.getAndIncrement()); // potrzebne?
                players.add(player);
            }
        });
    }

    private static void createTeams(Tournament tournament) {
        List<Team> teams = tournament.getTeamList();
        List<String> teamsName = List.of("AC Milan", "Ajax", "Arsenal", "Bayern Munich", "FC Barcelona",
                "Real Madrid", "Manchester United", "Zenit Saint Petersburg");
        AtomicLong id = new AtomicLong(1);
        teamsName.forEach(name -> {
            Team team = new Team(name, tournament);
//            team.setId(id.getAndIncrement()); // potrzebne?
            teams.add(team);
        });
    }

    static byte[] generateFakeImageBytes() {
        return new byte[]{0x12, 0x34, 0x56, 0x78};
    }
}
