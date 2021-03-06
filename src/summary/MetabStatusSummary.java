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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.pharmgkb.Genotype;
import org.pharmgkb.Subject;
import util.GenotypeComparator;
import util.Value;

import java.util.SortedMap;

/**
 * Created by IntelliJ IDEA.
 * User: whaleyr
 * Date: Aug 3, 2010
 */
public class MetabStatusSummary extends AbstractSummary {
  private static final String sf_sheetTitle = "Metabolizer Summary";
  private static final String[] metabTable = new String[]{
      "Ultrarapid one (score 4.0)\tUM/UM (score 4)\tno\tno",  // 0
      "Ultrarapid two (score 3.5)\tUM/UM (score 4)\tyes\tno", // 1
      "Ultrarapid three (score 3.0)\tEM/UM (score 3)\tno\tno",// 2
      "Extensive one (score 2.5)\tIM/UM (score 2.5)\tno\tno", // 3
      "Extensive one (score 2.5)\tEM/UM(score 3)\tyes\tno",   // 4
      "Extensive two (score 2.0)\tEM/EM (score 2)\tno\tno",   // 5
      "Extensive two (score 2.0)\tIM/UM (score 2.5)\tyes\tno",// 6
      "Extensive two (score 2.0)\tPM/UM (score 2)\tno\tno",   // 7
      "Intermediate one (score 1.5)\tPM/IM (score 2.0)\tyes\tno",   // 8
      "Intermediate one (score 1.5)\tEM/IM (score 1.5)\tno\tno",    // 9
      "Intermediate one (score 1.5)\tEM/EM (score 2.0)\tyes\tno",   //10
      "Intermediate two (score 1.0)\tIM/IM (score 1.0)\tno\tno",    //11
      "Intermediate two (score 1.0)\tEM/PM (score 1.0)\tno\tno",    //12
      "Intermediate two (score 1.0)\tEM/IM (score 1.5)\tyes\tno",   //13
      "Intermediate three (score 0.5)\tEM/PM (score 1.0)\tyes\tno", //14
      "Intermediate three (score 0.5)\tIM/IM (score 1.0)\tyes\tno", //15
      "Intermediate three (score 0.5)\tPM/IM (score 0.5)\tno\tno",  //16
      "Poor (score 0)\tunknown\tno\tyes",                 //17
      "Poor (score 0)\tany genotype\tno\tyes",            //18
      "Poor (score 0)\tPM/IM (score 0.5)\tyes\tno",       //19
      "Poor (score 0)\tPM/PM (score 0)\tyes\tno",         //20
      "Poor (score 0)\tPM/PM (score 0)\tno\tno",          //21
      "Poor (score 0)\tPM/PM (score 0)\tunknown\tunknown",//22
      "No Medication Data Available\t\t\t",               //23
      "Uncategorized\t\t\t"                               //24
  };
  protected int[] metabStatusTotals;
  protected SortedMap<String,Integer> metabStatusByAssignment = Maps.newTreeMap();
  protected SortedMap<Genotype,Integer> metabTypeMap = Maps.newTreeMap(GenotypeComparator.getComparator());

  public MetabStatusSummary() {
    metabStatusTotals = new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    metabStatusByAssignment.put(Genotype.EXTENSIVE, 0);
    metabStatusByAssignment.put(Genotype.INTERMEDIATE, 0);
    metabStatusByAssignment.put(Genotype.POOR, 0);
    metabStatusByAssignment.put(Genotype.UNKNOWN, 0);
  }

  public String getSheetTitle() {
    return sf_sheetTitle;
  }

  public void addSubject(Subject subject) {
    if (subject != null) {
      if (subject.getPotent() == Value.Yes && subject.getGenotypeFinal().isUnknown()) {
        metabStatusTotals[17]++;
      }

      else if (subject.getPotent() == Value.Yes) {
        metabStatusTotals[18]++;
      }
      else if (subject.getGenotypeFinal().is(Genotype.Metabolizer.UM,Genotype.Metabolizer.UM) && subject.getWeak() == Value.No) {
        metabStatusTotals[0]++;
      }
      else if (subject.getGenotypeFinal().is(Genotype.Metabolizer.UM,Genotype.Metabolizer.UM) && subject.getWeak() == Value.Yes) {
        metabStatusTotals[1]++;
      }
      else if (subject.getGenotypeFinal().is(Genotype.Metabolizer.EM,Genotype.Metabolizer.UM) && subject.getWeak() == Value.No) {
        metabStatusTotals[2]++;
      }
      else if (subject.getGenotypeFinal().is(Genotype.Metabolizer.IM,Genotype.Metabolizer.UM) && subject.getWeak() == Value.No) {
        metabStatusTotals[3]++;
      }
      else if (subject.getGenotypeFinal().is(Genotype.Metabolizer.EM,Genotype.Metabolizer.UM) && subject.getWeak() == Value.Yes) {
        metabStatusTotals[4]++;
      }
      else if (subject.getGenotypeFinal().is(Genotype.Metabolizer.EM,Genotype.Metabolizer.EM) && subject.getWeak() == Value.No) {
        metabStatusTotals[5]++;
      }
      else if (subject.getGenotypeFinal().is(Genotype.Metabolizer.IM,Genotype.Metabolizer.UM) && subject.getWeak() == Value.Yes) {
        metabStatusTotals[6]++;
      }
      else if (subject.getGenotypeFinal().is(Genotype.Metabolizer.PM,Genotype.Metabolizer.UM) && subject.getWeak() == Value.No) {
        metabStatusTotals[7]++;
      }
      else if (subject.getGenotypeFinal().is(Genotype.Metabolizer.IM,Genotype.Metabolizer.PM) && subject.getWeak() == Value.Yes) {
        metabStatusTotals[8]++;
      }
      else if (subject.getGenotypeFinal().is(Genotype.Metabolizer.EM, Genotype.Metabolizer.IM) && subject.getWeak() == Value.No) {
        metabStatusTotals[9]++;
      }
      else if (subject.getGenotypeFinal().is(Genotype.Metabolizer.EM,Genotype.Metabolizer.EM) && subject.getWeak() == Value.Yes) {
        metabStatusTotals[10]++;
      }
      else if (subject.getGenotypeFinal().is(Genotype.Metabolizer.IM,Genotype.Metabolizer.IM) && subject.getWeak() == Value.No) {
        metabStatusTotals[11]++;
      }
      else if (subject.getGenotypeFinal().is(Genotype.Metabolizer.EM,Genotype.Metabolizer.PM) && subject.getWeak() == Value.No) {
        metabStatusTotals[12]++;
      }
      else if (subject.getGenotypeFinal().is(Genotype.Metabolizer.EM,Genotype.Metabolizer.IM) && subject.getWeak() == Value.Yes) {
        metabStatusTotals[13]++;
      }
      else if (subject.getGenotypeFinal().is(Genotype.Metabolizer.EM,Genotype.Metabolizer.PM) && subject.getWeak() == Value.Yes) {
        metabStatusTotals[14]++;
      }
      else if (subject.getGenotypeFinal().is(Genotype.Metabolizer.IM,Genotype.Metabolizer.IM) && subject.getWeak() == Value.Yes) {
        metabStatusTotals[15]++;
      }
      else if (subject.getGenotypeFinal().is(Genotype.Metabolizer.IM,Genotype.Metabolizer.PM) && subject.getWeak() == Value.No) {
        metabStatusTotals[16]++;
      }
      else if (subject.getGenotypeFinal().is(Genotype.Metabolizer.IM,Genotype.Metabolizer.PM) && subject.getWeak() == Value.Yes) {
        metabStatusTotals[19]++;
      }
      else if (subject.getGenotypeFinal().is(Genotype.Metabolizer.PM,Genotype.Metabolizer.PM) && subject.getWeak() == Value.Yes) {
        metabStatusTotals[20]++;
      }
      else if (subject.getGenotypeFinal().is(Genotype.Metabolizer.PM,Genotype.Metabolizer.PM) && subject.getWeak() == Value.No) {
        metabStatusTotals[21]++;
      }
      else if (subject.getGenotypeFinal().is(Genotype.Metabolizer.PM,Genotype.Metabolizer.PM) && subject.getWeak() == Value.Unknown && subject.getPotent() == Value.Unknown) {
        metabStatusTotals[22]++;
      }
      else if (subject.getWeak() == Value.Unknown && subject.getPotent() == Value.Unknown) {
        metabStatusTotals[23]++;
      }
      else if (!(subject.getGenotypeFinal().isUnknown() && subject.getWeak() == Value.No && subject.getPotent() == Value.No)) {
        // sf_logger.warn("No metab. status for: " + row.get(subjectId) + " :: " + metab.toString() + " :: " + subject.getWeak() + "/" + subject.getPotent());
        metabStatusTotals[24]++;
      }
      else {
        // sf_logger.warn("No matching logic for: " + row.get(subjectId) + " :: " + metab.toString() + " :: " + subject.getWeak() + "/" + subject.getPotent());
        metabStatusTotals[24]++;
      }

      String metabGroup = subject.getGenotypeFinal().getMetabolizerGroup();
      metabStatusByAssignment.put(metabGroup, metabStatusByAssignment.get(metabGroup) + 1);

      if (!metabTypeMap.containsKey(subject.getGenotypeFinal())) {
        metabTypeMap.put(subject.getGenotypeFinal(), 1);
      }
      else {
        metabTypeMap.put(subject.getGenotypeFinal(), metabTypeMap.get(subject.getGenotypeFinal())+1);
      }
    }
  }

  public void writeToWorkbook(Workbook wb) {
    Sheet sheet = getSheet(wb);
    int currentRow = 0;

    Row header = sheet.createRow(currentRow++);
    header.createCell(0).setCellValue("CYP2D6 Metabolizer Status (column DQ)");
    header = sheet.createRow(currentRow++);
    header.createCell(0).setCellValue("Status");
    header.createCell(1).setCellValue("n");

    for (String group : metabStatusByAssignment.keySet()) {
      Row data = sheet.createRow(currentRow++);
      data.createCell(0).setCellValue(group);
      data.createCell(1).setCellValue(metabStatusByAssignment.get(group));
    }

    currentRow += 3;

    header = sheet.createRow(currentRow++);
    header.createCell(0).setCellValue("CYP2D6 Metabolizer Types");
    header = sheet.createRow(currentRow++);
    header.createCell(0).setCellValue("Type");
    header.createCell(1).setCellValue("n");

    for (Genotype geno : metabTypeMap.keySet()) {
      Row data = sheet.createRow(currentRow++);
      if (geno.isUnknown()) {
        data.createCell(0).setCellValue("Unknown");
      }
      else {
        data.createCell(0).setCellValue(geno.getMetabolizerStatus());
      }
      data.createCell(1).setCellValue(metabTypeMap.get(geno));
    }

    currentRow += 3;

    header = sheet.createRow(currentRow++);
    header.createCell(0).setCellValue("CYP2D6 Metabolizer Status based on Score (columns DR-DU)");
    header = sheet.createRow(currentRow++);
    header.createCell(0).setCellValue("Status (score)");
    header.createCell(1).setCellValue("Function (score)");
    header.createCell(2).setCellValue("Weak CYP2D6 Inhibitor Administered");
    header.createCell(3).setCellValue("Potent CYP2D6 Inhibitor Administered");
    header.createCell(4).setCellValue("n");

    for (int i=0; i<metabTable.length; i++) {
      String fields[] = metabTable[i].split("\t");
      Row data = sheet.createRow(currentRow++);
      data.createCell(0).setCellValue(fields[0]);
      if (fields.length==4) {
        data.createCell(1).setCellValue(fields[1]);
        data.createCell(2).setCellValue(fields[2]);
        data.createCell(3).setCellValue(fields[3]);
      }
      data.createCell(4).setCellValue(metabStatusTotals[i]);
    }
  }
}
