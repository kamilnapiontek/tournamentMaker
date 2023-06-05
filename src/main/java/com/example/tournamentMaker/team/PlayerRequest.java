package com.example.tournamentMaker.team;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public abstract class PlayerRequest {
    private String teamName;
    private String firstName;
    private String lastName;
}
