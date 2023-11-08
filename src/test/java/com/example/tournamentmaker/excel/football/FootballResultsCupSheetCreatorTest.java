package com.example.tournamentmaker.excel.football;

import com.example.tournamentmaker.team.TeamRepository;
import com.example.tournamentmaker.tournament.Tournament;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FootballResultsCupSheetCreatorTest {
    @Mock
    private TeamRepository teamRepository;
    @InjectMocks
    private FootballResultsCupSheetCreator sheetCreator;
    private static final Workbook workbook = new XSSFWorkbook();
    @Test
    void shouldCreateSheetForFootballResultsCup() {
        // given
        Sheet sheet = workbook.createSheet();
        Tournament tournament = new Tournament();
        // when
        sheetCreator.fillSheet(workbook, sheet, tournament);
        // then

    }

    private boolean wymyslNazwe(Sheet sheet) {
        return true;
    }
}