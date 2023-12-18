package com.example.tournamentmaker.util;

import com.example.tournamentmaker.team.Team;
import com.example.tournamentmaker.tournament.Tournament;

import java.util.List;
import java.util.stream.IntStream;

import static com.example.tournamentmaker.util.TournamentUtil.createTournament;

public class TeamUtil {
    public static Team createTeamInNewTournament(String name) {
        return new Team(name, createTournament());
    }

    public static Team createTeam(long id, String name, Tournament tournament) {
        Team team = new Team(name, tournament);
        team.setId(id);
        return team;
    }

    public static void createTeams(Tournament tournament, int teamsNumber) {
        List<Team> teamList = IntStream.range(1, teamsNumber + 1)
                .mapToObj(i -> createTeam(i, "Team", tournament))
                .toList();
        tournament.getTeamList().addAll(teamList);
    }
}
