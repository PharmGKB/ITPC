/*
 * ----- BEGIN LICENSE BLOCK -----
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is PharmGen.
 *
 * The Initial Developer of the Original Code is PharmGKB (The Pharmacogenetics
 * and Pharmacogenetics Knowledge Base, supported by NIH U01GM61374). Portions
 * created by the Initial Developer are Copyright (C) 2013 the Initial Developer.
 * All Rights Reserved.
 *
 * Contributor(s):
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or the
 * GNU Lesser General Public License Version 2.1 or later (the "LGPL"), in
 * which case the provisions of the GPL or the LGPL are applicable instead of
 * those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ----- END LICENSE BLOCK -----
 */

package util;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;


/**
 * This class provides convenience methods for dealing with POI objects.
 *
 * @author Ryan Whaley
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

}
