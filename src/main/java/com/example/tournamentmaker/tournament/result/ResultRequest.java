package com.example.tournamentmaker.tournament.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class ResultRequest {
    protected final String tournamentName;
    protected final Integer turn;
    protected final String hostName;
    protected final String guestName;
    protected final Integer hostPoints;
    protected final Integer guestPoints;
}
