package com.example.tournamentmaker.util;

import com.example.tournamentmaker.team.Team;
import com.example.tournamentmaker.team.player.FootballPlayer;
import com.example.tournamentmaker.team.player.FootballPosition;
import com.example.tournamentmaker.tournament.Tournament;
import com.example.tournamentmaker.tournament.enums.Sport;
import com.example.tournamentmaker.tournament.enums.TournamentType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Util {
    public static Team createTeam(String name) {
        return new Team(name, createTournament());
    }

    public static Tournament createTournament() {
        return new Tournament("Tournament A", TournamentType.LEAGUE, Sport.FOOTBALL);
    }

    public static FootballPlayer createFootballPlayer(long id, String firstName, Team team, int jerseyNumber) {
        FootballPlayer player = new FootballPlayer(firstName, "LastName", team, jerseyNumber, FootballPosition.MIDFIELDER);
        player.setId(id);
        return player;
    }
}
