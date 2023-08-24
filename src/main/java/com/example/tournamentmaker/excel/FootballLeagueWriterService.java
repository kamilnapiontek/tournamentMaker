package com.example.tournamentmaker.excel;

import com.example.tournamentmaker.tournament.Tournament;
import org.springframework.stereotype.Service;

@Service
class FootballLeagueWriterService implements ExcelStrategy {
    @Override
    public boolean writeTournamentInformation(Tournament tournament) {
        return false;
    }
}
