package com.example.tournamentMaker.team;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
class TeamRequest {
    private String tournamentName;
    private String teamName;
}