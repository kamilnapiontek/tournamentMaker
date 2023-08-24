package com.example.tournamentmaker.tournament;

import com.example.tournamentmaker.tournament.result.FootballResultRequest;
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
    boolean finishRegistration(@RequestBody String tournamentName) {
        return tournamentService.finishRegistration(tournamentName);
    }
    @PostMapping("/createSchedule")
    @PreAuthorize("hasAuthority('admin:update')")
    void createSchedule(@RequestBody String tournamentName) {
        tournamentService.createSchedule(tournamentName);
    }

    @PostMapping("/launchFootballResult")
    @PreAuthorize("hasAuthority('admin:update')")
    void launchFootballResult(@RequestBody FootballResultRequest footballResultRequest) {
        tournamentService.launchFootballResult(footballResultRequest);
    }

}
