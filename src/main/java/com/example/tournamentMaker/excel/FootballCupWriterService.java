package com.example.tournamentMaker.excel;

import com.example.tournamentMaker.constans.Constans;
import com.example.tournamentMaker.team.Team;
import com.example.tournamentMaker.team.TeamRepository;
import com.example.tournamentMaker.tournament.Tournament;
import com.example.tournamentMaker.tournament.game.Game;
import com.example.tournamentMaker.tournament.round.Round;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
@Slf4j
@Service
@RequiredArgsConstructor
class FootballCupWriterService implements ExcelWriteStrategy {
    @Value("${application.excel.file.path}")
    private String filePath;
    @Value("${application.excel.cup.path}")
    private String cupPath;
    private static final String SHEET_NAME = "Tournament ladder";
    private static final int MAX_COLUMN_AMOUNT = 20;
    private static final int TEAM_COLUMN_EXCEL_WIDTH = 30;
    private static final int CONNECTING_COLUMN_EXCEL_WIDTH = 3;
    private static final int COLUMN_WIDTH_UNIT = 256;
    private final TeamRepository teamRepository;
    private final StatisticWriterService statisticWriterService;

    @Override
    public boolean writeTournamentInformation(Tournament tournament) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(SHEET_NAME);

        CellStyle teamCellStyle = workbook.createCellStyle();
        teamCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        teamCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        ExcelUtil.setBorder(teamCellStyle, BorderStyle.THIN);

        CellStyle connectingCellStyle = workbook.createCellStyle();
        connectingCellStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        connectingCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        ExcelUtil.setBorder(connectingCellStyle, BorderStyle.THIN);

        List<Round> rounds = tournament.getRounds();
        int firstRoundGamesAmount = rounds.get(0).getGames().size();
        int numerRowsToCreate = firstRoundGamesAmount * 4 - 1;

        ExcelUtil.createRows(sheet, numerRowsToCreate);
        createTeamCells(workbook, sheet, rounds, teamCellStyle);
        connectTeamsWithColoredCells(sheet, rounds.size(), connectingCellStyle, numerRowsToCreate);
        setColumnWidth(sheet);

        XSSFSheet statisticsSheet = workbook.createSheet("Statistics");
        statisticWriterService.writeStatistic(statisticsSheet, 0, 0, tournament);

        return saveFile(workbook);
    }

    private void addCupPicture(XSSFWorkbook workbook, XSSFSheet sheet, int col, int row) {
        try (InputStream inputStream = FootballCupWriterService.class.getClassLoader().
                getResourceAsStream(cupPath)) {

            byte[] bytes = IOUtils.toByteArray(inputStream);
            int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);

            XSSFDrawing drawing = sheet.createDrawingPatriarch();
            XSSFClientAnchor anchor = new XSSFClientAnchor();
            anchor.setCol1(col);
            anchor.setRow1(row);

            Picture picture = drawing.createPicture(anchor, pictureIdx);
            picture.resize();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createTeamCells(XSSFWorkbook workbook, XSSFSheet sheet, List<Round> rounds, CellStyle style) {
        int rowCount = 0;
        int columCount = 0;
        int cellsToSkipOffTheTop = 0;
        int iterator = 0;
        int cellsToJumpBetweenTeams;

        for (Round round : rounds) {
            List<Game> games = round.getGames();
            games.sort(Comparator.comparingLong(Game::getId));

            cellsToJumpBetweenTeams = (int) Math.pow(2, iterator + 1);

            for (Game game : games) {
                Row row = sheet.getRow(rowCount);
                Cell cell = row.createCell(columCount);
                if (game.getHostId() != null) {
                    cell.setCellValue(findTeamNameById(game.getHostId()));
                }
                cell.setCellStyle(style);

                rowCount += cellsToJumpBetweenTeams;
                row = sheet.getRow(rowCount);
                cell = row.createCell(columCount);
                if (game.getGuestId() != null) {
                    cell.setCellValue(findTeamNameById(game.getGuestId()));
                }
                cell.setCellStyle(style);

                rowCount += cellsToJumpBetweenTeams;
            }
            columCount += 2;

            cellsToSkipOffTheTop += Math.pow(2, iterator);
            iterator++;
            rowCount = cellsToSkipOffTheTop;
        }
        writeWinner(workbook, sheet, rounds, style, rowCount, columCount);
    }

    private boolean saveFile(XSSFWorkbook workbook) {
        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(filePath))) {
            workbook.write(dataOutputStream);
            return true;
        } catch (IOException e) {
            log.error("An error occurred while writing workbook to file");
            return false;
        }
    }

    private void connectTeamsWithColoredCells(XSSFSheet sheet, int howManyRounds, CellStyle style, int numerRowsToCreate) {
        int columCount = 1;
        int rowCount = 0;
        int cellsToSkipOffTheTop = 0;

        int coloredCounter;
        int howManyToColorInRow;
        int cellsToSkip;
        boolean isBreakInPainting = true;

        for (int i = 0; i < howManyRounds; i++) {
            coloredCounter = 0;
            howManyToColorInRow = (int) Math.pow(2, i + 1) + 1;
            cellsToSkip = 0;
            for (int j = cellsToSkipOffTheTop; j < numerRowsToCreate - cellsToSkipOffTheTop; j++) {
                if (coloredCounter < howManyToColorInRow && cellsToSkip == 0) {
                    Row row = sheet.getRow(rowCount);
                    Cell cell = row.createCell(columCount);
                    cell.setCellStyle(style);
                    coloredCounter++;
                    isBreakInPainting = false;
                } else {
                    if (!isBreakInPainting) {
                        isBreakInPainting = true;
                        coloredCounter = 0;
                        cellsToSkip = (int) (cellsToSkipOffTheTop + Math.pow(2, i));
                    }
                    cellsToSkip--;
                }
                rowCount++;
            }
            columCount += 2;

            cellsToSkipOffTheTop += Math.pow(2, i);
            rowCount = cellsToSkipOffTheTop;
        }
    }

    private void setColumnWidth(XSSFSheet sheet) {
        for (int i = 0; i < MAX_COLUMN_AMOUNT; i++) {
            if (i % 2 == 0) {
                sheet.setColumnWidth(i, TEAM_COLUMN_EXCEL_WIDTH * COLUMN_WIDTH_UNIT);
            } else {
                sheet.setColumnWidth(i, CONNECTING_COLUMN_EXCEL_WIDTH * COLUMN_WIDTH_UNIT);
            }
        }
    }

    private void writeWinner(XSSFWorkbook workbook, XSSFSheet sheet, List<Round> rounds, CellStyle style, int rowCount, int columCount) {
        Row row = sheet.getRow(rowCount);
        Cell cell = row.createCell(columCount);
        Round lastRound = rounds.get(rounds.size() - 1);
        if (!lastRound.getGames().isEmpty()) {
            Game finalGame = lastRound.getGames().get(0);
            cell.setCellValue(findWinnerTeamName(finalGame));
        }
        cell.setCellStyle(style);
        addCupPicture(workbook, sheet, columCount + 2, rowCount - 2);
    }



    private String findTeamNameById(long id) {
        Team team = teamRepository.findById(id).orElseThrow(() -> {
            throw new NoSuchElementException(Constans.NO_TEAM_FOUND);
        });
        return team.getName();
    }

    private String findWinnerTeamName(Game game) {
        long winnerId = game.getHostPoints() > game.getGuestPoints() ? game.getHostId() : game.getGuestId();
        Team team = teamRepository.findById(winnerId).orElseThrow(() -> {
            throw new NoSuchElementException(Constans.NO_TEAM_FOUND);
        });
        return team.getName();
    }
}
