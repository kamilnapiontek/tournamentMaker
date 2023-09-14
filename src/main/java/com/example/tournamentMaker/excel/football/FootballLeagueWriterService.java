package com.example.tournamentmaker.excel.football;

import com.example.tournamentmaker.excel.ExcelStrategy;
import com.example.tournamentmaker.excel.ExcelUtil;
import com.example.tournamentmaker.statistics.StatisticService;
import com.example.tournamentmaker.statistics.Statistics;
import com.example.tournamentmaker.team.Team;
import com.example.tournamentmaker.tournament.Tournament;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
        final CellStyle borderCellStyle = ExcelUtil.createCellStyle(workbook, IndexedColors.GOLD.getIndex());
        final CellStyle fillingTableCellStyle = ExcelUtil.createCellStyle(workbook, IndexedColors.LEMON_CHIFFON.getIndex());

        List<Statistics> statistics = tournament.getTeamList().stream()
                .map(Team::getStatistics)
                .sorted(Comparator.comparingInt(Statistics::getPoints).reversed())
                .toList();

        int rowStart = 0;
        int colStart = 0;
        setColumnsWidth(sheet);
        createColumnHeaders(rowStart, colStart, borderCellStyle);
        rowStart++;

        for (int i = 0; i < statistics.size(); i++) {
            int place = i + 1;
            fillRow(rowStart, statistics.get(i), fillingTableCellStyle, place);
            rowStart++;
        }
        footballStatisticWriterService.writeStatistic(workbook, sheet, STATISTICS_ROW_START, STATISTICS_COLUMN_START,
                tournament);
        return ExcelUtil.createExcelFile(filePath, workbook);
    }

    private void fillRow(int rowNumber, Statistics stats, CellStyle cellStyle, int place) {
        Row row = sheet.createRow(rowNumber);
        AtomicInteger col = new AtomicInteger(0);

        List<Object> values = List.of(place, stats.getTeam().getName(), statisticService.getGamesCount(stats),
                stats.getCountWins(), stats.getCountDraws(), stats.getCountLoses(), stats.getPoints(),
                getRichTextStringInColors(stats));
        values.forEach(value -> ExcelUtil.createCell(row, col.getAndIncrement(), cellStyle, value));
    }

    private XSSFRichTextString getRichTextStringInColors(Statistics stats) {
        String lastResults = statisticService.getRecentMatchResultsString(stats);
        List<String> firstLetterResults = List.of(lastResults.split(" "));
        XSSFRichTextString richText = new XSSFRichTextString("");

        Font font;
        for (String letter : firstLetterResults) {
            font = switch (letter) {
                case "W" -> getFontColor("GREEN");
                case "L" -> getFontColor("RED");
                case "D" -> getFontColor("BLUE");
                default -> throw new IllegalArgumentException("There are other letters in String than W,L,D");
            };
            richText.append(letter, (XSSFFont) font);
            richText.append(" ");
        }
        return richText;
    }

    private Font getFontColor(String colorName) {
        Font font = workbook.createFont();
        font.setColor(IndexedColors.valueOf(colorName).getIndex());
        return font;
    }

    private void createColumnHeaders(int rowStart, int colStart, CellStyle cellStyle) {
        Row row = sheet.createRow(rowStart);
        AtomicInteger col = new AtomicInteger(colStart);
        List<String> headers = List.of("", "Team", "MP", "W", "D", "L", "P", "Last 5");
        headers.forEach(s -> ExcelUtil.createCell(row, col.getAndIncrement(), cellStyle, s));
    }

    private void setColumnsWidth(Sheet sheet) {
        final int columnCount = 8;
        for (int i = 0; i < columnCount; i++) {
            int currentWidth = COLUMN_WITH_NUMBER_EXCEL_WIDTH;
            if (i == 1) {
                currentWidth = TEAM_COLUMN_EXCEL_WIDTH;
            } else if (i == 7) {
                currentWidth = LAST_5_COLUMN_EXCEL_WIDTH;
            }
            sheet.setColumnWidth(i, currentWidth * ExcelUtil.COLUMN_WIDTH_UNIT);
        }
    }
}
