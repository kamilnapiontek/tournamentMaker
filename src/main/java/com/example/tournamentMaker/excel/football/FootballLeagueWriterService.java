package com.example.tournamentmaker.excel.football;

import com.example.tournamentmaker.excel.ExcelStrategy;
import com.example.tournamentmaker.excel.ExcelUtil;
import com.example.tournamentmaker.statistics.StatisticService;
import com.example.tournamentmaker.statistics.Statistics;
import com.example.tournamentmaker.tournament.Tournament;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FootballLeagueWriterService implements ExcelStrategy {
    private String filePath = "D:\\tournamentMaker.xlsx";
    private static final String SHEET_NAME = "League";
    private final FootballStatisticWriterService footballStatisticWriterService;
    private final Workbook workbook = new XSSFWorkbook();
    private final Sheet sheet = workbook.createSheet(SHEET_NAME);
    private static final int TEAM_COLUMN_EXCEL_WIDTH = 26;
    private static final int COLUMN_WITH_NUMBER_EXCEL_WIDTH = 5;
    private static final int LAST_5_COLUMN_EXCEL_WIDTH = 12;
    private static final int STATISTICS_COLUMN_START = 9;
    private static final int STATISTICS_ROW_START = 0;
    private final StatisticService statisticService = new StatisticService();

    @Override
    public boolean writeTournamentInformation(Tournament tournament) {
        CellStyle borderCellStyle = ExcelUtil.createCellStyle(workbook, IndexedColors.GOLD.getIndex());
        CellStyle fillingTableCellStyle = ExcelUtil.createCellStyle(workbook, IndexedColors.LEMON_CHIFFON.getIndex());

        List<Statistics> statistics = tournament.getTeamList().stream()
                .map(team -> team.getStatistics())
                .sorted(Comparator.comparingInt(Statistics::getPoints).reversed())
                .collect(Collectors.toList());

        int rowStart = 0;
        int colStart = 0;
        createColumnHeaders(rowStart, colStart, borderCellStyle);
        rowStart++;

        Font greenFont = workbook.createFont();
        greenFont.setColor(IndexedColors.GREEN.getIndex());
        Font redFont = workbook.createFont();
        redFont.setColor(IndexedColors.RED.getIndex());
        Font blueFont = workbook.createFont();
        blueFont.setColor(IndexedColors.BLUE.getIndex());

        for (int i = 0; i < statistics.size(); i++) {
            Statistics stats = statistics.get(i);
            Row row = sheet.createRow(rowStart);

            Cell cell = row.createCell(colStart);
            cell.setCellStyle(fillingTableCellStyle);
            int place = i + 1;
            cell.setCellValue(place);
            colStart++;

            cell = row.createCell(colStart);
            cell.setCellStyle(fillingTableCellStyle);
            cell.setCellValue(stats.getTeam().getName());
            colStart++;

            cell = row.createCell(colStart);
            cell.setCellStyle(fillingTableCellStyle);
            cell.setCellValue(statisticService.getGamesCount(stats));
            colStart++;

            cell = row.createCell(colStart);
            cell.setCellStyle(fillingTableCellStyle);
            cell.setCellValue(stats.getCountWins());
            colStart++;

            cell = row.createCell(colStart);
            cell.setCellStyle(fillingTableCellStyle);
            cell.setCellValue(stats.getCountDraws());
            colStart++;

            cell = row.createCell(colStart);
            cell.setCellStyle(fillingTableCellStyle);
            cell.setCellValue(stats.getCountLoses());
            colStart++;

            cell = row.createCell(colStart);
            cell.setCellStyle(fillingTableCellStyle);
            cell.setCellValue(stats.getPoints());
            colStart++;

            cell = row.createCell(colStart);
            cell.setCellStyle(fillingTableCellStyle);
            String lastResults = statisticService.getRecentMatchResultsString(stats);
            List<String> firstLetterResults = List.of(lastResults.split(" "));
            XSSFRichTextString richText = new XSSFRichTextString("");

            Font font;
            for (String letter : firstLetterResults) {
                font = switch (letter) {
                    case "W" -> greenFont;
                    case "L" -> redFont;
                    case "D" -> blueFont;
                    default -> throw new IllegalArgumentException("There are other letters in String than W,L,D");
                };
                richText.append(letter, (XSSFFont) font);
                richText.append(" ");
            }
            cell.setCellValue(richText);

            rowStart++;
            colStart = 0;
        }
        footballStatisticWriterService.writeStatistic(workbook, sheet,
                STATISTICS_ROW_START, STATISTICS_COLUMN_START, tournament);
        return ExcelUtil.createExcelFile(filePath, workbook);
    }

    private void createColumnHeaders(int rowStart, int colStart, CellStyle cellStyle) {
        Row row = sheet.createRow(rowStart);

        Cell cell = row.createCell(colStart);
        cell.setCellStyle(cellStyle);
        sheet.setColumnWidth(colStart, COLUMN_WITH_NUMBER_EXCEL_WIDTH * ExcelUtil.COLUMN_WIDTH_UNIT);

        colStart++;

        cell = row.createCell(colStart);
        cell.setCellValue("Team");
        cell.setCellStyle(cellStyle);
        sheet.setColumnWidth(colStart, TEAM_COLUMN_EXCEL_WIDTH * ExcelUtil.COLUMN_WIDTH_UNIT);

        colStart++;

        cell = row.createCell(colStart);
        cell.setCellValue("MP");
        cell.setCellStyle(cellStyle);
        sheet.setColumnWidth(colStart, COLUMN_WITH_NUMBER_EXCEL_WIDTH * ExcelUtil.COLUMN_WIDTH_UNIT);

        colStart++;

        cell = row.createCell(colStart);
        cell.setCellValue("W");
        cell.setCellStyle(cellStyle);
        sheet.setColumnWidth(colStart, COLUMN_WITH_NUMBER_EXCEL_WIDTH * ExcelUtil.COLUMN_WIDTH_UNIT);

        colStart++;

        cell = row.createCell(colStart);
        cell.setCellValue("D");
        cell.setCellStyle(cellStyle);
        sheet.setColumnWidth(colStart, COLUMN_WITH_NUMBER_EXCEL_WIDTH * ExcelUtil.COLUMN_WIDTH_UNIT);

        colStart++;

        cell = row.createCell(colStart);
        cell.setCellValue("L");
        cell.setCellStyle(cellStyle);
        sheet.setColumnWidth(colStart, COLUMN_WITH_NUMBER_EXCEL_WIDTH * ExcelUtil.COLUMN_WIDTH_UNIT);

        colStart++;

        cell = row.createCell(colStart);
        cell.setCellValue("P");
        cell.setCellStyle(cellStyle);
        sheet.setColumnWidth(colStart, COLUMN_WITH_NUMBER_EXCEL_WIDTH * ExcelUtil.COLUMN_WIDTH_UNIT);

        colStart++;

        cell = row.createCell(colStart);
        cell.setCellValue("Last 5");
        cell.setCellStyle(cellStyle);
        sheet.setColumnWidth(colStart, LAST_5_COLUMN_EXCEL_WIDTH * ExcelUtil.COLUMN_WIDTH_UNIT);
    }

    private CellStyle createCellStyle(Workbook workbook, IndexedColors color) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setColor(color.getIndex());
        style.setFont(font);
        return style;
    }
}
