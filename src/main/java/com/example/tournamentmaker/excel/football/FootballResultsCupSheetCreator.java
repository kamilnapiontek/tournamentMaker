package com.example.tournamentmaker.excel.football;

import com.example.tournamentmaker.constans.Constans;
import com.example.tournamentmaker.team.Team;
import com.example.tournamentmaker.team.TeamRepository;
import com.example.tournamentmaker.tournament.Tournament;
import com.example.tournamentmaker.tournament.game.Game;
import com.example.tournamentmaker.tournament.round.Round;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import static com.example.tournamentmaker.excel.ExcelUtil.*;

@Service
@RequiredArgsConstructor
public class FootballResultsCupSheetCreator implements FootballResultsCreator {
    @Value("${application.excel.cup.path}")
    private String cupPath;
    private final TeamRepository teamRepository;
    private static final int MAX_COLUMN_AMOUNT = 20;
    private static final int TEAM_COLUMN_EXCEL_WIDTH = 30;
    private static final int CONNECTING_COLUMN_EXCEL_WIDTH = 3;
    private int pictureColumnPosition;
    private int pictureRowPosition;

    @Override
    public Sheet fillSheet(Workbook workbook, Sheet sheet, Tournament tournament) {
        CellStyle teamCellStyle = createCellStyle(workbook, IndexedColors.LEMON_CHIFFON.getIndex());
        CellStyle connectingCellStyle = createCellStyle(workbook, IndexedColors.GOLD.getIndex());

        List<Round> rounds = tournament.getRounds();
        final int firstRoundGamesAmount = rounds.get(0).getGames().size();
        final int ROWS_NEEDED_FOR_ONE_TEAM = 4;
        final int numberRowsToCreate = firstRoundGamesAmount * ROWS_NEEDED_FOR_ONE_TEAM - 1;
        createNeededRows(sheet, numberRowsToCreate);
        setColumnsWidth(sheet);

        createTeamCells(sheet, rounds, teamCellStyle);
        connectTeamCellsWithColoredCells(sheet, rounds.size(), connectingCellStyle, numberRowsToCreate);
        addCupPicture(workbook, sheet, pictureColumnPosition, pictureRowPosition);
        return sheet;
    }

    private void createTeamCells(Sheet sheet, List<Round> rounds, CellStyle style) {
        final int skippingColumnsBetweenRounds = 2;
        int rowNumber = 0;
        int columNumber = 0;
        int cellsToSkipOffTheTop = 0;
        int cellsToJumpBetweenTeams;
        int numberRaisedToThePower = 0;

        for (Round round : rounds) {
            List<Game> games = round.getGames();
            games.sort(Comparator.comparingLong(Game::getId));

//             In each subsequent round, the gap between the targets should be doubled
//             starting with 2 jumps, then 4, 8, and so on
            cellsToJumpBetweenTeams = (int) Math.pow(2, numberRaisedToThePower + 1);

            for (Game game : games) {
                fillTeamCell(sheet, style, rowNumber, columNumber, game.getHostId());
                rowNumber += cellsToJumpBetweenTeams;

                fillTeamCell(sheet, style, rowNumber, columNumber, game.getGuestId());
                rowNumber += cellsToJumpBetweenTeams;
            }
            columNumber += skippingColumnsBetweenRounds;

//            Skipping squares at the top is necessary because the square with the winning team will always
//            be between two competing teams. In the first round, you don't need to skip any squares
//            in the second round, you should skip one square, then three in the next round, then seven, and so on
            cellsToSkipOffTheTop += Math.pow(2, numberRaisedToThePower);
            rowNumber = cellsToSkipOffTheTop;

            numberRaisedToThePower++;
        }
        writeCellWinner(sheet, rounds, style, rowNumber, columNumber);
    }

    private void writeCellWinner(Sheet sheet, List<Round> rounds, CellStyle style, int rowCount, int columCount) {
        Row row = sheet.getRow(rowCount);
        Cell cell = row.createCell(columCount);
        Round lastRound = rounds.get(rounds.size() - 1);
        if (!lastRound.getGames().isEmpty()) {
            Game finalGame = lastRound.getGames().get(0);
            cell.setCellValue(findWinnerTeamName(finalGame));
        }
        cell.setCellStyle(style);

        pictureColumnPosition = columCount + 2;
        pictureRowPosition = rowCount - 2;
    }

    private String findWinnerTeamName(Game game) {
        long winnerId = game.getHostPoints() > game.getGuestPoints() ? game.getHostId() : game.getGuestId();
        Team team = teamRepository.findById(winnerId).orElseThrow(() -> {
            throw new NoSuchElementException(Constans.NO_TEAM_FOUND);
        });
        return team.getName();
    }

    private void fillTeamCell(Sheet sheet, CellStyle style, int rowNumber, int columNumber, Long teamId) {
        Row row = sheet.getRow(rowNumber);
        Cell cell = row.createCell(columNumber);
        if (teamId != null) {
            cell.setCellValue(findTeamNameById(teamId));
        }
        cell.setCellStyle(style);
    }

    private String findTeamNameById(long id) {
        Team team = teamRepository.findById(id).orElseThrow(() -> {
            throw new NoSuchElementException(Constans.NO_TEAM_FOUND);
        });
        return team.getName();
    }

    private void addCupPicture(Workbook workbook, Sheet sheet, int col, int row) {
        try (InputStream inputStream = FootballResultsCupSheetCreator.class.getClassLoader().getResourceAsStream(cupPath)) {

            assert inputStream != null;
            byte[] bytes = IOUtils.toByteArray(inputStream);
            int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);

            Drawing drawing = sheet.createDrawingPatriarch();
            XSSFClientAnchor anchor = new XSSFClientAnchor();
            anchor.setCol1(col);
            anchor.setRow1(row);

            Picture picture = drawing.createPicture(anchor, pictureIdx);
            picture.resize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setColumnsWidth(Sheet sheet) {
        for (int i = 0; i < MAX_COLUMN_AMOUNT; i++) {
            if (i % 2 == 0) {
                sheet.setColumnWidth(i, TEAM_COLUMN_EXCEL_WIDTH * COLUMN_WIDTH_UNIT);
            } else {
                sheet.setColumnWidth(i, CONNECTING_COLUMN_EXCEL_WIDTH * COLUMN_WIDTH_UNIT);
            }
        }
    }

    private void connectTeamCellsWithColoredCells(Sheet sheet, int howManyRounds, CellStyle style, int numerRowsToCreate) {
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
}
