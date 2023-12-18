package com.example.tournamentmaker.tournament.round;

import com.example.tournamentmaker.tournament.Tournament;
import com.example.tournamentmaker.tournament.game.Game;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CupRoundUtil {

    public static void createNextRoundSchedule(Tournament tournament, Round currentRound) {
        int newRoundNumber = currentRound.getTurn();
        Round newRound = tournament.getRounds().get(newRoundNumber);
        int gamesAmount = currentRound.getGames().size();

        for (int i = 0; i < gamesAmount; i++) {
            Game game = currentRound.getGames().get(i);
            long firstWinner = findWinner(game);
            i++;
            game = currentRound.getGames().get(i);
            long secondWinner = findWinner(game);

            Game newGame = new Game(firstWinner, secondWinner, newRound);
            newRound.getGames().add(newGame);
        }
    }

    private static long findWinner(Game game) {
        if (isBye(game) || game.getHostPoints() > game.getGuestPoints()) {
            return game.getHostId();
        }
        return game.getGuestId();
    }

    public static boolean isBye(Game game) {
        return game.getGuestId() == null;
    }
}
