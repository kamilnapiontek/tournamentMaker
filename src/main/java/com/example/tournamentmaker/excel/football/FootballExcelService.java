package com.example.tournamentmaker.excel.football;

import com.example.tournamentmaker.excel.SheetCreator;
import com.example.tournamentmaker.tournament.Tournament;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FootballExcelService {
    private final FootballResultsSheetCreator footballResultsSheetCreator;
    private final FootballStatisticsSheetCreator footballStatisticsSheetCreator;
    private List<SheetCreator> sheetList;

    @PostConstruct
    private void initSheetList() {
        sheetList = List.of(footballResultsSheetCreator, footballStatisticsSheetCreator);
    }

    public void fillWorkbook(Workbook workbook, Tournament tournament) {
        for (SheetCreator sheetCreator : sheetList) {
            sheetCreator.fillSheet(workbook, workbook.createSheet(sheetCreator.getName()), tournament);
        }
    }
}
