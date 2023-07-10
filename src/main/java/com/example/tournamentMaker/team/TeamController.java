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
class TeamController {
    private final TeamService teamService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('admin:update')")
    void createTeam(@RequestBody TeamRequest teamRequest) {
        teamService.createTeam(teamRequest);
    }
    @PostMapping("/create-player-add-to-team")
    @PreAuthorize("hasAuthority('admin:update')")
    void addFootballPlayer(@RequestBody FootballPlayerRequest footballPlayerRequest) {
        teamService.addFootballPlayer(footballPlayerRequest);
    }

    @PostMapping("/createFootballTeamsWithPlayers")
    @PreAuthorize("hasAuthority('admin:update')")
    void createFootballTeamsWithPlayers(@RequestBody FootballTeamsAndPlayersRequest footballTeamsAndPlayersRequest) {
        teamService.createFootballTeamsWithPlayers(footballTeamsAndPlayersRequest);
    }
}
