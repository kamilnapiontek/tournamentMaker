package com.example.tournamentMaker.team;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
class FootballTeamRequest {
    private String teamName;
    private List<FootballPlayerRequestWithoutGivingTeamName> players;
}
