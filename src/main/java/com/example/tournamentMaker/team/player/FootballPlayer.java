package com.example.tournamentMaker.team.player;

import com.example.tournamentMaker.team.Team;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@DiscriminatorValue("FOOTBALL")
@Getter
@Setter
public class FootballPlayer extends Player {
    private Integer jerseyNumber;
    @Enumerated(EnumType.STRING)
    private FootballPosition footballPosition;

    public FootballPlayer(String firstName, String lastName, Team team, Integer jerseyNumber, FootballPosition footballPosition) {
        super(firstName, lastName, team);
        this.jerseyNumber = jerseyNumber;
        this.footballPosition = footballPosition;
    }
}
