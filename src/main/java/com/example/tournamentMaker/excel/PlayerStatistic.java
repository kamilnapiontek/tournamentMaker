package com.example.tournamentMaker.excel;

import lombok.Getter;

@Getter
public class PlayerStatistic {
    private final String name;
    private final int result;

    public PlayerStatistic(String name, int result) {
        this.name = name;
        this.result = result;
    }
}
