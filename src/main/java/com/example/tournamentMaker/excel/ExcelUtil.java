package com.example.tournamentmaker.excel;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExcelUtil {
    public static final int COLUMN_WIDTH_UNIT = 256;
    public static void setThinBorder(CellStyle cellStyle) {
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
    }
    public static void createRows(Sheet sheet, int howMany) {
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
        ExcelUtil.setThinBorder(cellStyle);
        return cellStyle;
    }
    public static boolean createExcelFile(String filePath, Workbook workbook) {
        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(filePath))) {
            workbook.write(dataOutputStream);
            return true;
        } catch (IOException e) {
            log.error("An error occurred while writing workbook to file");
            return false;
        }
    }
}
