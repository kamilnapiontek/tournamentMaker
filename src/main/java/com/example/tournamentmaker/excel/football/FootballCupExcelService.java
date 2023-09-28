package com.example.tournamentmaker.excel.football;

import com.example.tournamentmaker.constans.Constans;
import com.example.tournamentmaker.excel.ExcelStrategy;
import com.example.tournamentmaker.excel.ExcelUtil;
import com.example.tournamentmaker.team.Team;
import com.example.tournamentmaker.team.TeamRepository;
import com.example.tournamentmaker.tournament.Tournament;
import com.example.tournamentmaker.tournament.game.Game;
import com.example.tournamentmaker.tournament.round.Round;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class FootballCupExcelService implements ExcelStrategy {
    @Value("{application.excel.cup.path}")
    private String filePath;
    @Value("${application.excel.cup.path}")
    private String cupPath;
    private static final String SHEET_LADDER_NAME = "Tournament ladder";
    private static final int MAX_COLUMN_AMOUNT = 20;
    private static final int TEAM_COLUMN_EXCEL_WIDTH = 30;
    private static final int CONNECTING_COLUMN_EXCEL_WIDTH = 3;
    private final TeamRepository teamRepository;
    private final FootballStatisticWriterService footballStatisticWriterService;
    private final Workbook workbook = new XSSFWorkbook();
    private final Sheet sheetLadder = workbook.createSheet(SHEET_LADDER_NAME);

    @Override
    public boolean writeTournamentInformation(Tournament tournament) {
        CellStyle teamCellStyle = ExcelUtil.createCellStyle(workbook, IndexedColors.LEMON_CHIFFON.getIndex());
        CellStyle connectingCellStyle = ExcelUtil.createCellStyle(workbook, IndexedColors.GOLD.getIndex());

        List<Round> rounds = tournament.getRounds();
        int firstRoundGamesAmount = rounds.get(0).getGames().size();
        final int ROWS_NEEDED_FOR_ONE_TEAM = 4;
        int numerRowsToCreate = firstRoundGamesAmount * ROWS_NEEDED_FOR_ONE_TEAM - 1;

        ExcelUtil.createRows(sheetLadder, numerRowsToCreate);
        createTeamCells(rounds, teamCellStyle);
        connectTeamsWithColoredCells(rounds.size(), connectingCellStyle, numerRowsToCreate);
        setColumnsWidth();

        Sheet statisticsSheet = workbook.createSheet("Statistics");
        footballStatisticWriterService.writeStatistic(workbook, statisticsSheet, 0, 0, tournament);

        return ExcelUtil.createExcelFile(filePath, workbook);
    }

    private void addCupPicture(int col, int row) {
        try (InputStream inputStream = FootballCupExcelService.class.getClassLoader().
                getResourceAsStream(cupPath)) {

            assert inputStream != null;
            byte[] bytes = IOUtils.toByteArray(inputStream);
            int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);

            Drawing drawing = sheetLadder.createDrawingPatriarch();
            XSSFClientAnchor anchor = new XSSFClientAnchor();
            anchor.setCol1(col);
            anchor.setRow1(row);

            Picture picture = drawing.createPicture(anchor, pictureIdx);
            picture.resize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createTeamCells(List<Round> rounds, CellStyle style) {
        final int skippingColumnsBetweenRounds = 2;
        int rowNumber = 0;
        int columNumber = 0;
        int cellsToSkipOffTheTop = 0;
        int cellsToJumpBetweenTeams;
        int iterator = 0;

        for (Round round : rounds) {
            List<Game> games = round.getGames();
            games.sort(Comparator.comparingLong(Game::getId));

//             In each subsequent round, the gap between the targets should be doubled
//             starting with 2 jumps, then 4, 8, and so on
            cellsToJumpBetweenTeams = (int) Math.pow(2, iterator + 1);

            for (Game game : games) {
                fillTeamCell(style, rowNumber, columNumber, game.getHostId());
                rowNumber += cellsToJumpBetweenTeams;

                fillTeamCell(style, rowNumber, columNumber, game.getGuestId());
                rowNumber += cellsToJumpBetweenTeams;
            }
            columNumber += skippingColumnsBetweenRounds;

//            Skipping squares at the top is necessary because the square with the winning team will always
//            be between two competing teams. In the first round, you don't need to skip any squares
//            in the second round, you should skip one square, then three in the next round, then seven, and so on
            cellsToSkipOffTheTop += Math.pow(2, iterator);
            rowNumber = cellsToSkipOffTheTop;

            iterator++;
        }
        writeCellWinner(rounds, style, rowNumber, columNumber);
    }

    private void fillTeamCell(CellStyle style, int rowNumber, int columNumber, Long teamId) {
        Row row = sheetLadder.getRow(rowNumber);
        Cell cell = row.createCell(columNumber);
        if (teamId != null) {
            cell.setCellValue(findTeamNameById(teamId));
        }
        cell.setCellStyle(style);
    }

    private void connectTeamsWithColoredCells(int howManyRounds, CellStyle style, int numerRowsToCreate) {
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
                    Row row = sheetLadder.getRow(rowCount);
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

    private void setColumnsWidth() {
        for (int i = 0; i < MAX_COLUMN_AMOUNT; i++) {
            if (i % 2 == 0) {
                sheetLadder.setColumnWidth(i, TEAM_COLUMN_EXCEL_WIDTH * ExcelUtil.COLUMN_WIDTH_UNIT);
            } else {
                sheetLadder.setColumnWidth(i, CONNECTING_COLUMN_EXCEL_WIDTH * ExcelUtil.COLUMN_WIDTH_UNIT);
            }
        }
    }

    private void writeCellWinner(List<Round> rounds, CellStyle style, int rowCount, int columCount) {
        Row row = sheetLadder.getRow(rowCount);
        Cell cell = row.createCell(columCount);
        Round lastRound = rounds.get(rounds.size() - 1);
        if (!lastRound.getGames().isEmpty()) {
            Game finalGame = lastRound.getGames().get(0);
            cell.setCellValue(findWinnerTeamName(finalGame));
        }
        cell.setCellStyle(style);

        final int PICTURE_COLUMN_POSITION = columCount + 2;
        final int PICTURE_ROW_POSITION = rowCount - 2;
        addCupPicture(PICTURE_COLUMN_POSITION, PICTURE_ROW_POSITION);
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
