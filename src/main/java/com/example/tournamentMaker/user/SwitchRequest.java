package com.example.tournamentMaker.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
class SwitchRequest {
    private String adminEmail;
    private String switchEmail;
}
