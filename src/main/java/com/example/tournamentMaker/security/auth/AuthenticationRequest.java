package com.example.tournamentMaker.security.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthenticationRequest {

    private String email;
    private String password;
}
