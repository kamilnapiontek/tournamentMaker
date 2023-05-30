package com.example.tournamentMaker.admin;

import com.example.tournamentMaker.user.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
class SwitchRequest {
    private String email;
    private Role newRole;

    @JsonProperty("Role")
    public void setNewRole(String role) {
        this.newRole = Role.valueOf(role);
    }
}
