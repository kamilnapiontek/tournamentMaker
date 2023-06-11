package com.example.tournamentMaker.tournament;

import com.example.tournamentMaker.tournament.enums.Sport;
import com.example.tournamentMaker.tournament.enums.TournamentType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
class TournamentRequest {
    private String name;
    private Sport sport;
    private TournamentType tournamentType;

    @JsonProperty("sport")
    public void setSport(String sport) {
        this.sport = Sport.valueOf(sport);
    }

    @JsonProperty("type")
    public void setTournamentType(String type) {
        this.tournamentType = TournamentType.valueOf(type);
    }
}
