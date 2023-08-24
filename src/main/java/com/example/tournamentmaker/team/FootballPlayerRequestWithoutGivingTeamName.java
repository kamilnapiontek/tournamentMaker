package com.example.tournamentmaker.team;

import com.example.tournamentmaker.team.player.FootballPosition;
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
