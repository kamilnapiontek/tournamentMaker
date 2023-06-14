package com.example.tournamentMaker.tournament;

import com.example.tournamentMaker.team.Team;
import com.example.tournamentMaker.tournament.game.Game;
import com.example.tournamentMaker.tournament.round.Round;
import com.example.tournamentMaker.tournament.round.RoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CupSchedule implements ScheduleStrategy {
    private final TournamentRepository tournamentRepository;
    private final RoundRepository roundRepository;
    private final static int FIRST_ROUND = 1;

    @Override
    public void createSchedule(Tournament tournament) {
        List<Long> teamsId = getTeamIdList(tournament);
        Collections.shuffle(teamsId);

        int teamsAmount = teamsId.size();

        int neededSlots = 2;
        while (teamsAmount > neededSlots) {
            neededSlots *= 2;
        }
        int teamsWithoutOpponent = neededSlots - teamsAmount;
        int gamesAmount = (neededSlots - teamsWithoutOpponent * 2) / 2;
        Round round = new Round(FIRST_ROUND, tournament);

        int currentIndex = 0;
        for (int i = 0; i < gamesAmount; i++) {
            Game game = new Game(teamsId.get(currentIndex), teamsId.get(currentIndex + 1), round);
            round.getGames().add(game);
            currentIndex += 2;
        }
        for (int i = 0; i < teamsWithoutOpponent; i++) {
            Game game = new Game(teamsId.get(currentIndex++), null, round);
            round.getGames().add(game);
        }
        roundRepository.save(round);
        tournament.getRounds().add(round);

        createRemainingRounds(tournament, neededSlots);
        tournamentRepository.save(tournament);
    }

    private void createRemainingRounds(Tournament tournament, int slots) {
        int roundsAmountToCreate = getPowerOfTwo(slots) - FIRST_ROUND;
        for (int i = 0; i < roundsAmountToCreate; i++) {
            Round round = new Round(i + 2, tournament);
            tournament.getRounds().add(round);
            roundRepository.save(round);
        }
    }

    private int getPowerOfTwo(int number) {
        return (int) (Math.log(number) / Math.log(2));
    }

    private List<Long> getTeamIdList(Tournament tournament) {
        return new ArrayList<>(tournament.getTeamList()
                .stream()
                .map(Team::getId)
                .toList());
    }
}
