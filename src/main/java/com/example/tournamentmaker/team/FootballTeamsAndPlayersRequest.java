package com.example.tournamentmaker.team;

import java.util.List;

record FootballTeamsAndPlayersRequest(String tournamentName, List<FootballTeamRequest> teams) {
}
