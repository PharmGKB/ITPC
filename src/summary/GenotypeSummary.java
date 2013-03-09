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

package summary;

import com.google.common.collect.Maps;
import org.apache.poi.ss.usermodel.*;
import org.pharmgkb.Subject;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

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
  private SortedMap<Integer,int[]> tumorFreqMap = Maps.newTreeMap();

  public GenotypeSummary() {
    int[] starFour = new int[]{0,0,0,0};
    sourceMap.put(Subject.SampleSource.TUMOR_FFP, starFour);
    starFour = new int[]{0,0,0,0};
    sourceMap.put(Subject.SampleSource.BLOOD, starFour);
    starFour = new int[]{0,0,0,0};
    sourceMap.put(Subject.SampleSource.UNKNOWN, starFour);
    for (int i=0; i<12; i++) {
      tumorFreqMap.put(i, new int[]{0,0,0,0,0,0});
    }
  }

  public String getSheetTitle() {
    return sf_sheetTitle;
  }

  public void addSubject(Subject subject) {
    if (subject != null) {
      int siteIdx = Integer.parseInt(subject.getProjectSite())-1;

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

      Subject.SampleSource source = Subject.SampleSource.UNKNOWN;
      if (subject.isSampleSourceBlood() && !subject.isSampleSourceTumor()) {
        source = Subject.SampleSource.BLOOD;
      }
      if (!subject.isSampleSourceBlood() && subject.isSampleSourceTumor()) {
        source = Subject.SampleSource.TUMOR_FFP;
      }

      int[] totals = sourceMap.get(source);
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

      tumorFreqMap.get(siteIdx)[source.ordinal()]++;
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
    row.createCell(0).setCellValue("*4 Status by Sample Source");
    row = sheet.createRow(++rowNum);
    row.createCell(0).setCellValue("Source");
    row.createCell(1).setCellValue("Count");
    row.createCell(2).setCellValue("*4 Homozygous");
    row.createCell(3).setCellValue("*4 Heterozygous");
    row.createCell(4).setCellValue("Non-*4");

    row = sheet.createRow(++rowNum);
    row.createCell(0).setCellValue(Subject.SampleSource.TUMOR_FFP.toString());
    row.createCell(1).setCellValue(sourceMap.get(Subject.SampleSource.TUMOR_FFP)[fourTotal]);
    row.createCell(2).setCellValue(sourceMap.get(Subject.SampleSource.TUMOR_FFP)[fourHomo]);
    row.createCell(3).setCellValue(sourceMap.get(Subject.SampleSource.TUMOR_FFP)[fourHeto]);
    row.createCell(4).setCellValue(sourceMap.get(Subject.SampleSource.TUMOR_FFP)[fourNon]);

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

    rowNum++;
    row = sheet.createRow(++rowNum);
    row.createCell(0).setCellValue("Sample Source by Site");
    row = sheet.createRow(++rowNum);
    row.createCell(0).setCellValue("Site");
    row.createCell(1).setCellValue("Tumor N");
    row.createCell(2).setCellValue("Tumor %");
    row.createCell(3).setCellValue("Blood N");
    row.createCell(4).setCellValue("Blood %");
    row.createCell(5).setCellValue("Unknown N");
    row.createCell(6).setCellValue("Unknown %");

    int[] totals = new int[]{0,0,0,0,0,0};
    CellStyle pctStyle = sheet.getWorkbook().createCellStyle();
    DataFormat format = sheet.getWorkbook().createDataFormat();
    pctStyle.setDataFormat(format.getFormat("0.0%"));

    for (Integer i : tumorFreqMap.keySet()) {
      row = sheet.createRow(++rowNum);
      Integer siteTotal = tumorFreqMap.get(i)[Subject.SampleSource.TUMOR_FFP.ordinal()] + tumorFreqMap.get(i)[Subject.SampleSource.BLOOD.ordinal()] + tumorFreqMap.get(i)[Subject.SampleSource.UNKNOWN.ordinal()];

      Integer tumorTotal = tumorFreqMap.get(i)[Subject.SampleSource.TUMOR_FFP.ordinal()];
      Float tumorPct = (float)tumorFreqMap.get(i)[Subject.SampleSource.TUMOR_FFP.ordinal()] / (float)siteTotal;
      Integer bloodTotal = tumorFreqMap.get(i)[Subject.SampleSource.BLOOD.ordinal()];
      Float bloodPct = (float)tumorFreqMap.get(i)[Subject.SampleSource.BLOOD.ordinal()] / (float)siteTotal;
      Integer unkTotal = tumorFreqMap.get(i)[Subject.SampleSource.UNKNOWN.ordinal()];
      Float unkPct = (float)tumorFreqMap.get(i)[Subject.SampleSource.UNKNOWN.ordinal()] / (float)siteTotal;

      Cell cell;
      row.createCell(0).setCellValue(i+1);
      row.createCell(1).setCellValue(tumorTotal);

      cell = row.createCell(2);
      cell.setCellValue(tumorPct);
      cell.setCellStyle(pctStyle);

      row.createCell(3).setCellValue(bloodTotal);

      cell = row.createCell(4);
      cell.setCellValue(bloodPct);
      cell.setCellStyle(pctStyle);

      row.createCell(5).setCellValue(unkTotal);

      cell = row.createCell(6);
      cell.setCellValue(unkPct);
      cell.setCellStyle(pctStyle);

      totals[Subject.SampleSource.TUMOR_FFP.ordinal()]+=tumorTotal;
      totals[Subject.SampleSource.BLOOD.ordinal()]+=bloodTotal;
      totals[Subject.SampleSource.UNKNOWN.ordinal()]+=unkTotal;
    }
    row = sheet.createRow(++rowNum);
    int projectTotal = totals[Subject.SampleSource.TUMOR_FFP.ordinal()] + totals[Subject.SampleSource.BLOOD.ordinal()] + totals[Subject.SampleSource.UNKNOWN.ordinal()];
    row.createCell(1).setCellValue(totals[Subject.SampleSource.TUMOR_FFP.ordinal()]);

    Cell cell = row.createCell(2);
    cell.setCellValue((float)totals[Subject.SampleSource.TUMOR_FFP.ordinal()] / (float)projectTotal);
    cell.setCellStyle(pctStyle);

    row.createCell(3).setCellValue(totals[Subject.SampleSource.BLOOD.ordinal()]);

    cell = row.createCell(4);
    cell.setCellValue((float)totals[Subject.SampleSource.BLOOD.ordinal()] / (float)projectTotal);
    cell.setCellStyle(pctStyle);

    row.createCell(5).setCellValue(totals[Subject.SampleSource.UNKNOWN.ordinal()]);

    cell = row.createCell(6);
    cell.setCellValue((float)totals[Subject.SampleSource.UNKNOWN.ordinal()] / (float)projectTotal);
    cell.setCellStyle(pctStyle);
  }

  enum StarFourStatus {Homozygous, Heterozygous, NonFour}
}
