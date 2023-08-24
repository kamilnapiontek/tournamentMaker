package com.example.tournamentmaker.security.auth;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RegisterRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String city;
    private String street;
}
