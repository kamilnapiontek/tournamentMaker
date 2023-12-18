package com.example.tournamentmaker.randomResultGenerator;

import com.example.tournamentmaker.team.Team;
import com.example.tournamentmaker.team.player.FootballPlayer;
import com.example.tournamentmaker.team.player.FootballPosition;
import com.example.tournamentmaker.team.player.Player;
import com.example.tournamentmaker.tournament.Tournament;
import com.example.tournamentmaker.tournament.game.Game;
import com.example.tournamentmaker.tournament.round.Round;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class RandomResultServiceTestData {
    static void createRounds(Tournament tournament) {
        createRoundWithOneGame(1L, 2L, tournament);
        createRoundWithOneGame(1L, 3L, tournament);
        createRoundWithOneGame(2L, 3L, tournament);
    }

    private static void createRoundWithOneGame(long hostId, long guestId, Tournament tournament) {
        Round round = new Round();
        round.getGames().add(new Game(hostId, guestId, round));
        tournament.getRounds().add(round);
    }

    static void createPlayersAtEveryPosition(Team team) {
        List<Player> players = team.getPlayers();
        players.addAll(List.of(
                createPlayerAtPosition(FootballPosition.GOALKEEPER, team, 1),
                createPlayerAtPosition(FootballPosition.DEFENDER, team, 2),
                createPlayerAtPosition(FootballPosition.MIDFIELDER, team, 10),
                createPlayerAtPosition(FootballPosition.FORWARD, team, 9))
        );
    }

    private static FootballPlayer createPlayerAtPosition(FootballPosition position, Team team, int jerseyNumber) {
        return new FootballPlayer("firstName", "lastName", team, jerseyNumber, position);
    }
}
