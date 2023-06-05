package com.example.tournamentMaker.team;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/team")
@PreAuthorize("hasRole('ADMIN')")
public class TeamController {
    private final TeamService teamService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('admin:update')")
    public void createTeam(@RequestBody TeamRequest teamRequest) {
        teamService.createTeam(teamRequest);
    }
    @PostMapping("/create-player-add-to-team")
    @PreAuthorize("hasAuthority('admin:update')")
    public void addFootballPlayer(@RequestBody FootballPlayerRequest footballPlayerRequest) {
        teamService.addFootballPlayer(footballPlayerRequest);
    }
}
