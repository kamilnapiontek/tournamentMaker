package com.example.tournamentMaker.util;

import com.example.tournamentMaker.team.Team;
import com.example.tournamentMaker.team.player.FootballPlayer;
import com.example.tournamentMaker.team.player.FootballPosition;
import com.example.tournamentMaker.tournament.Tournament;
import com.example.tournamentMaker.tournament.enums.Sport;
import com.example.tournamentMaker.tournament.enums.TournamentType;
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
