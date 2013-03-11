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
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.pharmgkb.Subject;

import java.util.Map;
import java.util.SortedMap;

/**
 * Created by IntelliJ IDEA.
 * User: whaleyr
 * Date: Aug 3, 2010
 */
public class GenotypeSummary extends AbstractSummary {
  private static final Logger sf_logger = Logger.getLogger(GenotypeSummary.class);
  private static final String sf_sheetTitle = "Genotype Summary";
  private Map<String,Integer> countMap = Maps.newHashMap();
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
    sourceMap.put(Subject.SampleSource.BUCCAL, starFour);
    starFour = new int[]{0,0,0,0};
    sourceMap.put(Subject.SampleSource.TUMOR_FROZEN, starFour);
    starFour = new int[]{0,0,0,0};
    sourceMap.put(Subject.SampleSource.NORMAL_PARAFFIN, starFour);
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

      if (subject.getSampleSources().size()>1) {
        sf_logger.warn("Multiple sample sources for "+subject.getSubjectId());
      }
      Subject.SampleSource source = subject.getSampleSources().iterator().next();

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

    for (Subject.SampleSource source : Subject.SampleSource.values()) {
      row = sheet.createRow(++rowNum);
      row.createCell(0).setCellValue(source.toString());
      row.createCell(1).setCellValue(sourceMap.get(source)[fourTotal]);
      row.createCell(2).setCellValue(sourceMap.get(source)[fourHomo]);
      row.createCell(3).setCellValue(sourceMap.get(source)[fourHeto]);
      row.createCell(4).setCellValue(sourceMap.get(source)[fourNon]);
    }

    rowNum++;
    row = sheet.createRow(++rowNum);
    row.createCell(0).setCellValue("Sample Source by Site");
    row = sheet.createRow(++rowNum);
    row.createCell(0).setCellValue("Site");

    int colMarker = 0;
    for (Subject.SampleSource source : Subject.SampleSource.values()) {
      row.createCell(colMarker*2+1).setCellValue(source.name()+" N");
      row.createCell(colMarker*2+2).setCellValue(source.name()+" %");
      colMarker++;
    }

    int[] totals = new int[]{0,0,0,0,0,0,0,0,0,0,0,0};
    CellStyle pctStyle = sheet.getWorkbook().createCellStyle();
    DataFormat format = sheet.getWorkbook().createDataFormat();
    pctStyle.setDataFormat(format.getFormat("0.0%"));

    for (Integer i : tumorFreqMap.keySet()) {
      row = sheet.createRow(++rowNum);
      Integer siteTotal = tumorFreqMap.get(i)[Subject.SampleSource.TUMOR_FFP.ordinal()]
              + tumorFreqMap.get(i)[Subject.SampleSource.TUMOR_FROZEN.ordinal()]
              + tumorFreqMap.get(i)[Subject.SampleSource.BLOOD.ordinal()]
              + tumorFreqMap.get(i)[Subject.SampleSource.BUCCAL.ordinal()]
              + tumorFreqMap.get(i)[Subject.SampleSource.NORMAL_PARAFFIN.ordinal()]
              + tumorFreqMap.get(i)[Subject.SampleSource.UNKNOWN.ordinal()];

      Cell cell;
      row.createCell(0).setCellValue(i+1);

      colMarker = 0;
      for (Subject.SampleSource source : Subject.SampleSource.values()) {
        Integer total = tumorFreqMap.get(i)[source.ordinal()];
        Float pct = (float)tumorFreqMap.get(i)[source.ordinal()] / (float)siteTotal;

        row.createCell(colMarker*2+1).setCellValue(total);

        cell = row.createCell(colMarker*2+2);
        cell.setCellValue(pct);
        cell.setCellStyle(pctStyle);

        totals[source.ordinal()]+=total;
        colMarker++;
      }
    }
    row = sheet.createRow(++rowNum);
    int projectTotal = totals[Subject.SampleSource.TUMOR_FFP.ordinal()]
            + totals[Subject.SampleSource.TUMOR_FROZEN.ordinal()]
            + totals[Subject.SampleSource.NORMAL_PARAFFIN.ordinal()]
            + totals[Subject.SampleSource.BLOOD.ordinal()]
            + totals[Subject.SampleSource.BUCCAL.ordinal()]
            + totals[Subject.SampleSource.UNKNOWN.ordinal()];

    colMarker = 0;
    for (Subject.SampleSource source : Subject.SampleSource.values()) {
      row.createCell(colMarker*2+1).setCellValue(totals[source.ordinal()]);

      Cell cell = row.createCell(colMarker*2+2);
      cell.setCellValue((float)totals[source.ordinal()] / (float)projectTotal);
      cell.setCellStyle(pctStyle);
      colMarker++;
    }
  }

  enum StarFourStatus {Homozygous, Heterozygous, NonFour}
}
