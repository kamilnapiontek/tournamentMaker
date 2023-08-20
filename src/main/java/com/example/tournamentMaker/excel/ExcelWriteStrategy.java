package com.example.tournamentMaker.excel;

import com.example.tournamentMaker.tournament.Tournament;

interface ExcelWriteStrategy {
    boolean writeTournamentInformation(Tournament tournament);
}
