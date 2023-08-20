package com.example.tournamentMaker.excel;

import com.example.tournamentMaker.tournament.Tournament;
import org.springframework.stereotype.Service;

@Service
class FootballLeagueWriterService implements ExcelWriteStrategy {
    @Override
    public boolean writeTournamentInformation(Tournament tournament) {
        return false;
    }
}
