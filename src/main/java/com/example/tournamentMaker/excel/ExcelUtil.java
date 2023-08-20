package com.example.tournamentMaker.excel;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class ExcelUtil {
    static void setBorder(CellStyle cellStyle, BorderStyle borderStyle) {
        cellStyle.setBorderTop(borderStyle);
        cellStyle.setBorderBottom(borderStyle);
        cellStyle.setBorderLeft(borderStyle);
        cellStyle.setBorderRight(borderStyle);
    }
    static void createRows(XSSFSheet sheet, int howMany) {
        for (int i = 0; i < howMany; i++) {
            sheet.createRow(i);
        }
    }
}
