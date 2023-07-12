package com.example.tournamentMaker.randomResultGenerator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
class RandomResultRequest {
    private final String tournamentName;
    private final String roundsToDraw;
}
