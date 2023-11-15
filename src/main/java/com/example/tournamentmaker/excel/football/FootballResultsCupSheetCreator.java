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
import static com.example.tournamentmaker.excel.football.FootballExcelUtil.createCellsNumberToColorInRow;
import static com.example.tournamentmaker.excel.football.FootballExcelUtil.createCellsToSkipList;

@Service
@RequiredArgsConstructor
public class FootballResultsCupSheetCreator implements FootballResultsCreator {
    @Value("${application.excel.cup.path}")
    private String cupPath;
    private final TeamRepository teamRepository;
    private static final int MAX_COLUMN_AMOUNT = 20;
    private static final int TEAM_COLUMN_EXCEL_WIDTH = 30;
    private static final int CONNECTING_COLUMN_EXCEL_WIDTH = 3;
    private static final int ROWS_NEEDED_FOR_ONE_GAME = 4;
    private int pictureColumnPosition;
    private int pictureRowPosition;
    private CellStyle teamCellStyle;
    private CellStyle connectingCellStyle;

    @Override
    public Sheet fillSheet(Workbook workbook, Sheet sheet, Tournament tournament) {
        teamCellStyle = createCellStyle(workbook, IndexedColors.LEMON_CHIFFON.getIndex());
        connectingCellStyle = createCellStyle(workbook, IndexedColors.GOLD.getIndex());

        List<Round> rounds = tournament.getRounds();
        final int firstRoundGamesAmount = rounds.get(0).getGames().size();
        final int numberRowsToCreate = firstRoundGamesAmount * ROWS_NEEDED_FOR_ONE_GAME - 1;
        createNeededRows(sheet, numberRowsToCreate);
        setColumnsWidth(sheet);

        createTeamCells(sheet, rounds);
        connectTeamCellsWithColoredCells(sheet, rounds.size());
//        addCupPicture(workbook, sheet, pictureColumnPosition, pictureRowPosition); test nie działa przez dodanie zdjecia
        return sheet;
    }

    private void createTeamCells(Sheet sheet, List<Round> rounds) {
        final int skippingColumnsBetweenRounds = 2;
        int rowNumber = 0;
        int columNumber = 0;
        int cellsToJumpBetweenTeams = 1;
        List<Integer> cellsToSkipOffTheTop = createCellsToSkipList(rounds.size());
        int cellsToSkipIndex = 0;

        for (Round round : rounds) {
            List<Game> games = round.getGames();
            games.sort(Comparator.comparingLong(Game::getId));

            cellsToJumpBetweenTeams *= 2;

            for (Game game : games) {
                fillTeamCell(sheet, rowNumber, columNumber, game.getHostId());
                rowNumber += cellsToJumpBetweenTeams;

                fillTeamCell(sheet, rowNumber, columNumber, game.getGuestId());
                rowNumber += cellsToJumpBetweenTeams;
            }
            columNumber += skippingColumnsBetweenRounds;
            rowNumber = cellsToSkipOffTheTop.get(cellsToSkipIndex);
            cellsToSkipIndex++;
        }
        generateCellForTournamentWinner(sheet, rounds, rowNumber, columNumber);
    }

    private void generateCellForTournamentWinner(Sheet sheet, List<Round> rounds, int rowCount, int columCount) {
        Row row = sheet.getRow(rowCount);
        Cell cell = row.createCell(columCount);
        Round lastRound = rounds.get(rounds.size() - 1);
        if (!lastRound.getGames().isEmpty()) {
            Game finalGame = lastRound.getGames().get(0);
            cell.setCellValue(findWinnerTeamName(finalGame));
        }
        cell.setCellStyle(teamCellStyle);

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

    private void fillTeamCell(Sheet sheet, int rowNumber, int columNumber, Long teamId) {
        Row row = sheet.getRow(rowNumber);
        Cell cell = row.createCell(columNumber);
        if (teamId != null) {
            cell.setCellValue(findTeamNameById(teamId));
        }
        cell.setCellStyle(teamCellStyle);
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

            Drawing<?> drawing = sheet.createDrawingPatriarch();
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

    private void connectTeamCellsWithColoredCells(Sheet sheet, int howManyRounds) {
        final int skippingColumnsBetweenRounds = 2;
        List<Integer> cellsNumberToSkipOffTheTopAndBottom = createCellsToSkipList(howManyRounds);
        List<Integer> gamesCountToColorInRow = createCellsNumberToColorInRow(howManyRounds);
        int howManyPaintingsInRound = (int) Math.pow(2, howManyRounds - 1); // ???
        int columCount = 1;
        int rowCount = 0;

        for (int i = 0; i < howManyRounds; i++) {
            for (int j = 0; j < howManyPaintingsInRound; j++) {
                rowCount = fillConnectCells(sheet, gamesCountToColorInRow, columCount, rowCount, i);
                rowCount += cellsNumberToSkipOffTheTopAndBottom.get(i);
            }
            howManyPaintingsInRound = howManyPaintingsInRound / 2;
            rowCount = cellsNumberToSkipOffTheTopAndBottom.get(i);
            columCount += skippingColumnsBetweenRounds;
        }
    }

    private int fillConnectCells(Sheet sheet, List<Integer> gamesCountToColorInRow, int colCount, int rowCount, int gameIndex) {
        for (int i = 0; i < gamesCountToColorInRow.get(gameIndex); i++) {
            fillColoredCell(sheet, colCount, rowCount);
            rowCount++;
        }
        return rowCount;
    }

    private void fillColoredCell(Sheet sheet, int columCount, int rowCount) {
        Row row = sheet.getRow(rowCount);
        Cell cell = row.createCell(columCount);
        cell.setCellStyle(connectingCellStyle);
    }
}
