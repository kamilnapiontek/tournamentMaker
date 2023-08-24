package com.example.tournamentmaker.excel;

import com.example.tournamentmaker.tournament.Tournament;

interface ExcelStrategy {
    boolean writeTournamentInformation(Tournament tournament);
}
