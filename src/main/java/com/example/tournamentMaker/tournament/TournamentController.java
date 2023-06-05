package com.example.tournamentMaker.tournament;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tournament")
@PreAuthorize("hasRole('ADMIN')")
public class TournamentController {
    private final TournamentService tournamentService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('admin:update')")
    public void createTournament(@RequestBody TournamentRequest tournamentRequest) {
        tournamentService.createTournament(tournamentRequest);
    }
}
