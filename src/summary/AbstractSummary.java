package summary;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.pharmgkb.Subject;

/**
 * Created by IntelliJ IDEA.
 * User: whaleyr
 * Date: Aug 25, 2010
 */
public abstract class AbstractSummary {

  public abstract String getSheetTitle();

  public abstract void addSubject(Subject subject);

  public abstract void writeToWorkbook(Workbook wb);

  /**
   * If the Sheet doesn't already exist in the Workbook, create it.  Otherwise this method
   * will delete the existing one and create a new one
   * @param wb an Excel workbook
   * @return a new, blank Sheet in the given Workbook
   */
  public Sheet getSheet(Workbook wb) {
    Sheet sheet;

    int sheetIdx = wb.getSheetIndex(getSheetTitle());
    if (sheetIdx >= 0) {
      wb.removeSheetAt(sheetIdx);
    }
    sheet = wb.createSheet(getSheetTitle());

    return sheet;
  }
}
