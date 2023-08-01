package com.example.tournamentMaker.tournament.result;

import com.example.tournamentMaker.statistics.FootballStatistics;
import com.example.tournamentMaker.team.Team;
import com.example.tournamentMaker.team.player.FootballPlayer;
import com.example.tournamentMaker.team.player.FootballPosition;
import com.example.tournamentMaker.team.player.PlayerRepository;
import com.example.tournamentMaker.tournament.Tournament;
import com.example.tournamentMaker.tournament.enums.Sport;
import com.example.tournamentMaker.tournament.enums.TournamentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResultServiceTest {
    @InjectMocks
    private ResultService resultService;

    @Mock
    PlayerRepository playerRepository;

    @Test
    void shouldUpdateGoalsCount() {
        //given
        int hostPoints = 3;
        int guestPoints = 2;
        FootballStatistics hostStatistics = new FootballStatistics(createTeam("Team A"));
        FootballStatistics guestStatistics = new FootballStatistics(createTeam("Team B"));
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
        Team team = createTeam("Team A");
        FootballPlayer player1 = createFootballPlayer(1L, "Jack", team, 5);
        FootballPlayer player2 = createFootballPlayer(3L, "Will", team, 7);
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


    private Team createTeam(String name) {
        return new Team(name, createTournament());
    }

    private Tournament createTournament() {
        return new Tournament("Tournament A", TournamentType.LEAGUE, Sport.FOOTBALL);
    }

    private FootballPlayer createFootballPlayer(long id, String firstName, Team team, int jerseyNumber) {
        FootballPlayer player = new FootballPlayer(firstName, "LastName", team, jerseyNumber, FootballPosition.MIDFIELDER);
        player.setId(id);
        return player;
    }
}