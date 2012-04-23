package summary;

import com.google.common.collect.Maps;
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
  private Map<Subject.SampleSource, int[]> sourceMap = Maps.newHashMap();
  private static final int fourHomo  = 0;
  private static final int fourHeto  = 1;
  private static final int fourNon   = 2;
  private static final int fourTotal = 3;

  public GenotypeSummary() {
    int[] starFour = new int[]{0,0,0,0};
    sourceMap.put(Subject.SampleSource.TUMOR, starFour);
    starFour = new int[]{0,0,0,0};
    sourceMap.put(Subject.SampleSource.BLOOD, starFour);
    starFour = new int[]{0,0,0,0};
    sourceMap.put(Subject.SampleSource.UNKNOWN, starFour);
  }

  public String getSheetTitle() {
    return sf_sheetTitle;
  }

  public void addSubject(Subject subject) {
    if (subject != null) {

      String key = subject.getGenotypeFinal().getMetabolizerGroup()
          + "|" + subject.getWeak()
          + "|" + subject.getPotent();

      StarFourStatus status;
      if (subject.getGenotypeFinal().is("*4","*4")) {
        status = StarFourStatus.Homozygous;
      }
      else if (subject.getGenotypeFinal().contains("*4")) {
        status = StarFourStatus.Heterozygous;
      }
      else {
        status = StarFourStatus.NonFour;
      }

      if (!countMap.containsKey(key)) {
        countMap.put(key, 1);
      }
      else {
        countMap.put(key, countMap.get(key)+1);
      }

      int[] totals = sourceMap.get(subject.getSampleSource());
      totals[fourTotal]++;
      switch (status) {
        case Homozygous:
          totals[fourHomo]++;
          break;
        case Heterozygous:
          totals[fourHeto]++;
          break;
        default:
          totals[fourNon]++;
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

    // Tumor source table
    Row row = sheet.createRow(++rowNum);
    row.createCell(0).setCellValue("Tumor Source");
    row.createCell(1).setCellValue("Count");
    row.createCell(2).setCellValue("*4 Homozygous");
    row.createCell(3).setCellValue("*4 Heterozygous");
    row.createCell(4).setCellValue("Non-*4");

    row = sheet.createRow(++rowNum);
    row.createCell(0).setCellValue(Subject.SampleSource.TUMOR.toString());
    row.createCell(1).setCellValue(sourceMap.get(Subject.SampleSource.TUMOR)[fourTotal]);
    row.createCell(2).setCellValue(sourceMap.get(Subject.SampleSource.TUMOR)[fourHomo]);
    row.createCell(3).setCellValue(sourceMap.get(Subject.SampleSource.TUMOR)[fourHeto]);
    row.createCell(4).setCellValue(sourceMap.get(Subject.SampleSource.TUMOR)[fourNon]);

    row = sheet.createRow(++rowNum);
    row.createCell(0).setCellValue(Subject.SampleSource.BLOOD.toString());
    row.createCell(1).setCellValue(sourceMap.get(Subject.SampleSource.BLOOD)[fourTotal]);
    row.createCell(2).setCellValue(sourceMap.get(Subject.SampleSource.BLOOD)[fourHomo]);
    row.createCell(3).setCellValue(sourceMap.get(Subject.SampleSource.BLOOD)[fourHeto]);
    row.createCell(4).setCellValue(sourceMap.get(Subject.SampleSource.BLOOD)[fourNon]);

    row = sheet.createRow(++rowNum);
    row.createCell(0).setCellValue(Subject.SampleSource.UNKNOWN.toString());
    row.createCell(1).setCellValue(sourceMap.get(Subject.SampleSource.UNKNOWN)[fourTotal]);
    row.createCell(2).setCellValue(sourceMap.get(Subject.SampleSource.UNKNOWN)[fourHomo]);
    row.createCell(3).setCellValue(sourceMap.get(Subject.SampleSource.UNKNOWN)[fourHeto]);
    row.createCell(4).setCellValue(sourceMap.get(Subject.SampleSource.UNKNOWN)[fourNon]);
  }

  enum StarFourStatus {Homozygous, Heterozygous, NonFour}
}
