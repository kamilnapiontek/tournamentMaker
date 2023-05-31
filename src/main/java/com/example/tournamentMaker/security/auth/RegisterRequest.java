package com.example.tournamentMaker.security.auth;

import com.example.tournamentMaker.user.Role;
import lombok.*;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String city;
    private String street;
}
