package com.example.tournamentMaker.tournament;

import com.example.tournamentMaker.team.Team;
import com.example.tournamentMaker.tournament.game.Game;
import com.example.tournamentMaker.tournament.round.Round;
import com.example.tournamentMaker.tournament.round.RoundRepository;
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
        int roundsAmount;

        if (evenTeamsCount) {
            roundsAmount = teamsAmount - 1;
        } else {
            roundsAmount = teamsAmount;
        }

        List<Long> teamsId = getTeamIdList(tournament);
        List<Long> oddItems = getListOfOddItems(teamsId);
        List<Long> allTeamsIdsInCorrectOrder = addEvenItemsInReverseOrder(teamsId, oddItems);

        for (int i = 0; i < roundsAmount; i++) {
            Round round = new Round(i + 1, tournament);
            for (int j = 0; j < gamesAmount; j++) {
                Game game = new Game(allTeamsIdsInCorrectOrder.get(j),
                        allTeamsIdsInCorrectOrder.get(teamsAmount - 1 - j), round);
                round.getGames().add(game);
            }
            roundRepository.save(round);
            tournament.getRounds().add(round);

            if (evenTeamsCount) {
                movePenultimateItemToFirstPosition(allTeamsIdsInCorrectOrder);
            } else {
                moveLastItemToFirstPosition(allTeamsIdsInCorrectOrder);
            }
        }
        tournamentRepository.save(tournament);
    }

    private List<Long> addEvenItemsInReverseOrder(List<Long> list, List<Long> resultList) {
        resultList.addAll(
                list.stream()
                        .filter(id -> list.indexOf(id) % 2 == 0)
                        .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
                            Collections.reverse(collected);
                            return collected;
                        }))
        );
        return resultList;
    }

    private List<Long> getListOfOddItems(List<Long> list) {
        return new LinkedList<>(list.stream().filter(id -> list.indexOf(id) % 2 != 0).toList());
    }

    private void movePenultimateItemToFirstPosition(List<Long> list) {
        Long penultimateElement = list.get(list.size() - 2);
        list.remove(penultimateElement);
        list.add(0, penultimateElement);
    }

    private void moveLastItemToFirstPosition(List<Long> list) {
        Long lastElement = list.get(list.size() - 1);
        list.remove(lastElement);
        list.add(0, lastElement);
    }

    private List<Long> getTeamIdList(Tournament tournament) {
        return tournament.getTeamList()
                .stream()
                .map(Team::getId)
                .toList();
    }
}
