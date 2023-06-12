package com.example.tournamentMaker.team;

import com.example.tournamentMaker.team.player.FootballPosition;
import lombok.Getter;

@Getter
class FootballPlayerRequest extends PlayerRequest {
    private final Integer jerseyNumber;
    private final FootballPosition footballPosition;

    public FootballPlayerRequest(String teamName, String firstName, String lastName,
                                 Integer jerseyNumber, String footballPosition) {
        super(teamName, firstName, lastName);
        this.jerseyNumber = jerseyNumber;
        this.footballPosition = FootballPosition.valueOf(footballPosition);
    }
}
