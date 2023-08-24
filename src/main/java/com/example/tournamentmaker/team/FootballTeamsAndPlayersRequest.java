package com.example.tournamentmaker.team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
class FootballTeamsAndPlayersRequest {
    private final String tournamentName;
    private final List<FootballTeamRequest> teams;
}
