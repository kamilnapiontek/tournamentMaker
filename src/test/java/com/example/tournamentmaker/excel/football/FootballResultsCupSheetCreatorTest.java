package com.example.tournamentmaker.excel.football;

import com.example.tournamentmaker.team.Team;
import com.example.tournamentmaker.team.TeamRepository;
import com.example.tournamentmaker.tournament.Tournament;
import com.example.tournamentmaker.tournament.enums.TournamentType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.tournamentmaker.excel.football.FootballTournamentData.generateFakeImageBytes;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FootballResultsCupSheetCreatorTest {
    @Mock
    private TeamRepository teamRepository;
    @InjectMocks
    private FootballResultsCupSheetCreator sheetCreator;
    private static final Workbook workbook = new XSSFWorkbook();

    @Test
    void shouldCreateSheetForFootballResultsCup() throws IOException {
        // given
        Sheet sheet = workbook.createSheet();
        Tournament tournament = FootballTournamentData.createFootallTournament(TournamentType.CUP);
        List<Team> teamList = tournament.getTeamList();
        // when
        for (int i = 0; i < teamList.size(); i++) {
            when(teamRepository.findById(i + 1L)).thenReturn(Optional.of(teamList.get(i)));
        }
        sheetCreator.fillSheet(workbook, sheet, tournament);
        // then
        AtomicInteger rowNumber = new AtomicInteger(0);
        AtomicInteger colNumber = new AtomicInteger(0);

        List<String> expectedTeamNamesInFirstRound = List.of("AC Milan", "Ajax", "Arsenal", "Bayern Munich", "FC Barcelona",
                "Real Madrid", "Manchester United", "Zenit Saint Petersburg");

        expectedTeamNamesInFirstRound.forEach(expectedTeamName -> {
            assertEquals(expectedTeamName,
                    sheet.getRow(rowNumber.getAndAdd(2)).getCell(colNumber.get()).getStringCellValue());
        });

        colNumber.set(2);
        rowNumber.set(1);
        List<String> expectedTeamNamesInSecondRound = List.of("AC Milan", "Arsenal", "FC Barcelona", "Manchester United");

        expectedTeamNamesInSecondRound.forEach(expectedTeamName -> {
            assertEquals(expectedTeamName,
                    sheet.getRow(rowNumber.getAndAdd(4)).getCell(colNumber.get()).getStringCellValue());
        });

        colNumber.set(4);
        rowNumber.set(3);
        List<String> expectedTeamNamesInThirdRound = List.of("AC Milan", "FC Barcelona");
        expectedTeamNamesInThirdRound.forEach(expectedTeamName -> {
            assertEquals(expectedTeamName,
                    sheet.getRow(rowNumber.getAndAdd(8)).getCell(colNumber.get()).getStringCellValue());
        });

        String expectedWinner = "AC Milan";
        colNumber.set(6);
        rowNumber.set(7);
        assertEquals(expectedWinner, sheet.getRow(rowNumber.get()).getCell(colNumber.get()).getStringCellValue());
    }
}