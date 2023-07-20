package com.example.tournamentMaker.randomResultGenerator;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/randomGenerator")
class RandomResultController {
    private final RandomResultService randomResultService;
    @PostMapping
    @PreAuthorize("hasAuthority('admin:update')")
    void drawLotRoundsResults(@RequestBody RandomResultRequest randomResultRequest) {
        randomResultService.drawLotRoundsResults(randomResultRequest);
    }

}
