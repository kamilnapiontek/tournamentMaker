package com.example.tournamentMaker.team;

import com.example.tournamentMaker.team.player.FootballPosition;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
class FootballPlayerRequest {
    private String teamName;
    private String firstName;
    private String lastName;
    private Integer jerseyNumber;
    private FootballPosition footballPosition;

    @JsonProperty("FootballPosition")
    public void setFootballPosition(String position) {
        this.footballPosition = FootballPosition.valueOf(position);
    }
}
