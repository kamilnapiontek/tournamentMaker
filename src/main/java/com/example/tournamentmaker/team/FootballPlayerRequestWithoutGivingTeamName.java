package com.example.tournamentmaker.team;

import com.example.tournamentmaker.team.player.FootballPosition;

record FootballPlayerRequestWithoutGivingTeamName(
        String firstName, String lastName, Integer jerseyNumber, FootballPosition footballPosition) {
}
