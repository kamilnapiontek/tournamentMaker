package com.example.tournamentmaker.constans;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constans {
    public static final String NO_TOURNAMENT_FOUND = "No tournament with the given name was found";
    public static final String NO_TEAM_FOUND = "The team could not be found";
    public static final int MINIMUM_TEAMS_NUMBER = 2;
    public static final int COLLECTED_MATCH_RESULTS_NUMBER = 5;
    public static final int POINTS_FOR_WINNING_IN_FOOTBALL = 3;
}
