package com.example.tournamentMaker.util;

import com.example.tournamentMaker.team.Team;
import com.example.tournamentMaker.team.player.FootballPlayer;
import com.example.tournamentMaker.team.player.FootballPosition;
import com.example.tournamentMaker.tournament.Tournament;
import com.example.tournamentMaker.tournament.enums.Sport;
import com.example.tournamentMaker.tournament.enums.TournamentType;

public class Util {
    public Team createTeam(String name) {
        return new Team(name, createTournament());
    }

    public Tournament createTournament() {
        return new Tournament("Tournament A", TournamentType.LEAGUE, Sport.FOOTBALL);
    }

    public FootballPlayer createFootballPlayer(long id, String firstName, Team team, int jerseyNumber) {
        FootballPlayer player = new FootballPlayer(firstName, "LastName", team, jerseyNumber, FootballPosition.MIDFIELDER);
        player.setId(id);
        return player;
    }
}
