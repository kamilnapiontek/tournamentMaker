package com.example.tournamentmaker.tournament;

import com.example.tournamentmaker.team.Team;
import com.example.tournamentmaker.tournament.game.Game;
import com.example.tournamentmaker.tournament.round.Round;
import com.example.tournamentmaker.tournament.round.RoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CupSchedule implements ScheduleStrategy {
    private static final int FIRST_ROUND = 1;
    private final TournamentRepository tournamentRepository;
    private final RoundRepository roundRepository;

    @Override
    public void createSchedule(Tournament tournament) {
        List<Long> teamsId = getTeamIdList(tournament);
        Collections.shuffle(teamsId);

        final int teamsAmount = teamsId.size();
        final int neededSlots = getNeededSlots(teamsAmount);

        createFirstRound(tournament, teamsId, teamsAmount, neededSlots);
        createRemainingRounds(tournament, neededSlots);
        tournamentRepository.save(tournament);
    }

    private void createFirstRound(Tournament tournament, List<Long> teamsId, int teamsAmount, int neededSlots) {
        final int teamsWithoutOpponent = neededSlots - teamsAmount;
        final int gamesAmount = (neededSlots - teamsWithoutOpponent * 2) / 2;
        Round round = new Round(FIRST_ROUND, tournament);

        int currentTeamIndex = 0;
        for (int i = 0; i < gamesAmount; i++) {
            Game game = new Game(teamsId.get(currentTeamIndex), teamsId.get(currentTeamIndex + 1), round);
            round.getGames().add(game);
            currentTeamIndex += 2;
        }
        for (int i = 0; i < teamsWithoutOpponent; i++) {
            Game game = new Game(teamsId.get(currentTeamIndex++), round);
            round.getGames().add(game);
        }

        roundRepository.save(round);
        tournament.getRounds().add(round);
    }

    private int getNeededSlots(int teamsAmount) {
        int neededSlots = 2;
        while (teamsAmount > neededSlots) {
            neededSlots *= 2;
        }
        return neededSlots;
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
