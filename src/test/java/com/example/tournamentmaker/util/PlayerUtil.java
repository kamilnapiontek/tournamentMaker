package com.example.tournamentmaker.util;

import com.example.tournamentmaker.team.Team;
import com.example.tournamentmaker.team.player.FootballPlayer;
import com.example.tournamentmaker.team.player.FootballPosition;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerUtil {
    public static FootballPlayer createFootballPlayer(long id, String firstName, Team team, int jerseyNumber) {
        FootballPlayer player = new FootballPlayer(firstName, "LastName", team, jerseyNumber, FootballPosition.MIDFIELDER);
        player.setId(id);
        return player;
    }

    public static FootballPlayer createFootballPlayer(String firstName, Team team) {
        FootballPlayer player = new FootballPlayer();
        player.setFirstName(firstName);
        player.setLastName("player");
        player.setTeam(team);
        return player;
    }

    public static List<FootballPlayer> createPlayerListForEachTeam(List<Team> teamList, int howManyPlayers) {
        List<FootballPlayer> players = new ArrayList<>();
        AtomicLong playerId = new AtomicLong(1L);
        teamList.forEach(team -> {
            for (int i = 0; i < howManyPlayers; i++) {
                players.add(createFootballPlayer(team.getName() + " " + playerId.getAndIncrement(), team));
            }
        });
        return players;
    }
}
