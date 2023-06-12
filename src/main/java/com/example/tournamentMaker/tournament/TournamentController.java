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
class TournamentController {
    private final TournamentService tournamentService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('admin:update')")
    void createTournament(@RequestBody TournamentRequest tournamentRequest) {
        tournamentService.createTournament(tournamentRequest);
    }
//    @PreAuthorize("hasAnyRole('ADMIN','DB-ADMIN')")
    @PostMapping("/finishRegistration")
    @PreAuthorize("hasAuthority('admin:update')")
    void finishRegistration(@RequestBody String tournamentName) {
        tournamentService.finishRegistration(tournamentName);
    }
    @PostMapping("/createSchedule")
    @PreAuthorize("hasAuthority('admin:update')")
    void createSchedule(@RequestBody String tournamentName) {
        tournamentService.createSchedule(tournamentName);
    }

}
