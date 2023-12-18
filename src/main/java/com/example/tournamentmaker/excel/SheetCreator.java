package com.example.tournamentmaker.excel;

import com.example.tournamentmaker.tournament.Tournament;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public interface SheetCreator {
    Sheet fillSheet(Workbook workbook, Sheet sheet, Tournament tournament);
    String getName();
}
