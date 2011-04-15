package summary;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.pharmgkb.Subject;
import util.ItpcUtils;
import util.Value;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: whaleyr
 * Date: 4/15/11
 */
public class InclusionSummary extends AbstractSummary {
  private static final String sf_sheetTitle = "Inclusion Summary";
  private static final int inc1 = 0;
  private static final int inc2a = 1;
  private static final int inc2b = 2;
  private static final int inc3 = 3;
  private static final int inc4 = 4;
  private static final int inc4a = 5;
  private static final int inc4b = 6;
  private static final int inc4c = 7;
  private static final int inc5 = 8;
  private static final int inc6 = 9;
  private static final int inc7 = 10;
  private static final int inc8 = 11;
  private static final int inc9 = 12;
  private static final int incFinal = 13;

  private static final Map<Integer,String> inclusions = Maps.newHashMap();
  static {
    inclusions.put(0, "Inc. 1");
    inclusions.put(1, "Inc. 2a");
    inclusions.put(2, "Inc. 2b");
    inclusions.put(3, "Inc. 3");
    inclusions.put(4, "Inc. 4");
    inclusions.put(5, "Inc. 4a");
    inclusions.put(6, "Inc. 4b");
    inclusions.put(7, "Inc. 4c");
    inclusions.put(8, "Inc. 5");
    inclusions.put(9, "Inc. 6");
    inclusions.put(10, "Inc. 7");
    inclusions.put(11, "Inc. 8");
    inclusions.put(12, "Inc. 9");
    inclusions.put(13, "Final Inclusion");
  }

  private List<Map<Integer,Map<Value,Integer>>> projectMap = null;

  public InclusionSummary() {
    projectMap = Lists.newArrayList();

    for (int i=0; i< ItpcUtils.SITE_COUNT; i++) {
      Map<Integer,Map<Value,Integer>> inclusionMap = Maps.newHashMap();
      projectMap.add(inclusionMap);

      for (int j=0; j< inclusions.size(); j++) {
        Map<Value, Integer> valueMap = Maps.newHashMap();
        inclusionMap.put(j, valueMap);

        valueMap.put(Value.Yes, 0);
        valueMap.put(Value.No, 0);
        valueMap.put(Value.Unknown, 0);
      }
    }
  }

  @Override
  public String getSheetTitle() {
    return sf_sheetTitle;
  }

  @Override
  public void addSubject(Subject subject) {
    int site = Integer.valueOf(subject.getProjectSite());

    addSubjectInclusion(site-1, inc1, subject.passInclusion1());
    addSubjectInclusion(site-1, inc2a, subject.passInclusion2a());
    addSubjectInclusion(site-1, inc2b, subject.passInclusion2b());
    addSubjectInclusion(site-1, inc3, subject.passInclusion3());
    addSubjectInclusion(site-1, inc4, subject.passInclusion4());
    addSubjectInclusion(site-1, inc4a, subject.passInclusion4a());
    addSubjectInclusion(site-1, inc4b, subject.passInclusion4b());
    addSubjectInclusion(site-1, inc4c, subject.passInclusion4c());
    addSubjectInclusion(site-1, inc5, subject.passInclusion5());
    addSubjectInclusion(site-1, inc6, subject.passInclusion6());
    addSubjectInclusion(site-1, inc7, subject.passInclusion7());
    addSubjectInclusion(site-1, inc8, subject.passInclusion8());
    addSubjectInclusion(site-1, inc9, subject.passInclusion9());
    addSubjectInclusion(site-1, incFinal, subject.include());
  }

  private void addSubjectInclusion(int site, int criteria, Value value) {
    // let's just count Unknowns as no for this summary
    Value useValue = value;
    if (value == Value.Unknown) {
      useValue = Value.No;
    }

    Map<Integer, Map<Value, Integer>> inclusionMap = projectMap.get(site);
    Map<Value, Integer> valueMap = inclusionMap.get(criteria);

    valueMap.put(useValue, valueMap.get(useValue)+1);
  }

  @Override
  public void writeToWorkbook(Workbook wb) {
    int currentRow = 0;
    Sheet sheet = getSheet(wb);

    currentRow = writeTable(sheet, currentRow, Value.Yes);
    currentRow += 3;
    writeTable(sheet, currentRow, Value.No);
  }

  private int writeTable(Sheet sheet, int currentRow, Value value) {
    // initialize the inclusion totals to 0
    int totalSubjects = 0;
    Map<Integer, Integer> inclusionTotals = Maps.newHashMap();
    for (int i=0; i<inclusions.size(); i++) {
      inclusionTotals.put(i,0);
    }

    Row title = sheet.createRow(currentRow++);
    title.createCell(0).setCellValue("Inclusion Criteria Summary (criteria passed: "+value+")");

    Row headerRow = sheet.createRow(currentRow++);
    headerRow.createCell(0).setCellValue("Site ID");
    for (int i=0; i<inclusions.size(); i++) {
      headerRow.createCell(i+1).setCellValue(inclusions.get(i));
    }
    headerRow.createCell(inclusions.size()+1).setCellValue("Total Subjects");

    for (int i=0; i<ItpcUtils.SITE_COUNT; i++) {
      Row siteRow = sheet.createRow(currentRow++);
      String siteName = (new Integer(i+1)).toString();
      siteRow.createCell(0).setCellValue(siteName);

      for (int j=0; j<inclusions.size(); j++) {
        siteRow.createCell(j+1).setCellValue(projectMap.get(i).get(j).get(value));
        inclusionTotals.put(j, inclusionTotals.get(j) + projectMap.get(i).get(j).get(value));
      }

      Integer siteSubjectTotal = projectMap.get(i).get(0).get(Value.Yes) + projectMap.get(i).get(0).get(Value.No) + projectMap.get(i).get(0).get(Value.Unknown);
      siteRow.createCell(inclusions.size()+1).setCellValue(siteSubjectTotal);
      totalSubjects += siteSubjectTotal;
    }

    Row totalsRow = sheet.createRow(currentRow++);
    totalsRow.createCell(0).setCellValue("Total");
    for (int i=0; i<inclusions.size(); i++) {
      totalsRow.createCell(i+1).setCellValue(inclusionTotals.get(i));
    }
    totalsRow.createCell(inclusions.size()+1).setCellValue(totalSubjects);

    return currentRow;
  }
}
