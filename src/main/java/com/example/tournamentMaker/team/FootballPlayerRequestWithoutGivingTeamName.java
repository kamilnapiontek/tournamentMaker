package com.example.tournamentMaker.team;

import com.example.tournamentMaker.team.player.FootballPosition;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
class FootballPlayerRequestWithoutGivingTeamName {
    private final String firstName;
    private final String lastName;
    private final Integer jerseyNumber;
    private final FootballPosition footballPosition;
}
