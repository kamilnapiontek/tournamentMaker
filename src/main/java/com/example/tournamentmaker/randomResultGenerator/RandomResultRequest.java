package com.example.tournamentmaker.randomResultGenerator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
class RandomResultRequest {
    private final String tournamentName;
    private final String roundsToDraw;
}
