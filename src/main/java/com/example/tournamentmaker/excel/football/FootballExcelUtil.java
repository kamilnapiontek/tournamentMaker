package com.example.tournamentmaker.excel.football;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FootballExcelUtil {
    public static void createCell(Row row, int col, CellStyle cellStyle, Object value) {
        Cell cell = row.createCell(col);
        cell.setCellStyle(cellStyle);

        if (value instanceof Integer integer) cell.setCellValue(integer);
        if (value instanceof XSSFRichTextString richTextString) cell.setCellValue(richTextString);
        if (value instanceof String string) {
            if (stringContainsOnlyNumbers(string)) {
                cell.setCellValue(Integer.parseInt(string));
            } else {
                cell.setCellValue(string);
            }
        }
    }

    public static boolean stringContainsOnlyNumbers(String string) {
        return string.matches("\\d+");
    }
}
