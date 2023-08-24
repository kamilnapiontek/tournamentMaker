package com.example.tournamentmaker.tournament.result;

import lombok.Getter;

import java.util.List;
@Getter
public class FootballResultStatistics {
    private final List<Integer> shirtNumbersWithGoal;
    private final List<Integer> shirtNumbersWithYellowCard;
    private final List<Integer> shirtNumbersWithRedCard;
    private final List<Integer> shirtNumbersWithCleanSlate;


    public FootballResultStatistics(List<Integer> shirtNumbersWithGoal, List<Integer> shirtNumbersWithYellowCard, List<Integer> shirtNumbersWithRedCard, List<Integer> shirtNumbersWithCleanSlate) {
        this.shirtNumbersWithGoal = shirtNumbersWithGoal;
        this.shirtNumbersWithYellowCard = shirtNumbersWithYellowCard;
        this.shirtNumbersWithRedCard = shirtNumbersWithRedCard;
        this.shirtNumbersWithCleanSlate = shirtNumbersWithCleanSlate;
    }
}
