package com.example.tournamentMaker.team;

import com.example.tournamentMaker.team.player.FootballPosition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
class FootballPlayerRequestWithoutGivingTeamName {
    private final String firstName;
    private final String lastName;
    private final Integer jerseyNumber;
    private final FootballPosition footballPosition;
}
