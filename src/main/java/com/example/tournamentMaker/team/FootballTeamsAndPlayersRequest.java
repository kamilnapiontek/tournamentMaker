package com.example.tournamentMaker.team;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
class FootballTeamsAndPlayersRequest {
    private String tournamentName;
    private List<FootballTeamRequest> teams;
}
