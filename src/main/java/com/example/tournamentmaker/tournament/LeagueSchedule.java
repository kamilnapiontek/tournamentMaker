package com.example.tournamentmaker.tournament;

import com.example.tournamentmaker.team.Team;
import com.example.tournamentmaker.tournament.game.Game;
import com.example.tournamentmaker.tournament.round.Round;
import com.example.tournamentmaker.tournament.round.RoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeagueSchedule implements ScheduleStrategy {
    private final TournamentRepository tournamentRepository;
    private final RoundRepository roundRepository;

    @Override
    public void createSchedule(Tournament tournament) {
        boolean evenTeamsCount = tournament.getTeamList().size() % 2 == 0;
        createRoundRobinScheduling(tournament, evenTeamsCount);
    }

    private void createRoundRobinScheduling(Tournament tournament, boolean evenTeamsCount) {
        int teamsAmount = tournament.getTeamList().size();
        int gamesAmount = teamsAmount / 2;
        int roundsAmount = evenTeamsCount ? teamsAmount - 1 : teamsAmount;

        List<Long> teamsId = getTeamIdList(tournament);
        List<Long> oddItems = getListOfOddItems(teamsId);
        List<Long> allTeamsIdsInCorrectOrder = addEvenItemsInReverseOrder(teamsId, oddItems);

        for (int i = 1; i <= roundsAmount; i++) {
            Round round = new Round(i, tournament);
            createRound(round, teamsAmount, gamesAmount, allTeamsIdsInCorrectOrder);
            tournament.getRounds().add(round);

            if (evenTeamsCount) {
                movePenultimateItemToFirstPosition(allTeamsIdsInCorrectOrder);
            } else {
                moveLastItemToFirstPosition(allTeamsIdsInCorrectOrder);
            }
        }
        tournamentRepository.save(tournament);
    }

    private void createRound(Round round, int teamsAmount, int gamesAmount, List<Long> allTeamsIdsInCorrectOrder) {
        for (int j = 0; j < gamesAmount; j++) {
            Game game = new Game(allTeamsIdsInCorrectOrder.get(j),
                    allTeamsIdsInCorrectOrder.get(teamsAmount - 1 - j), round);
            round.getGames().add(game);
        }
        roundRepository.save(round);
    }

    private List<Long> addEvenItemsInReverseOrder(List<Long> teamsId, List<Long> resultList) {
        resultList.addAll(
                teamsId.stream()
                        .filter(id -> teamsId.indexOf(id) % 2 == 0)
                        .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
                            Collections.reverse(collected);
                            return collected;
                        }))
        );
        return resultList;
    }

    private List<Long> getListOfOddItems(List<Long> teamsId) {
        return new LinkedList<>(teamsId.stream().filter(id -> teamsId.indexOf(id) % 2 != 0).toList());
    }

    private void movePenultimateItemToFirstPosition(List<Long> teamsId) {
        Long penultimateElement = teamsId.get(teamsId.size() - 2);
        teamsId.remove(penultimateElement);
        teamsId.add(0, penultimateElement);
    }

    private void moveLastItemToFirstPosition(List<Long> teamsId) {
        Long lastElement = teamsId.get(teamsId.size() - 1);
        teamsId.remove(lastElement);
        teamsId.add(0, lastElement);
    }

    private List<Long> getTeamIdList(Tournament tournament) {
        return tournament.getTeamList()
                .stream()
                .map(Team::getId)
                .toList();
    }
}
