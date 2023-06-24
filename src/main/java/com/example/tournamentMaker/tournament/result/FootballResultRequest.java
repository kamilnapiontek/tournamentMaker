package com.example.tournamentMaker.tournament.result;

import lombok.Getter;

@Getter
public class FootballResultRequest extends ResultRequest {
    private final FootballResultStatistics hostStatistics;
    private final FootballResultStatistics guestStatistics;


    public FootballResultRequest(String tournamentName, Integer turn, String hostName, String guestName, Integer hostPoints, Integer gustPoints, FootballResultStatistics hostStatistics, FootballResultStatistics guestStatistics) {
        super(tournamentName, turn, hostName, guestName, hostPoints, gustPoints);
        this.hostStatistics = hostStatistics;
        this.guestStatistics = guestStatistics;
    }
}
