package com.example.tournamentmaker.util;

import com.example.tournamentmaker.team.Team;
import com.example.tournamentmaker.team.player.FootballPlayer;
import com.example.tournamentmaker.team.player.FootballPosition;
import com.example.tournamentmaker.tournament.Tournament;
import com.example.tournamentmaker.tournament.enums.Sport;
import com.example.tournamentmaker.tournament.enums.TournamentType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Util {
    public static Team createTeam(String name) {
        return new Team(name, createTournament());
    }

    public static Team createTeam(long id, String name, Tournament tournament) {
        Team team = new Team(name, tournament);
        team.setId(id);
        return team;
    }

    public static void createTeams(Tournament tournament, int teamsNumber) {
        List<Team> teamList = IntStream.range(1, teamsNumber + 1)
                .mapToObj(i -> Util.createTeam(i, "Team", tournament))
                .toList();
        tournament.getTeamList().addAll(teamList);
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
