package com.example.tournamentmaker.team;

import java.util.List;

record FootballTeamRequest(String teamName, List<FootballPlayerRequestWithoutGivingTeamName> players) {
}
