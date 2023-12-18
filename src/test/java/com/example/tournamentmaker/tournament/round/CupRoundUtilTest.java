package com.example.tournamentmaker.tournament.round;

import com.example.tournamentmaker.tournament.Tournament;
import com.example.tournamentmaker.tournament.game.Game;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.example.tournamentmaker.tournament.round.CupRoundUtil.createNextRoundSchedule;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CupRoundUtilTest {

    @Test
    void shouldCreateRoundScheduleWhenTeamsADGoToNext() {
        // given
        Tournament tournament = new Tournament();
        List<Round> rounds = tournament.getRounds();
        rounds.add(new Round(1, tournament));
        rounds.add(new Round(2, tournament));
        Round currentRound = rounds.get(0);
        enterRoundResult(currentRound);

        Round roundToGenerate = rounds.get(1);
        long winnerGame1Id = 1L;
        long winnerGame2Id = 4L;
        // when
        createNextRoundSchedule(tournament, currentRound);
        // then
        Assertions.assertAll(
                () -> assertEquals(1, roundToGenerate.getGames().size()),
                () -> assertEquals(winnerGame1Id, roundToGenerate.getGames().get(0).getHostId()),
                () -> assertEquals(winnerGame2Id, roundToGenerate.getGames().get(0).getGuestId())
        );
    }

    private void enterRoundResult(Round currentRound) {
        List<Game> games = currentRound.getGames();
        long teamA = 1L;
        int pointsA = 2;
        long teamB = 2L;
        int pointsB = 0;
        long teamC = 3L;
        int pointsC = 2;
        long teamD = 4L;
        int pointsD = 3;

        games.add(new Game(teamA, teamB, currentRound));
        Game firstGame = currentRound.getGames().get(0);
        setPointsInGame(firstGame, pointsA, pointsB);

        games.add(new Game(teamC, teamD, currentRound));
        Game secondGame = currentRound.getGames().get(1);
        setPointsInGame(secondGame, pointsC, pointsD);
    }

    private void setPointsInGame(Game game, int hostPoints, int guestPoints) {
        game.setHostPoints(hostPoints);
        game.setGuestPoints(guestPoints);
    }
}