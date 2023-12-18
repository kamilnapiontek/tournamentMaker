package com.example.tournamentmaker.tournament.result;

import java.util.List;

public record FootballResultStatistics(List<Integer> shirtNumbersWithGoal,
                                       List<Integer> shirtNumbersWithYellowCard,
                                       List<Integer> shirtNumbersWithRedCard,
                                       List<Integer> shirtNumbersWithCleanSlate) {
}
