package com.example.tournamentmaker.excel;

import com.example.tournamentmaker.tournament.Tournament;

public interface ExcelStrategy {
    boolean writeTournamentInformation(Tournament tournament);
}
