package com.example.tournamentMaker.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String city;
    private String street;
    private String email;
    private String password;
}