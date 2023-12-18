package com.example.tournamentmaker.excel.football;

import com.example.tournamentmaker.statistics.Statistics;
import com.example.tournamentmaker.team.Team;
import com.example.tournamentmaker.tournament.Tournament;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.tournamentmaker.excel.ExcelUtil.COLUMN_WIDTH_UNIT;
import static com.example.tournamentmaker.excel.ExcelUtil.createCellStyle;
import static com.example.tournamentmaker.excel.football.FootballExcelUtil.createCell;
import static com.example.tournamentmaker.statistics.StatisticUtil.getGamesCount;
import static com.example.tournamentmaker.statistics.StatisticUtil.getRecentMatchResultsString;

public class FootballResultsLeagueSheetStrategy implements FootballResultsStrategy {
    private static final int TEAM_COLUMN_EXCEL_WIDTH = 26;
    private static final int COLUMN_WITH_NUMBER_EXCEL_WIDTH = 5;
    private static final int LAST_5_COLUMN_EXCEL_WIDTH = 12;


    @Override
    public Sheet fillSheet(Workbook workbook, Sheet sheet, Tournament tournament) {
        final CellStyle borderCellStyle = createCellStyle(workbook, IndexedColors.GOLD.getIndex());
        final CellStyle fillingTableCellStyle = createCellStyle(workbook, IndexedColors.LEMON_CHIFFON.getIndex());
        setColumnsWidth(sheet);

        List<Statistics> statistics = tournament.getTeamList().stream()
                .map(Team::getStatistics)
                .sorted(Comparator.comparingInt(Statistics::getPoints).reversed())
                .toList();

        int rowStart = 0;
        int colStart = 0;

        createColumnHeaders(sheet, rowStart++, colStart, borderCellStyle);

        for (int i = 0; i < statistics.size(); i++) {
            int place = i + 1;
            fillRow(workbook, sheet, rowStart++, statistics.get(i), fillingTableCellStyle, place);
        }
        return sheet;
    }

    private void createColumnHeaders(Sheet sheet, int rowStart, int colStart, CellStyle cellStyle) {
        Row row = sheet.createRow(rowStart);
        AtomicInteger col = new AtomicInteger(colStart);
        List<String> headers = List.of("", "Team", "MP", "W", "D", "L", "P", "Last 5");
        headers.forEach(s -> createCell(row, col.getAndIncrement(), cellStyle, s));
    }

    private void fillRow(Workbook workbook, Sheet sheet, int rowNumber, Statistics stats, CellStyle cellStyle, int place) {
        Row row = sheet.createRow(rowNumber);
        AtomicInteger col = new AtomicInteger(0);

        List<Object> values = List.of(place, stats.getTeam().getName(), getGamesCount(stats),
                stats.getCountWins(), stats.getCountDraws(), stats.getCountLoses(), stats.getPoints(),
                getRichTextStringInColors(workbook, stats));
        values.forEach(value -> createCell(row, col.getAndIncrement(), cellStyle, value));
    }

    private XSSFRichTextString getRichTextStringInColors(Workbook workbook, Statistics stats) {
        String lastResults = getRecentMatchResultsString(stats);
        List<String> firstLetterResults = List.of(lastResults.split(" "));
        XSSFRichTextString richText = new XSSFRichTextString("");

        Font font;
        for (String letter : firstLetterResults) {
            font = switch (letter) {
                case "W" -> getFontColor(workbook, "GREEN");
                case "L" -> getFontColor(workbook, "RED");
                case "D" -> getFontColor(workbook, "BLUE");
                default -> throw new IllegalArgumentException("There are other letters in String than W,L,D");
            };
            richText.append(letter, (XSSFFont) font);
            richText.append(" ");
        }
        return richText;
    }

    private Font getFontColor(Workbook workbook, String colorName) {
        Font font = workbook.createFont();
        font.setColor(IndexedColors.valueOf(colorName).getIndex());
        return font;
    }

    @Override
    public void setColumnsWidth(Sheet sheet) {
        final int columnCount = 8;
        for (int i = 0; i < columnCount; i++) {
            int currentWidth = COLUMN_WITH_NUMBER_EXCEL_WIDTH;
            if (i == 1) {
                currentWidth = TEAM_COLUMN_EXCEL_WIDTH;
            } else if (i == 7) {
                currentWidth = LAST_5_COLUMN_EXCEL_WIDTH;
            }
            sheet.setColumnWidth(i, currentWidth * COLUMN_WIDTH_UNIT);
        }
    }
}
