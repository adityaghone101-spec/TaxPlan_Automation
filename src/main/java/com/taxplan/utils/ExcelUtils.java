package com.taxplan.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtils {

    private static final Logger logger = LogManager.getLogger(ExcelUtils.class);

    public static Object[][] getTestData(String filePath, String sheetName) {
        List<Map<String, String>> dataList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("Sheet not found: " + sheetName);
            }

            Row headerRow = sheet.getRow(0);
            int colCount = headerRow.getLastCellNum();
            List<String> headers = new ArrayList<>();
            for (int i = 0; i < colCount; i++) {
                headers.add(getCellValueAsString(headerRow.getCell(i)));
            }

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                Map<String, String> rowData = new HashMap<>();
                for (int colIndex = 0; colIndex < colCount; colIndex++) {
                    rowData.put(headers.get(colIndex), getCellValueAsString(row.getCell(colIndex)));
                }
                dataList.add(rowData);
            }

            logger.info("Loaded " + dataList.size() + " test data rows from sheet: " + sheetName);

        } catch (IOException e) {
            logger.error("Failed to read Excel file: " + filePath, e);
            throw new RuntimeException("Failed to read Excel file: " + filePath, e);
        }

        Object[][] data = new Object[dataList.size()][1];
        for (int i = 0; i < dataList.size(); i++) {
            data[i][0] = dataList.get(i);
        }
        return data;
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double numVal = cell.getNumericCellValue();
                    if (numVal == Math.floor(numVal) && !Double.isInfinite(numVal)) {
                        return String.valueOf((long) numVal);
                    }
                    return String.valueOf(numVal);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}
