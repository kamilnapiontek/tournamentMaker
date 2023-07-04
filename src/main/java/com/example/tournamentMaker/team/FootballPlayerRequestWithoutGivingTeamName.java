package com.example.tournamentMaker.team;

import com.example.tournamentMaker.team.player.FootballPosition;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FootballPlayerRequestWithoutGivingTeamName {
    private String firstName;
    private String lastName;
    private final Integer jerseyNumber;
    private final FootballPosition footballPosition;
}
