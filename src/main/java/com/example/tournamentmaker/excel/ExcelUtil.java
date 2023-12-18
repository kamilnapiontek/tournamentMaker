package com.example.tournamentmaker.excel;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExcelUtil {
    public static final int COLUMN_WIDTH_UNIT = 256;

    public static void setBorder(CellStyle cellStyle, BorderStyle borderStyle) {
        cellStyle.setBorderTop(borderStyle);
        cellStyle.setBorderBottom(borderStyle);
        cellStyle.setBorderLeft(borderStyle);
        cellStyle.setBorderRight(borderStyle);
    }

    public static void createNeededRows(Sheet sheet, int howMany) {
        for (int i = 0; i < howMany; i++) {
            if (sheet.getRow(i) == null) {
                sheet.createRow(i);
            }
        }
    }

    public static CellStyle createCellStyle(Workbook workbook, short index) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(index);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        setBorder(cellStyle, BorderStyle.THIN);
        return cellStyle;
    }
}
