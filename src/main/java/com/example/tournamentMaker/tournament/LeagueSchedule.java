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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeagueSchedule implements ScheduleStrategy {
    private final TournamentRepository tournamentRepository;
    private final RoundRepository roundRepository;

    @Override
    public void createSchedule(Tournament tournament) {
        if (tournament.getTeamList().size() % 2 == 0) {
            createScheduleForEvenTeamsNumber(tournament);
        } else createScheduleForOddTeamsNumber(tournament);
    }

    private void createScheduleForEvenTeamsNumber(Tournament tournament) {
        int numberTeams = tournament.getTeamList().size();
        int roundsNumber = numberTeams - 1;
        int gamesNumber = numberTeams / 2;

        List<Long> teamIdList = getTeamIdList(tournament);

        List<Long> list = getListOfOddItems(teamIdList);
        addEvenItemsInReverseOrder(teamIdList, list);

        for (int i = 0; i < roundsNumber; i++) {
            Round round = new Round(tournament);
            for (int j = 0; j < gamesNumber; j++) {
                Game game = new Game(i + 1, list.get(j), list.get(numberTeams - 1 - j), round);
                round.getGames().add(game);
            }
            roundRepository.save(round);
            moveSecondLastItemToFirstPosition(list);
            tournament.getRounds().add(round);

        }
        tournamentRepository.save(tournament);
    }

    private void createScheduleForOddTeamsNumber(Tournament tournament) {
        int numberTeams = tournament.getTeamList().size();
        int roundsNumber = numberTeams;
        int gamesNumber = numberTeams / 2;

        List<Long> teamIdList = getTeamIdList(tournament);

        List<Long> list = getListOfOddItems(teamIdList);
        addEvenItemsInReverseOrder(teamIdList, list);

        for (int i = 0; i < roundsNumber; i++) {
            Round round = new Round(tournament);
            for (int j = 0; j < gamesNumber; j++) {
                Game game = new Game(i + 1, list.get(j), list.get(numberTeams - 1 - j), round);
                round.getGames().add(game);
            }
            roundRepository.save(round);
            moveLastItemToTheFirstPosition(list);
            tournament.getRounds().add(round);
        }
        tournamentRepository.save(tournament);
    }

    private void addEvenItemsInReverseOrder(List<Long> list, List<Long> resultList) {
        resultList.addAll(
                list.stream()
                        .filter(id -> list.indexOf(id) % 2 == 0)
                        .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
                            Collections.reverse(collected);
                            return collected;
                        }))
        );
    }

    private ArrayList<Long> getListOfOddItems(List<Long> list) {
        return new ArrayList<>(
                list.stream()
                        .filter(id -> list.indexOf(id) % 2 != 0)
                        .toList());
    }

    private List<Long> getTeamIdList(Tournament tournament) {
        return tournament.getTeamList().stream()
                .map(Team::getId)
                .toList();
    }

    private void moveSecondLastItemToFirstPosition(List<Long> list) {

        Long secondLastElement = list.get(list.size() - 2);
        list.remove(secondLastElement);
        list.add(0, secondLastElement);
    }

    private void moveLastItemToTheFirstPosition(List<Long> list) {
        List<Long> newList = list.stream()
                .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
                    List<Long> reversedList = new ArrayList<>(collected);
                    int lastElementIndex = reversedList.size() - 1;
                    Long lastElement = reversedList.remove(lastElementIndex);
                    reversedList.add(0, lastElement);
                    return reversedList;
                }));
        list.clear();
        list.addAll(newList);
    }
}
