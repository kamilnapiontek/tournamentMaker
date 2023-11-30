package com.example.tournamentmaker.tournament.result;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ResultServiceTestData {
    static FootballResultRequest createFootballResultRequest(String tournamentName, int turnNumber, String hostName,
                                                             String guestName) {
        int hostPoints = 0;
        int guestPoints = 2;
        int hostPlayer1 = 1;
        int hostPlayer2 = 7;
        int guestPlayer1 = 1;
        int guestPlayer2 = 10;

        final List<Integer> hostShirtNumbersWithGoal = new ArrayList<>();
        final List<Integer> hostShirtNumbersWithYellowCard = List.of(hostPlayer2);
        final List<Integer> hostShirtNumbersWithRedCard = List.of(hostPlayer1);
        final List<Integer> hostShirtWithCleanSlate = new ArrayList<>();
        final List<Integer> guestShirtNumbersWithGoal = List.of(guestPlayer1, guestPlayer2);
        final List<Integer> guestShirtNumbersWithYellowCard = new ArrayList<>();
        final List<Integer> guestShirtNumbersWithRedCard = new ArrayList<>();
        final List<Integer> guestShirtNumbersWithCleanSlate = List.of(guestPlayer1);

        FootballResultStatistics hostResult = createFootballResultStatistics(hostShirtNumbersWithGoal,
                hostShirtNumbersWithYellowCard, hostShirtNumbersWithRedCard, hostShirtWithCleanSlate);
        FootballResultStatistics guestResult = createFootballResultStatistics(guestShirtNumbersWithGoal,
                guestShirtNumbersWithYellowCard, guestShirtNumbersWithRedCard, guestShirtNumbersWithCleanSlate);

        return FootballResultRequest.builder()
                .tournamentName(tournamentName)
                .turn(turnNumber)
                .hostStatistics(hostResult)
                .guestStatistics(guestResult)
                .hostName(hostName)
                .hostPoints(hostPoints)
                .guestName(guestName)
                .guestPoints(guestPoints).build();
    }

    static private FootballResultStatistics createFootballResultStatistics(List<Integer> shirtNumbersWithGoal,
                                                                           List<Integer> shirtNumbersWithYellowCard,
                                                                           List<Integer> shirtNumbersWithRedCard,
                                                                           List<Integer> shirtWithCleanSlate) {
        return new FootballResultStatistics(
                shirtNumbersWithGoal, shirtNumbersWithYellowCard, shirtNumbersWithRedCard, shirtWithCleanSlate);
    }
}
