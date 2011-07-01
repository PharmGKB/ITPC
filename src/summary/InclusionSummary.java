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

  private static final int exc1 = 0;
  private static final int exc2 = 1;
  private static final int exc3 = 2;

  private static final int crit1 = 0;
  private static final int crit2 = 1;
  private static final int crit3 = 2;

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
  }

  private static final Map<Integer,String> exclusions = Maps.newHashMap();
  static {
    exclusions.put(0, "Excl. 1");
    exclusions.put(1, "Excl. 2");
    exclusions.put(2, "Excl. 3");
  }

  private static final Map<Integer,String> criteriaLabels = Maps.newHashMap();
  static {
    criteriaLabels.put(0, "Criteria 1");
    criteriaLabels.put(1, "Criteria 2");
    criteriaLabels.put(2, "Criteria 3");
  }

  private List<Map<Integer,Map<Value,Integer>>> projectMap = null;
  private List<Map<Integer,Map<Value,Integer>>> projectExcludeMap = null;
  private List<Map<Integer,Map<Value,Integer>>> projectCritMap = null;
  private Map<Integer, Integer> projectSubjectCount = null;

  public InclusionSummary() {
    projectMap = Lists.newArrayList();
    projectExcludeMap = Lists.newArrayList();
    projectCritMap = Lists.newArrayList();
    projectSubjectCount = Maps.newHashMap();

    for (int i=0; i< ItpcUtils.SITE_COUNT; i++) {
      projectSubjectCount.put(i, 0);
      Map<Integer,Map<Value,Integer>> inclusionMap = Maps.newHashMap();
      projectMap.add(inclusionMap);

      for (int j=0; j< inclusions.size(); j++) {
        Map<Value, Integer> valueMap = Maps.newHashMap();
        inclusionMap.put(j, valueMap);

        valueMap.put(Value.Yes, 0);
        valueMap.put(Value.No, 0);
        valueMap.put(Value.Unknown, 0);
      }

      Map<Integer,Map<Value,Integer>> exclusionMap = Maps.newHashMap();
      projectExcludeMap.add(exclusionMap);

      for (int j=0; j<exclusions.size(); j++) {
        Map<Value,Integer> valueMap = Maps.newHashMap();
        exclusionMap.put(j, valueMap);

        valueMap.put(Value.Yes,0);
        valueMap.put(Value.No,0);
      }

      Map<Integer,Map<Value,Integer>> criteriaMap = Maps.newHashMap();
      projectCritMap.add(criteriaMap);

      for (int j=0; j<criteriaLabels.size(); j++) {
        Map<Value,Integer> valueMap = Maps.newHashMap();
        criteriaMap.put(j, valueMap);

        valueMap.put(Value.Yes,0);
        valueMap.put(Value.No,0);
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
    projectSubjectCount.put(site-1, projectSubjectCount.get(site-1)+1);

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

    addSubjectExclusion(site - 1, exc1, subject.exclude1());
    addSubjectExclusion(site - 1, exc2, subject.exclude2());
    addSubjectExclusion(site - 1, exc3, subject.exclude3());

    addSubjectCriterium(site - 1, crit1, subject.includeCrit1());
    addSubjectCriterium(site - 1, crit2, subject.includeCrit2());
    addSubjectCriterium(site - 1, crit3, subject.includeCrit3());
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

  private void addSubjectExclusion(int site, int criteria, Value value) {
    Map<Integer, Map<Value,Integer>> exclusionMap = projectExcludeMap.get(site);
    Map<Value, Integer> valueMap = exclusionMap.get(criteria);
    valueMap.put(value, valueMap.get(value)+1);
  }

  private void addSubjectCriterium(int site, int criteria, Value value) {
    Map<Integer, Map<Value,Integer>> criteriumMap = projectCritMap.get(site);
    Map<Value, Integer> valueMap = criteriumMap.get(criteria);
    valueMap.put(value, valueMap.get(value)+1);
  }

  @Override
  public void writeToWorkbook(Workbook wb) {
    int currentRow = 0;
    Sheet sheet = getSheet(wb);

    currentRow = writeInclusionTable(sheet, currentRow, Value.Yes);
    currentRow += 3;
    currentRow = writeInclusionTable(sheet, currentRow, Value.No);
    currentRow += 3;
    currentRow = writeExclusionTable(sheet, currentRow, Value.Yes);
    currentRow += 3;
    currentRow = writeExclusionTable(sheet, currentRow, Value.No);
    currentRow += 3;
    currentRow = writeCriteriaTable(sheet, currentRow, Value.Yes);
    currentRow += 3;
    writeCriteriaTable(sheet, currentRow, Value.No);
  }

  private int writeInclusionTable(Sheet sheet, int currentRow, Value value) {
    // initialize the inclusion totals to 0
    Map<Integer,String> useHeaders = inclusions;

    Map<Integer, Integer> inclusionTotals = Maps.newHashMap();
    for (int i=0; i<useHeaders.size(); i++) {
      inclusionTotals.put(i,0);
    }

    Row title = sheet.createRow(currentRow++);
    title.createCell(0).setCellValue("Inclusion Summary (criteria passed: "+ItpcUtils.valueToInclusion(value)+")");

    Row headerRow = sheet.createRow(currentRow++);
    headerRow.createCell(0).setCellValue("Site ID");
    headerRow.createCell(1).setCellValue("N");
    for (int i=0; i<useHeaders.size(); i++) {
      headerRow.createCell(i+2).setCellValue(useHeaders.get(i));
    }

    for (int i=0; i<ItpcUtils.SITE_COUNT; i++) {
      Row siteRow = sheet.createRow(currentRow++);
      String siteName = (new Integer(i+1)).toString();
      siteRow.createCell(0).setCellValue(siteName);
      siteRow.createCell(1).setCellValue(projectSubjectCount.get(i));

      for (int j=0; j<useHeaders.size(); j++) {
        siteRow.createCell(j+2).setCellValue(projectMap.get(i).get(j).get(value));
        inclusionTotals.put(j, inclusionTotals.get(j) + projectMap.get(i).get(j).get(value));
      }
    }

    Row totalsRow = sheet.createRow(currentRow++);
    totalsRow.createCell(0).setCellValue("Total");
    totalsRow.createCell(1).setCellValue(getSubjectTotal());
    for (int i=0; i<useHeaders.size(); i++) {
      totalsRow.createCell(i+2).setCellValue(inclusionTotals.get(i));
    }

    return currentRow;
  }

  private int writeExclusionTable(Sheet sheet, int currentRow, Value value) {

    Map<Integer,Integer> exclusionTotals = Maps.newHashMap();
    for (Integer i : exclusions.keySet()) {
      exclusionTotals.put(i, 0);
    }

    Row titleRow = sheet.createRow(currentRow++);
    titleRow.createCell(0).setCellValue("Exclusion Summary ("+ItpcUtils.valueToExclusion(value)+")");

    Row headerRow = sheet.createRow(currentRow++);
    headerRow.createCell(0).setCellValue("Site ID");
    headerRow.createCell(1).setCellValue("N");
    for (Integer i : exclusions.keySet()) {
      headerRow.createCell(i+2).setCellValue(exclusions.get(i));
    }

    for (int i=0; i<ItpcUtils.SITE_COUNT; i++) {
      Row siteRow = sheet.createRow(currentRow++);
      siteRow.createCell(0).setCellValue(i+1);
      siteRow.createCell(1).setCellValue(projectSubjectCount.get(i));

      for (Integer j : exclusions.keySet()) {
        siteRow.createCell(j+2).setCellValue(projectExcludeMap.get(i).get(j).get(value));
        exclusionTotals.put(j, exclusionTotals.get(j)+projectExcludeMap.get(i).get(j).get(value));
      }
    }

    Row totalsRow = sheet.createRow(currentRow++);
    totalsRow.createCell(0).setCellValue("Total");
    totalsRow.createCell(1).setCellValue(getSubjectTotal());
    for (int i=0; i<exclusions.size(); i++) {
      totalsRow.createCell(i+2).setCellValue(exclusionTotals.get(i));
    }

    return currentRow;
  }

  
  private int writeCriteriaTable(Sheet sheet, int currentRow, Value value) {

    Map<Integer,Integer> criteriaTotals = Maps.newHashMap();
    for (Integer i : criteriaLabels.keySet()) {
      criteriaTotals.put(i, 0);
    }

    Row titleRow = sheet.createRow(currentRow++);
    titleRow.createCell(0).setCellValue("Criteria ("+ItpcUtils.valueToInclusion(value)+")");

    Row headerRow = sheet.createRow(currentRow++);
    headerRow.createCell(0).setCellValue("Site ID");
    for (Integer i : criteriaLabels.keySet()) {
      headerRow.createCell(i+1).setCellValue(criteriaLabels.get(i));
    }

    for (int i=0; i<ItpcUtils.SITE_COUNT; i++) {
      Row siteRow = sheet.createRow(currentRow++);
      siteRow.createCell(0).setCellValue(i+1);

      for (Integer j : criteriaLabels.keySet()) {
        siteRow.createCell(j+1).setCellValue(projectCritMap.get(i).get(j).get(value));
        criteriaTotals.put(j, criteriaTotals.get(j)+projectCritMap.get(i).get(j).get(value));
      }
    }

    Row totalsRow = sheet.createRow(currentRow++);
    totalsRow.createCell(0).setCellValue("Total");
    for (int i=0; i<criteriaLabels.size(); i++) {
      totalsRow.createCell(i + 1).setCellValue(criteriaTotals.get(i));
    }

    return currentRow;
  }

  private int getSubjectTotal() {
    int totalSubjects = 0;
    for (Integer siteId : projectSubjectCount.keySet()) {
      totalSubjects += projectSubjectCount.get(siteId);
    }
    return totalSubjects;
  }
}
