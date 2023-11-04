package com.example.tournamentmaker.excel;

import com.example.tournamentmaker.constans.Constans;
import com.example.tournamentmaker.excel.football.FootballExcelService;
import com.example.tournamentmaker.tournament.Tournament;
import com.example.tournamentmaker.tournament.TournamentRepository;
import com.example.tournamentmaker.tournament.enums.Sport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
class ExcelService {
    private final TournamentRepository tournamentRepository;
    private final FootballExcelService footballExcelService;
    private final Workbook workbook = new XSSFWorkbook();
    @Value("{application.excel.cup.path}")
    private String filePath;

    void writeTournamentInformationInExcel(String tournamentName) {
        Tournament tournament = tournamentRepository.findByName(tournamentName).orElseThrow(
                () -> new NoSuchElementException(Constans.NO_TOURNAMENT_FOUND));
        Sport sport = tournament.getSport();

        if (sport == Sport.FOOTBALL) {
            footballExcelService.fillWorkbook(workbook, tournament);
        }

        createExcelFile();
    }

    private void createExcelFile() {
        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(filePath))) {
            workbook.write(dataOutputStream);
        } catch (IOException e) {
            log.error("An error occurred while writing workbook to file");
        }
    }
}
