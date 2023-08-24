package com.example.tournamentmaker.team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
class FootballTeamRequest {
    private final String teamName;
    private final List<FootballPlayerRequestWithoutGivingTeamName> players;
}
