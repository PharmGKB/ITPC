package summary;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.pharmgkb.Subject;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: whaleyr
 * Date: Aug 23, 2010
 */
public class NonFourSummary extends AbstractSummary {
  private static final String sf_sheetTitle = "Non-4 Alleles";
  private Map<Serializable,Integer> projectNonFourAlleles;
  private Map<Serializable,Integer> projectAllAlleles;

  public NonFourSummary() {
    projectNonFourAlleles = new TreeMap<Serializable,Integer>();
    projectAllAlleles = new TreeMap<Serializable,Integer>();
  }

  public String getSheetTitle() {
    return sf_sheetTitle;
  }

  public void addSubject(Subject subject) {
    if (!subject.getGenotypeFinal().contains("*4")) {
      iterate(projectNonFourAlleles, subject.getProjectSite());
    }
    iterate(projectAllAlleles, subject.getProjectSite());

  }

  private void iterate(Map<Serializable,Integer> counterMap, Serializable key) {
    if (counterMap.containsKey(key)) {
      counterMap.put(key, counterMap.get(key)+1);
    }
    else {
      counterMap.put(key, 1);
    }
  }

  public void writeToWorkbook(Workbook wb) {
    Sheet sheet = getSheet(wb);

    Row header = sheet.createRow(0);
    header.createCell(0).setCellValue("Non-*4 data available by Site");

    header = sheet.createRow(1);
    header.createCell(0).setCellValue("Project Site");
    header.createCell(1).setCellValue("Non-*4 Samples");
    header.createCell(2).setCellValue("Total Samples");

    int rowNum = 2;
    for (Serializable site : projectNonFourAlleles.keySet()) {
      Row data = sheet.createRow(rowNum);
      data.createCell(0).setCellValue((String)site);
      data.createCell(1).setCellValue(projectNonFourAlleles.get(site));
      data.createCell(1).setCellValue(projectAllAlleles.get(site));
      rowNum++;
    }
  }
}
