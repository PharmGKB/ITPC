/*
 ----- BEGIN LICENSE BLOCK -----
 Version: MPL 1.1/GPL 2.0/LGPL 2.1

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with the
 License. You may obtain a copy of the License at http://www.mozilla.org/MPL/

 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 the specific language governing rights and limitations under the License.

 The Original Code is PharmGen.

 The Initial Developer of the Original Code is
 PharmGKB (The Pharmacogenetics and Pharmacogenetics Knowledge Base,
 supported by NIH U01GM61374).
 Portions created by the Initial Developer are Copyright (C) 2009
 the Initial Developer. All Rights Reserved.

 Contributor(s):

 Alternatively, the contents of this file may be used under the terms of
 either the GNU General Public License Version 2 or later (the "GPL"), or the
 GNU Lesser General Public License Version 2.1 or later (the "LGPL"), in
 which case the provisions of the GPL or the LGPL are applicable instead of
 those above. If you wish to allow use of your version of this file only
 under the terms of either the GPL or the LGPL, and not to allow others to
 use your version of this file under the terms of the MPL, indicate your
 decision by deleting the provisions above and replace them with the notice
 and other provisions required by the GPL or the LGPL. If you do not delete
 the provisions above, a recipient may use your version of this file under
 the terms of any one of the MPL, the GPL or the LGPL.

 ----- END LICENSE BLOCK -----
 */
package util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;


/**
 * This class provides convenience methods for dealing with POI objects.
 *
 * @author Winston Gor
 */
public class POIUtils {

  private POIUtils() {
  }


  /**
   * Converts numbers that include exponents into a regular number.
   *
   * @param number original number
   * @return reformatted number
   **/
  private static String formatNumber(double number) {

    String numString = Double.toString(number);
    int idx = numString.indexOf((int)'E');
    if (idx == -1) {
      // lop off trailing .0
      if (numString.endsWith(".0")) {
        numString = numString.substring(0, numString.length() - 2);
      }
      return numString;
    }

    int exponent = Integer.parseInt(numString.substring(idx + 1));
    int precision = idx - 1;
    if (exponent > 0 && exponent == precision) {
      precision++;
    }
    BigDecimal bd = new BigDecimal(number, new MathContext(precision));
    return bd.toPlainString();
  }

  /**
   * Gets the string value of a cell.
   *
   * @param cell the cell to get the string value of
   * @return the string value of the specified cell
   */
  private static String getStringValue(Cell cell) {

    if (cell != null) {
      switch (cell.getCellType()) {
        case Cell.CELL_TYPE_NUMERIC:
          cell.setCellType(Cell.CELL_TYPE_NUMERIC);
          return formatNumber(cell.getNumericCellValue());
        case Cell.CELL_TYPE_BOOLEAN:
          return Boolean.toString(cell.getBooleanCellValue());
        default:
          return StringUtils.stripToNull(cell.getRichStringCellValue().getString());
      }
    }
    return null;
  }


  /**
   * Returns value at specified row and cell numbers.  Indexes are 0-based.
   *
   * @param sheet Excel spreadsheet
   * @param rowNum row number
   * @param cellNum cell number
   * @return value of cell
   **/
  public static String getStringCellValue(Sheet sheet, int rowNum, int cellNum) {

    Row row = sheet.getRow(rowNum);
    if (row != null) {
      return getStringValue(row.getCell(cellNum));
    }
    return null;
  }


  /**
   * Returns column values in specified row.  Indexes are 0-based.
   *
   * @param sheet Excel spreadsheet
   * @param rowNum row number
   * @return list of cell values in row
   **/
  public static List<String> getStringCellValues(Sheet sheet, int rowNum) {

    Row row = sheet.getRow(rowNum);
    if (row != null) {
      return getStringCellValues(sheet, rowNum, 0, (int)row.getLastCellNum()-1);
    }
    return new ArrayList<String>();
  }


  /**
   * Returns values within specified column range in specified row.
   *
   * @param sheet Excel spreadsheet
   * @param rowNum row number
   * @param columnStart index of beginning of column range
   * @param columnStop index of end of column range
   * @return list of cell values in row
   **/
  public static List<String> getStringCellValues(Sheet sheet, int rowNum, int columnStart,
      int columnStop) {

    List<String> values = new ArrayList<String>();
    Row row = sheet.getRow(rowNum);
    if (row != null) {
      int stop = columnStop + 1;
      for (int i = columnStart; i < stop; i++) {
        values.add(getStringValue(row.getCell(i)));
      }
    }
    return values;
  }


 /**
  * Returns cells within specified column range in specified row.
  *
  * @param sheet Excel spreadsheet
  * @param rowNum row number
  * @param column column number
  * @return list of cell values in row
  **/
  public static Cell getCell(Sheet sheet, int rowNum, int column) {

    Cell cell = null;
    Row row = sheet.getRow(rowNum);
    if (row != null) {
      cell = row.getCell(column);
    }
    return cell;
  }


  public static CellStyle getTitleStyle(Workbook wb) {
    CellStyle style = wb.createCellStyle();

    style.setBorderBottom(CellStyle.BORDER_THIN);
    style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
    style.setBorderLeft(CellStyle.BORDER_THIN);
    style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
    style.setBorderTop(CellStyle.BORDER_THIN);
    style.setTopBorderColor(IndexedColors.BLACK.getIndex());
    style.setBorderRight(CellStyle.BORDER_THIN);
    style.setRightBorderColor(IndexedColors.BLACK.getIndex());

    style.setAlignment(CellStyle.ALIGN_CENTER);
    style.setWrapText(true);

    return style;
  }

  public static void styleTitleCells(Row headerRow) {
    CellStyle style = POIUtils.getTitleStyle(headerRow.getSheet().getWorkbook());

    Iterator<Cell> headerCells = headerRow.cellIterator();

    while (headerCells.hasNext()) {
      Cell headerCell=headerCells.next();
      headerCell.setCellStyle(style);
    }
  }

  public static CellStyle getScoreStyle(Workbook wb) {
    CellStyle scoreStyle = wb.createCellStyle();
    DataFormat scoreFormat = wb.createDataFormat();
    scoreStyle.setDataFormat(scoreFormat.getFormat("0.0"));
    return scoreStyle;
  }

  /**
   * Copies the given row to the given sheet, useful for copying rows between two differnt sheets.
   * This will preserve values, formulas, and formatting
   * @param row a Row to copy
   * @param sheet a Sheet to copy to
   * @return the new Row in <code>sheet</code>
   */
  public static Row copyRowTo(Row row, Sheet sheet) {
    return copyRowTo(row, sheet, row.getRowNum());
  }

  /**
   * Copies the given row to the given sheet, useful for copying rows between two differnt sheets.
   * This will preserve values, formulas, and formatting.  You can specify a new row number if you
   * want the row to appear in a different place in the new sheet.
   * @param row a Row to copy
   * @param sheet a Sheet to copy to
   * @param rowNum the desired row number in <code>sheet</code>
   * @return the new Row in <code>sheet</code>
   */
  public static Row copyRowTo(Row row, Sheet sheet, int rowNum) {
    Row newRow = sheet.createRow(rowNum);

    for (Cell cell : row) {
      Cell newCell = newRow.createCell(cell.getColumnIndex());
      newCell.setCellStyle(cell.getCellStyle());
      switch (cell.getCellType()) {
        case Cell.CELL_TYPE_NUMERIC:
          newCell.setCellValue(cell.getNumericCellValue());
          break;
        case Cell.CELL_TYPE_STRING:
          newCell.setCellValue(cell.getStringCellValue());
          break;
        case Cell.CELL_TYPE_BOOLEAN:
          newCell.setCellValue(cell.getBooleanCellValue());
          break;
        case Cell.CELL_TYPE_FORMULA:
          newCell.setCellFormula(cell.getCellFormula());
        default:
          break;
      }
    }
    return newRow;
  }
}
