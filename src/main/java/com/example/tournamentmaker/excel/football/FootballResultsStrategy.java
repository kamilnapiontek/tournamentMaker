package com.example.tournamentmaker.excel.football;

import com.example.tournamentmaker.tournament.Tournament;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public interface FootballResultsStrategy {
    Sheet fillSheet(Workbook workbook, Sheet sheet, Tournament tournament);

    void setColumnsWidth(Sheet sheet);
}
