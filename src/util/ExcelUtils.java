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
