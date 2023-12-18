package com.example.tournamentmaker.tournament.result;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class FootballResultRequest extends ResultRequest {
    private final FootballResultStatistics hostStatistics;
    private final FootballResultStatistics guestStatistics;
}
