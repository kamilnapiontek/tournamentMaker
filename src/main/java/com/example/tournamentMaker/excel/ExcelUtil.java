package com.example.tournamentmaker.excel;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ExcelUtil {
    static void setThinBorder(CellStyle cellStyle) {
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
    }
    static void createRows(Sheet sheet, int howMany) {
        for (int i = 0; i < howMany; i++) {
            sheet.createRow(i);
        }
    }
    static CellStyle createCellStyle(Workbook workbook, short index) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(index);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        ExcelUtil.setThinBorder(cellStyle);
        return cellStyle;
    }
}
