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

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;

/**
 * Created by IntelliJ IDEA.
 * User: whaleyr
 * Date: Jul 28, 2010
 * Time: 8:35:06 AM
 */
public class ExcelUtils {
  private static Logger sf_logger = Logger.getLogger(ExcelUtils.class);

  public static String getAddress(Cell cell) {
    StringBuilder sb = new StringBuilder();
    sb.append(CellReference.convertNumToColString(cell.getColumnIndex()));
    sb.append(cell.getRowIndex()+1);
    return sb.toString();
  }

  public static void writeCell(Row row, int idx, String value) {
    writeCell(row, idx, value, null);
  }

  public static void writeCell(Row row, int idx, String value, CellStyle highlight) {
    // first validate all the important arguments
    if (value == null || row == null || idx<0) {
      // don't do anything if they're missing
      return;
    }

    Cell cell = row.getCell(idx);

    if (cell == null) {
      row.createCell(idx).setCellValue(value);
    }
    else {
      if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
        if (!value.equals(cell.getStringCellValue())) {
          StringBuilder sb = new StringBuilder();
          sb.append("Changed value: ")
              .append(CellReference.convertNumToColString(cell.getColumnIndex()))
              .append(cell.getRowIndex()+1)
              .append(" = ")
              .append(cell.getStringCellValue())
              .append(" -> ")
              .append(value);
          sf_logger.info(sb.toString());
          if (highlight != null) {
            cell.setCellStyle(highlight);
          }
        }
      }
      else {
        Double existingValue = cell.getNumericCellValue();

        StringBuilder sb = new StringBuilder();
        sb.append("Changed value: ")
            .append(CellReference.convertNumToColString(cell.getColumnIndex()))
            .append(cell.getRowIndex()+1)
            .append(" = ")
            .append(existingValue)
            .append(" -> ")
            .append(value);
        sf_logger.info(sb.toString());

        row.removeCell(cell);
        row.createCell(idx).setCellType(Cell.CELL_TYPE_STRING);
        if (highlight != null) {
          cell.setCellStyle(highlight);
        }
      }
      cell.setCellValue(value);
    }
  }

  public static void writeCell(Row row, int idx, float value, CellStyle highlight) {
    Cell cell = row.getCell(idx);
    if (cell == null) {
      row.createCell(idx).setCellValue(value);
    }
    else {
      if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
        Double existingValue = cell.getNumericCellValue();
        if (value != existingValue.floatValue()) {
          StringBuilder sb = new StringBuilder();
          sb.append("Changed value: ")
              .append(CellReference.convertNumToColString(cell.getColumnIndex()))
              .append(cell.getRowIndex()+1)
              .append(" = ")
              .append(cell.getNumericCellValue())
              .append(" -> ")
              .append(value);
          sf_logger.info(sb.toString());
          if (highlight != null) {
            cell.setCellStyle(highlight);
          }
        }
      }
      else {
        String existingValue = cell.getStringCellValue();

        StringBuilder sb = new StringBuilder();
        sb.append("Changed value: ")
            .append(CellReference.convertNumToColString(cell.getColumnIndex()))
            .append(cell.getRowIndex()+1)
            .append(" = ")
            .append(existingValue)
            .append(" -> ")
            .append(value);
        sf_logger.info(sb.toString());

        row.removeCell(cell);
        row.createCell(idx).setCellType(Cell.CELL_TYPE_NUMERIC);
        if (highlight != null) {
          cell.setCellStyle(highlight);
        }
      }

      cell.setCellValue(value);
    }
  }

}
