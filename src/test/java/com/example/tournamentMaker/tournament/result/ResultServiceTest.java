package com.example.tournamentMaker.tournament.result;

import com.example.tournamentMaker.statistics.FootballStatistics;
import com.example.tournamentMaker.team.Team;
import com.example.tournamentMaker.team.player.FootballPlayer;
import com.example.tournamentMaker.team.player.PlayerRepository;
import com.example.tournamentMaker.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResultServiceTest {
    @InjectMocks
    private ResultService resultService;
    @Mock
    private PlayerRepository playerRepository;

    @Test
    void shouldUpdateGoalsCount() {
        //given
        int hostPoints = 3;
        int guestPoints = 2;
        FootballStatistics hostStatistics = new FootballStatistics(Util.createTeam("Team A"));
        FootballStatistics guestStatistics = new FootballStatistics(Util.createTeam("Team B"));
        //when
        resultService.updateGoalsCount(hostPoints, guestPoints, hostStatistics, guestStatistics);
        //then
        Assertions.assertAll(
                () -> assertEquals(hostPoints, hostStatistics.getGoalsScored()),
                () -> assertEquals(hostPoints, guestStatistics.getGoalsConceded()),
                () -> assertEquals(guestPoints, guestStatistics.getGoalsScored()),
                () -> assertEquals(guestPoints, hostStatistics.getGoalsConceded())
        );
    }

    @Test
    void shouldUpdateSpecificStatisticInTeam() {
        //given
        List<Integer> jerseyNumbersList = List.of(5, 5, 7);
        Team team = Util.createTeam("Team A");
        FootballPlayer player1 = Util.createFootballPlayer(1L, "Jack", team, 5);
        FootballPlayer player2 = Util.createFootballPlayer(3L, "Will", team, 7);
        Map<Long, Integer> specificStatistic = new HashMap<>();
        specificStatistic.put(1L, 1);
        specificStatistic.put(3L, 22);
        //when
        when(playerRepository.findByJerseyNumberAndTeam(5, team)).thenReturn(Optional.of(player1));
        when(playerRepository.findByJerseyNumberAndTeam(7, team)).thenReturn(Optional.of(player2));
        resultService.updateSpecificStatisticInTeam(jerseyNumbersList, team, specificStatistic);
        //then
        assertEquals(3, specificStatistic.get(player1.getId()));
        assertEquals(23, specificStatistic.get(player2.getId()));
    }

    @Test
    void shouldUpdateSpecificStatisticInTeamWhenNoPlayersInSpecificStatisticMap() {
        //given
        List<Integer> jerseyNumbersList = List.of(5, 5, 5, 5);
        Team team = Util.createTeam("Team A");
        FootballPlayer player = Util.createFootballPlayer(1L, "Jack", team, 5);
        Map<Long, Integer> specificStatistic = new HashMap<>();
        //when
        when(playerRepository.findByJerseyNumberAndTeam(5, team)).thenReturn(Optional.of(player));
        resultService.updateSpecificStatisticInTeam(jerseyNumbersList, team, specificStatistic);
        //then
        int numberOfGoalsScoredByPlayerWithNumber5 = 4;
        assertEquals(numberOfGoalsScoredByPlayerWithNumber5, specificStatistic.get(player.getId()));
    }

    @Test
    void shouldContainExceptionWhenPlayerWithJerseyNumberNotFound() {
        //given
        List<Integer> jerseyNumbersList = List.of(5);
        Team team = Util.createTeam("Team A");
        //when
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            resultService.updateSpecificStatisticInTeam(jerseyNumbersList, team, new HashMap<>());
        });
    }
}