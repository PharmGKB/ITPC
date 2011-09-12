package summary;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.pharmgkb.Subject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: whaleyr
 * Date: Aug 3, 2010
 */
public class GenotypeSummary extends AbstractSummary {
  private static final String sf_sheetTitle = "Genotype Summary";
  private Map<String,Integer> countMap = new HashMap<String,Integer>();

  public String getSheetTitle() {
    return sf_sheetTitle;
  }

  public void addSubject(Subject subject) {
    if (subject != null) {

      String key = subject.getGenoMetabolizerGroup()
          + "|" + subject.getWeak()
          + "|" + subject.getPotent();

      if (!countMap.containsKey(key)) {
        countMap.put(key, 1);
      }
      else {
        countMap.put(key, countMap.get(key)+1);
      }
    }
  }

  public void writeToWorkbook(Workbook wb) {
    Sheet sheet = getSheet(wb);

    Row header = sheet.createRow(0);
    header.createCell(0).setCellValue("Metabolizer Group based on Genotype Only");
    header.createCell(1).setCellValue("Weak");
    header.createCell(2).setCellValue("Potent");
    header.createCell(3).setCellValue("Count");

    int rowNum = 1;
    for (String key : countMap.keySet()) {
      String[] fields = key.split("\\|");
      Row data = sheet.createRow(rowNum);
      data.createCell(0).setCellValue(fields[0]);
      data.createCell(1).setCellValue(fields[1]);
      data.createCell(2).setCellValue(fields[2]);
      data.createCell(3).setCellValue(countMap.get(key));

      rowNum++;
    }
  }
}
