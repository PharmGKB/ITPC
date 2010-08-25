package summary;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import util.Value;

/**
 * Created by IntelliJ IDEA.
 * User: whaleyr
 * Date: Aug 3, 2010
 */
public class MetabStatusSummary {
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

  public MetabStatusSummary() {
    metabStatusTotals = new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
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

      else if (subject.getGenotypeFinal().is(Genotype.Metabolizer.EM,Genotype.Metabolizer.IM) && subject.getWeak() == Value.No) {
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
    }
  }

  public void writeToWorkbook(Workbook wb) {
    int sheetIdx = wb.getSheetIndex(sf_sheetTitle);
    if (sheetIdx >= 0) {
      wb.removeSheetAt(sheetIdx);
    }
    Sheet sheet = wb.createSheet(sf_sheetTitle);

    Row header = sheet.createRow(0);
    header.createCell(0).setCellValue("Final CYP2D6 Metabolizer Status and Score");
    header.createCell(1).setCellValue("Genotype and Genotype Score");
    header.createCell(2).setCellValue("Weak CYP2D6 Inhibitor Administered");
    header.createCell(3).setCellValue("Potent CYP2D6 Inhibitor Administered");
    header.createCell(4).setCellValue("n");

    int rowNum = 1;
    for (int i=0; i<metabTable.length; i++) {
      String fields[] = metabTable[i].split("\t");
      Row data = sheet.createRow(rowNum);
      data.createCell(0).setCellValue(fields[0]);
      if (fields.length==4) {
        data.createCell(1).setCellValue(fields[1]);
        data.createCell(2).setCellValue(fields[2]);
        data.createCell(3).setCellValue(fields[3]);
      }
      data.createCell(4).setCellValue(metabStatusTotals[i]);
      rowNum++;
    }
  }
}
