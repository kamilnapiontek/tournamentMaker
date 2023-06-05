package com.example.tournamentMaker.tournament;

import com.example.tournamentMaker.tournament.enums.Sport;
import com.example.tournamentMaker.tournament.enums.Type;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
class TournamentRequest {
    private String name;
    private Sport sport;
    private Type type;

    @JsonProperty("Sport")
    public void setSport(String sport) {
        this.sport = Sport.valueOf(sport);
    }

    @JsonProperty("Type")
    public void setType(String type) {
        this.type = Type.valueOf(type);
    }
}
