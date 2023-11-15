package com.example.tournamentmaker.excel;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exel")
class ExcelController {
    private final ExcelService service;

    @PostMapping
    @PreAuthorize("hasAuthority('admin:update')")
    void writeTournamentInformationInExcel(@RequestBody String tournamentName) {
        service.writeTournamentInformationInExcel(tournamentName);
    }
}
