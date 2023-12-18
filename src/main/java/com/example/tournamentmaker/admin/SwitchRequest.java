package com.example.tournamentmaker.admin;

import com.example.tournamentmaker.user.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SwitchRequest {
    private final String email;
    private Role newRole;

    @JsonProperty("Role")
    public void setNewRole(String role) {
        this.newRole = Role.valueOf(role);
    }
}
