package com.example.tournamentMaker.tournament.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class ResultRequest {
    private final String tournamentName;
    private Integer turn;
    private final String hostName;
    private final String guestName;
    private final Integer hostPoints;
    private final Integer guestPoints;
}
