package com.example.tournamentMaker.tournament.round;

import com.example.tournamentMaker.tournament.Tournament;
import com.example.tournamentMaker.tournament.game.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CupRoundService {

    public void createNextRoundSchedule(Tournament tournament, Round currentRound) {
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

    private long findWinner(Game game) {
        if (isBye(game) || game.getHostPoints() > game.getGuestPoints()) {
            return game.getHostId();
        }
        return game.getGuestId();
    }

    public boolean isBye(Game game) {
        return game.getGuestId() == null;
    }
}
