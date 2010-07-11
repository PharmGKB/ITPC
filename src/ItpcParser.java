/*
 ----- BEGIN LICENSE BLOCK -----
 Version: MPL 1.1/GPL 2.0/LGPL 2.1

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with the
 License. You may obtain a copy of the License at http://www.mozilla.org/MPL/

 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 the specific language governing rights and limitations under the License.

 The Original Code is PharmGen.

 The Initial Developer of the Original Code is PharmGKB (The Pharmacogenetics
 and Pharmacogenetics Knowledge Base, supported by NIH U01GM61374). Portions
 created by the Initial Developer are Copyright (C) 2010 the Initial Developer.
 All Rights Reserved.

 Contributor(s):

 Alternatively, the contents of this file may be used under the terms of
 either the GNU General Public License Version 2 or later (the "GPL"), or the
 GNU Lesser General Public License Version 2.1 or later (the "LGPL"), in
 which case the provisions of the GPL or the LGPL are applicable instead of
 those above. If you wish to allow use of your version of this file only
 under the terms of either the GPL or the LGPL, and not to allow others to
 use your version of this file under the terms of the MPL, indicate your
 decision by deleting the provisions above and replace them with the notice
 and other provisions required by the GPL or the LGPL. If you do not delete
 the provisions above, a recipient may use your version of this file under
 the terms of any one of the MPL, the GPL or the LGPL.

 ----- END LICENSE BLOCK -----
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;
import util.CliHelper;
import util.POIUtils;
import util.PoiWorksheetIterator;


/**
 * @author Ryan Whaley
 */
public class ItpcParser {

  private static final Logger sf_logger = Logger.getLogger(ItpcParser.class);
  private static final String sf_sheetName = "Combined_Data";
  private static final Pattern sf_alleleRegex = Pattern.compile("\\*\\d+");

  private static final Map<String,String> genoGroup = new HashMap<String,String>();
  static {
    genoGroup.put("*3","PM");
    genoGroup.put("*4","PM");
    genoGroup.put("*5","PM");
    genoGroup.put("*6","PM");
    genoGroup.put("*7","PM");
    genoGroup.put("*8","PM");
    genoGroup.put("*11","PM");
    genoGroup.put("*12","PM");
    genoGroup.put("*13","PM");
    genoGroup.put("*14","PM");
    genoGroup.put("*15","PM");
    genoGroup.put("*16","PM");
    genoGroup.put("*18","PM");
    genoGroup.put("*19","PM");
    genoGroup.put("*20","PM");
    genoGroup.put("*40","PM");
    genoGroup.put("*42","PM");
    genoGroup.put("*44","PM");
    genoGroup.put("*56","PM");
    genoGroup.put("*36","PM");
    genoGroup.put("*38","PM");
    genoGroup.put("*4XN","PM");

    genoGroup.put("*9","IM");
    genoGroup.put("*10","IM");
    genoGroup.put("*17","IM");
    genoGroup.put("*29","IM");
    genoGroup.put("*37","IM");
    genoGroup.put("*41","IM");
    genoGroup.put("*45","IM");
    genoGroup.put("*46","IM");

    genoGroup.put("*1","EM");
    genoGroup.put("*2","EM");
    genoGroup.put("*33","EM");
    genoGroup.put("*35","EM");
    genoGroup.put("*39","EM");
    genoGroup.put("*43","EM");

    genoGroup.put("*1XN", "UM");
    genoGroup.put("*2XN", "UM");
    genoGroup.put("*9XN", "UM");
    genoGroup.put("*10XN", "UM");
    genoGroup.put("*35XN", "UM");
    genoGroup.put("*39XN", "UM");
    genoGroup.put("*41XN", "UM");
    genoGroup.put("*45XN", "UM");
  }
  private static final Map<String,Float> genoScore = new HashMap<String,Float>();
  static {
    genoScore.put("*3",0f);
    genoScore.put("*4",0f);
    genoScore.put("*5",0f);
    genoScore.put("*6",0f);
    genoScore.put("*7",0f);
    genoScore.put("*11",0f);
    genoScore.put("*12",0f);
    genoScore.put("*16",0f);
    genoScore.put("*40",0f);
    genoScore.put("*42",0f);
    genoScore.put("*56",0f);
    genoScore.put("*36",0f);
    genoScore.put("*4XN",0f);

    genoScore.put("*9", 0.5f);
    genoScore.put("*10",0.5f);
    genoScore.put("*17",0.5f);
    genoScore.put("*29",0.5f);
    genoScore.put("*37",0.5f);
    genoScore.put("*41",0.5f);
    genoScore.put("*45",0.5f);
    genoScore.put("*46",0.5f);

    genoScore.put("*1",1f);
    genoScore.put("*2",1f);
    genoScore.put("*33",1f);
    genoScore.put("*35",1f);
    genoScore.put("*39",1f);
    genoScore.put("*43",1f);

    genoScore.put("*1XN",2f);
    genoScore.put("*2XN",2f);
    genoScore.put("*9XN",2f);
    genoScore.put("*10XN",2f);
    genoScore.put("*35XN",2f);
    genoScore.put("*39XN",2f);
    genoScore.put("*41XN",2f);
    genoScore.put("*45XN",2f);
  }
  private static final Map<String,Integer> genoPriority = new HashMap<String,Integer>();
  static {
    genoPriority.put("PM",1);
    genoPriority.put("IM",2);
    genoPriority.put("EM",3);
    genoPriority.put("UM",4);
  }
  private static List<Integer> summaryColumns = new ArrayList<Integer>();
  private static final String[] metabTable = new String[]{
      "Ultrarapid one (score 4.0)\tUM/UM (score 4)\tno\tno",  //0
      "Ultrarapid two (score 3.5)\tUM/UM (score 4)\tyes\tno", //1
      "Ultrarapid three (score 3.0)\tEM/UM (score 3)\tno\tno",//2
      "Extensive one (score 2.5)\tIM/UM (score 2.5)\tno\tno", //3
      "Extensive one (score 2.5)\tEM/UM(score 3)\tyes\tno",   //4
      "Extensive two (score 2.0)\tEM/EM (score 2)\tno\tno",   //5
      "Extensive two (score 2.0)\tIM/UM (score 2.5)\tyes\tno",//6
      "Extensive two (score 2.0)\tPM/UM (score 2)\tno\tno",   //7
      "Intermediate one (score 1.5)\tPM/IM (score 2.0)\tyes\tno",   //8
      "Intermediate one (score 1.5)\tEM/IM (score 1.5)\tno\tno",    //9
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

  private enum DataSet {
    ALL,
    INCLUDED,
    EXCLUDED
  }

  private File m_fileInput;
  private DataSet m_dataSet;

  private ItpcParser() {
    sf_logger.info("Initializing ITPC Parser");
  }

  public static void main(String args[]) {
    try {
      ItpcParser parser = new ItpcParser();
      parser.parseInput(args);
      parser.parseItpcFile();
    }
    catch (Exception ex) {
      sf_logger.error("Error running parser", ex);
    }
  }

  private void parseItpcFile() {

    InputStream inputFileStream = null;
    FileWriter outputSummaryWriter = null;
    int idx = 0;
    SortedMap<Integer,Integer> nonStar4CountMap = new TreeMap<Integer,Integer>();
    SortedMap<Integer,Integer> siteTotalMap = new TreeMap<Integer,Integer>();
    int[] metabStatusTotals = new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

    try {
      DataSet filter = this.getDataSet();
      File inputFile = this.getFileInput();
      File outputFile;
      String dateTag = getDate();

      switch(filter) {
        case INCLUDED:
          outputFile = new File(inputFile.getAbsolutePath().replaceAll("\\.xls","." + dateTag + ".included.xls"));
          break;
        case EXCLUDED:
          outputFile = new File(inputFile.getAbsolutePath().replaceAll("\\.xls","." + dateTag + ".excluded.xls"));
          break;
        default:
          outputFile = new File(inputFile.getAbsolutePath().replaceAll("\\.xls","." + dateTag + ".all.xls"));
      }
      File outputSummary = new File(inputFile.getAbsolutePath().replaceAll("\\.xls","." + dateTag + ".char.csv"));
      outputSummaryWriter = new FileWriter(outputSummary);
      sf_logger.info("Generating output: " + outputFile);
      sf_logger.info("Generating summary: " + outputSummary);

      // setup reader
      inputFileStream = new FileInputStream(inputFile);
      Workbook inputWorkbook = WorkbookFactory.create(inputFileStream);
      Sheet inputSheet = inputWorkbook.getSheet(sf_sheetName);
      if (inputSheet == null) {
        System.err.println("Cannot find worksheet named " + sf_sheetName);
        return;
      }
      PoiWorksheetIterator inputIt = new PoiWorksheetIterator(inputSheet);

      // xxx: parse headers
      List<String> headers = inputIt.next();
      int subjectId = -1;
      int projectSiteIdx = -1;
      int ageIdx = -1;
      int menoStatusIdx = -1;
      int metastaticIdx = -1;
      int erStatusIdx = -1;
      int durationIdx = -1;
      int tamoxDoseIdx = -1;
      int tumorSourceIdx = -1;
      int bloodSourceIdx = -1;
      int priorHistoryIdx = -1;
      int priorDcisIdx = -1;
      int chemoIdx = -1;
      int hormoneIdx = -1;
      int systemicTherIdx = -1;
      int followupIdx = -1;
      int timeBtwSurgTamoxIdx = -1;
      int firstAdjEndoTherIdx = -1;
      int genoSourceIdx1 = -1;
      int genoSourceIdx2 = -1;
      int genoSourceIdx3 = -1;
      int projectNotesIdx = -1;

      int allele1idx = -1;
      int allele2idx = -1;
      int allele1finalIdx = -1;
      int allele2finalIdx = -1;
      int callCommentsIdx = -1;
      int genotypeIdx = -1;
      int weakIdx = -1;
      int potentIdx = -1;
      int metabStatusIdx = -1;

      int incAgeIdx = -1;
      int incNonmetaIdx = -1;
      int incPriorHistIdx = -1;
      int incErPosIdx = -1;
      int incSysTherIdx = -1;
      int incAdjTamoxIdx = -1;
      int incDurationIdx = -1;
      int incTamoxDoseIdx = -1;
      int incChemoIdx = -1;
      int incHormoneIdx = -1;
      int incDnaCollectionIdx = -1;
      int incFollowupIdx = -1;
      int incGenoDataAvailIdx = -1;

      int includeIdx = -1;
      int scoreIdx = -1;

      int rs4986774idx = -1;
      int rs1065852idx = -1;
      int rs3892097idx = -1;
      int star5idx = -1;
      int rs5030655idx = -1;
      int rs16947idx = -1;
      int rs28371706idx = -1;
      int rs28371725idx = -1;

      int amplichipidx = -1;

      int fluoxetineCol = -1;
      int paroxetineCol = -1;
      int quinidienCol = -1;
      int buproprionCol = -1;
      int duloxetineCol = -1;
      int cimetidineCol = -1;
      int sertralineCol = -1;
      int citalopramCol = -1;

      idx = 0;
      for (String header : headers) {
        if (StringUtils.isNotEmpty(header)) {
          header = header.trim().toLowerCase();
        }
        if (header.contains("subject id")) {
          subjectId = idx;
        } else if (header.contains("project site")) {
          projectSiteIdx = idx;
        } else if (header.contains("age at diagnosis")) {
          ageIdx = idx;
        } else if (header.contains("metastatic disease")) {
          metastaticIdx = idx;
        } else if (header.contains("menopause status at diagnosis")) {
          menoStatusIdx = idx;
        } else if (header.equals("estrogen receptor (existing column)")) {
          erStatusIdx = idx;
        } else if (header.contains("intended tamoxifen duration")) {
          durationIdx = idx;
        } else if (header.contains("intended tamoxifen dose")) {
          tamoxDoseIdx = idx;
        } else if (header.contains("if tumor or tissue was dna source")) {
          tumorSourceIdx = idx;
        } else if (header.contains("blood or buccal cells")) {
          bloodSourceIdx = idx;
        } else if (header.contains("prior history of cancer")) {
          priorHistoryIdx = idx;
        } else if (header.contains("prior invasive breast cancer or dcis")) {
          priorDcisIdx = idx;
        } else if (header.contains("chemotherapy (existing column)")) {
          chemoIdx = idx;
        } else if (header.contains("additional hormone or other treatment after breast surgery? (new column)")) {
          hormoneIdx = idx;
        } else if (header.contains("systemic therapy prior to surgery?")) {
          systemicTherIdx = idx;
        } else if (header.contains("annual physical exam after breast cancer surgery")) {
          followupIdx = idx;
        } else if (header.contains("time between definitive breast cancer surgery")) {
          timeBtwSurgTamoxIdx = idx;
        } else if (header.contains("first adjuvant endocrine therapy")) {
          firstAdjEndoTherIdx = idx;
        } else if (header.contains("project notes (existing column)")) {
          projectNotesIdx = idx;
        } else if (header.contains("rs4986774")) {
          if (!header.contains("source")) {
            rs4986774idx = idx;
          }
          else {
            genoSourceIdx1 = idx;
          }
        } else if (header.contains("rs1065852")) {
          if (!header.contains("source")) {
            rs1065852idx = idx;
          }
          else {
            genoSourceIdx2 = idx;
          }
        } else if (header.contains("rs3892097")) {
          if (!header.contains("source")) {
          rs3892097idx = idx;
          }
          else {
            genoSourceIdx3 = idx;
          }
        } else if (header.contains("rs5030655") && !header.contains("source")) {
          rs5030655idx = idx;
        } else if (header.contains("rs16947") && !header.contains("source")) {
          rs16947idx = idx;
        } else if (header.contains("rs28371706") && !header.contains("source")) {
          rs28371706idx = idx;
        } else if (header.contains("rs28371725") && !header.contains("source")) {
          rs28371725idx = idx;
        } else if (header.contains("cyp2d6 *5") && !header.contains("source")) {
          star5idx = idx;
        } else if (header.contains("fluoxetine")) {
          fluoxetineCol = idx;
        } else if (header.contains("paroxetine")) {
          paroxetineCol = idx;
        } else if (header.contains("quinidine")) {
          quinidienCol = idx;
        } else if (header.contains("buproprion")) {
          buproprionCol = idx;
        } else if (header.contains("duloxetine")) {
          duloxetineCol = idx;
        } else if (header.contains("cimetidine")) {
          cimetidineCol = idx;
        } else if (header.contains("sertraline")) {
          sertralineCol = idx;
        } else if(header.equals("citalopram (existing column)")) {
          citalopramCol = idx;
        } else if (header.contains("amplichip call")) {
          amplichipidx = idx;
        }
        // new columns to add to the end of the template
        //   the end of the template is considered the "Project Notes" column
        int startPgkbColsIdx = projectNotesIdx+1;
        allele1idx = startPgkbColsIdx;
        allele2idx = startPgkbColsIdx + 1;
        allele1finalIdx = startPgkbColsIdx + 2;
        allele2finalIdx = startPgkbColsIdx + 3;
        callCommentsIdx = startPgkbColsIdx + 4;
        genotypeIdx = startPgkbColsIdx + 5;
        weakIdx = startPgkbColsIdx + 6;
        potentIdx = startPgkbColsIdx + 7;
        scoreIdx = startPgkbColsIdx + 8;
        metabStatusIdx = startPgkbColsIdx + 9;

        incAgeIdx = startPgkbColsIdx + 10;
        incNonmetaIdx = startPgkbColsIdx + 11;
        incPriorHistIdx = startPgkbColsIdx + 12;
        incErPosIdx = startPgkbColsIdx + 13;
        incSysTherIdx = startPgkbColsIdx + 14;
        incAdjTamoxIdx = startPgkbColsIdx + 15;
        incDurationIdx = startPgkbColsIdx + 16;
        incTamoxDoseIdx = startPgkbColsIdx + 17;
        incChemoIdx = startPgkbColsIdx + 18;
        incHormoneIdx = startPgkbColsIdx + 19;
        incDnaCollectionIdx = startPgkbColsIdx + 20;
        incFollowupIdx = startPgkbColsIdx + 21;
        incGenoDataAvailIdx = startPgkbColsIdx + 22;

        includeIdx = startPgkbColsIdx + 23;

        idx++;
      }
      summaryColumns.add(subjectId);
      summaryColumns.add(projectSiteIdx);
      summaryColumns.add(ageIdx);
      summaryColumns.add(44);
      summaryColumns.add(menoStatusIdx);
      summaryColumns.add(29); // max. dimension
      summaryColumns.add(26); // # pos. nodes
      summaryColumns.add(28); // nottingham grade
      summaryColumns.add(erStatusIdx);
      summaryColumns.add(34); // progesterone receptor
      summaryColumns.add(40); // radiation therapy
      summaryColumns.add(43); // adj. tamox. treatment
      summaryColumns.add(50); // adj. aromatase treatement

      // make sure we have matching data
      idx = 1;
      while (inputIt.hasNext()) {
        List<String> row = inputIt.next();
        idx++;
        if (StringUtils.isEmpty(row.get(subjectId))) {
          //noinspection UnnecessaryContinue
          continue;
        }
      }

      // -------------------------------------------------------------------------------------------
      // xxx: setting output row headers

      Sheet outputSheet;
      if (filter == DataSet.ALL) {
        outputSheet = inputSheet;
      }
      else {
        outputSheet = inputWorkbook.createSheet("Filtered Data");
        POIUtils.copyRowTo(inputSheet.getRow(0), outputSheet);
      }

      Map<String, Integer> genophenoMap = new HashMap<String, Integer>();
      inputIt = new PoiWorksheetIterator(inputSheet);
      // output header labels for characteristic data
      List<String> headerRow = inputIt.next();
      if (outputSummaryWriter != null) {
        copyCharacterData(outputSummaryWriter, headerRow);
        outputSummaryWriter.write("CYP2D6 Allele 1,CYP2D6 Allele 2,");
        outputSummaryWriter.write("Inc 1,Inc 2a,Inc 2b,Inc 3,Inc 4,Inc 4a,Inc 4b,Inc 4c,Inc 5,Inc 6,Inc 7,Inc 8,Inc 9,");
        outputSummaryWriter.write("Include (w/o 4a),Include"+ IOUtils.LINE_SEPARATOR);
      }

      Row titleRow = outputSheet.getRow(0);
      writeCell(titleRow, allele1idx, "CYP2D6 Allele 1 (PharmGKB)");
      writeCell(titleRow, allele2idx, "CYP2D6 Allele 2 (PharmGKB)");
      writeCell(titleRow, allele1finalIdx, "CYP2D6 Allele 1 (Final)");
      writeCell(titleRow, allele2finalIdx, "CYP2D6 Allele 2 (Final)");
      writeCell(titleRow, callCommentsIdx, "Curator comments on calls");
      writeCell(titleRow, scoreIdx, "Drug and CYP2D6 Genotype Score");

      writeCell(titleRow, genotypeIdx, "Genotype (PharmGKB)");
      writeCell(titleRow, weakIdx, "Weak Drug (PharmGKB)");
      writeCell(titleRow, potentIdx, "Potent Drug (PharmGKB)");
      writeCell(titleRow, metabStatusIdx, "Metabolizer Status (PharmGKB)");

      writeCell(titleRow, incAgeIdx, "Inc 1\nPostmenopausal");
      writeCell(titleRow, incNonmetaIdx, "Inc 2a\nNon-metastatic invasive cancer");
      writeCell(titleRow, incPriorHistIdx, "Inc 2b\nNo prior history of contralateral breast cancer");
      writeCell(titleRow, incErPosIdx, "Inc 3\nER Positive");
      writeCell(titleRow, incSysTherIdx, "Inc 4\nSystemic therapy prior to surgery");
      writeCell(titleRow, incAdjTamoxIdx, "Inc 4a\nAdjuvant tamoxifen initiated within 6 months");
      writeCell(titleRow, incDurationIdx, "Inc 4b\nTamoxifen duration intended 5 years");
      writeCell(titleRow, incTamoxDoseIdx, "Inc 4c\nTamoxifen dose intended 20mg/day");
      writeCell(titleRow, incChemoIdx, "Inc 5\nNo adjuvant chemotherapy");
      writeCell(titleRow, incHormoneIdx, "Inc 6\nNo additional adjuvant hormonal therapy");
      writeCell(titleRow, incDnaCollectionIdx, "Inc 7\nTiming of DNA Collection");
      writeCell(titleRow, incFollowupIdx, "Inc 8\nAdequate follow-up");
      writeCell(titleRow, incGenoDataAvailIdx, "Inc 9\nCYP2D6 genotype data available for assessment of *3, *4, *10, and *41");

      writeCell(titleRow, includeIdx, "Include");

      CellStyle style = getStyle(inputWorkbook);
      for (int i = allele1idx; i<=includeIdx; i++) {
        titleRow.getCell(i).setCellStyle(style);
      }
      CellStyle scoreStyle = inputWorkbook.createCellStyle();
      DataFormat scoreFormat = inputWorkbook.createDataFormat();
      style.setDataFormat(scoreFormat.getFormat("0.0"));


      idx = 1;
      int newRowNum = 0;
      inputIt.next();  //skip legend row
      while (inputIt.hasNext()) {  // xxx: data row iterator loop
        idx++;
        List<String> row = inputIt.next();
        String deleted = "1";

        if (StringUtils.isEmpty(row.get(subjectId))
            || (deleted.trim().equals("1") && filter==DataSet.INCLUDED)
            || (deleted.trim().equals("0") && filter==DataSet.EXCLUDED)) {
          continue;
        } else {
          newRowNum++;
        }

        Integer projectSite = Integer.valueOf(row.get(projectSiteIdx));
        iterateMappedTotals(siteTotalMap, projectSite);

        // patient info section
        String age = row.get(ageIdx);
        String menoStatus = row.get(menoStatusIdx);
        String metastatic = row.get(metastaticIdx);
        String erStatus = row.get(erStatusIdx);
        String duration = row.get(durationIdx);
        String tamoxDose = row.get(tamoxDoseIdx);
        String tumorSource = row.get(tumorSourceIdx);
        String bloodSource = row.get(bloodSourceIdx);
        String priorHistory = row.get(priorHistoryIdx);
        String priorDcis = row.get(priorDcisIdx);
        String chemotherapy = row.get(chemoIdx);
        String hormoneTherapy = row.get(hormoneIdx);
        String systemicTherapy = row.get(systemicTherIdx);
        String followup = row.get(followupIdx);
        String timeBtwSurgTamox = row.get(timeBtwSurgTamoxIdx);
        String firstAdjEndoTher = row.get(firstAdjEndoTherIdx);
        String genoSource = null;

        if (!StringUtils.isBlank(row.get(genoSourceIdx1)) && !row.get(genoSourceIdx1).equals("NA")) {
          genoSource = row.get(genoSourceIdx1);
        }
        else if (!StringUtils.isBlank(row.get(genoSourceIdx2)) && !row.get(genoSourceIdx2).equals("NA")) {
          genoSource = row.get(genoSourceIdx2);
        }
        else if (!StringUtils.isBlank(row.get(genoSourceIdx3)) && !row.get(genoSourceIdx3).equals("NA")) {
          genoSource = row.get(genoSourceIdx3);
        }

        boolean homoStar5 = false;
        boolean validStar5 = false;

        String star5 = cleanString(row.get(star5idx));
        VariantAlleles rs1065852 = new VariantAlleles(row.get(rs1065852idx));
        VariantAlleles rs4986774 = new VariantAlleles(row.get(rs4986774idx));
        VariantAlleles rs3892097 = new VariantAlleles(row.get(rs3892097idx));
        VariantAlleles rs5030655 = new VariantAlleles(row.get(rs5030655idx));
        VariantAlleles rs16947 = new VariantAlleles(row.get(rs16947idx));
        VariantAlleles rs28371706 = new VariantAlleles(row.get(rs28371706idx));
        VariantAlleles rs28371725 = new VariantAlleles(row.get(rs28371725idx));
        List<String> alleleCallsAmplichip = processAmplichip(row.get(amplichipidx), true);

        if (row.get(amplichipidx) != null && !StringUtils.isBlank(row.get(amplichipidx)) && !row.get(amplichipidx).equals("NA") && alleleCallsAmplichip.size()!=2) {
          sf_logger.error("Error on row: " + (idx+1) + " > malformed Amplichip call [" + row.get(amplichipidx) + "]");
        }

        List<String> alleleCallsPgkb = new ArrayList<String>();

        // xxx: start star call logic
        // *5
        if (star5 != null) {
          if (star5.contains("deletion") && !star5.contains("no deletion")) {
            addAllele(alleleCallsPgkb,"*5");
          }
          if (star5.equals("homozygous deletion")) {
            addAllele(alleleCallsPgkb,"*5");
            homoStar5 = true;
          }
          validStar5 = true; // any non-null value in the star5 column besides NA is considered valid
        }
        // sometimes groups don't make *5 evidence available. they say it's not available but if there is a
        // heterozygote in one of the variant columns we know it must have been able to read two genes, therefore
        // *5 was detectable and not present
        else if (rs4986774.is("-","a")
              || rs1065852.is("c","t")
              || rs3892097.is("a","g")
              || rs5030655.is("-","t")
              || rs16947.is("c","t")
              || rs28371706.is("c","t")
              || rs28371725.is("g","a")) {
            validStar5 = true;
        }

        if (!homoStar5) {
          // *3
          if (rs4986774.hasData()) {
            if (alleleCallsPgkb.contains("*5") && rs4986774.baseCount("-")==2) {
              addAllele(alleleCallsPgkb,"*3");
            }
            else if (!alleleCallsPgkb.contains("*5")) {
              for (int i=0; i<rs4986774.baseCount("-"); i++) {
                addAllele(alleleCallsPgkb,"*3");
              }
            }
          }

          // *6
          if (rs5030655.hasData()) {
            if (alleleCallsPgkb.contains("*5") && rs5030655.baseCount("-")==2) {
              addAllele(alleleCallsPgkb,"*6");
            }
            else if (!alleleCallsPgkb.contains("*5")) {
              for (int i=0; i<rs5030655.baseCount("-"); i++) {
                addAllele(alleleCallsPgkb,"*6");
              }
            }
          }

          // *4
          if (rs3892097.hasData()) {
            if (rs3892097.contains("a")) {
              addAllele(alleleCallsPgkb,"*4");
            }
            if (rs3892097.baseCount("a")==2 && !alleleCallsPgkb.contains("*5")) {
              addAllele(alleleCallsPgkb,"*4");
            }
          }

          // *41
          if (rs28371725.hasData()) {
            if (rs28371725.contains("a")) {
              addAllele(alleleCallsPgkb,"*41");
            }
            if (rs28371725.baseCount("a")==2 && validStar5 && !alleleCallsPgkb.contains("*5")) {
              addAllele(alleleCallsPgkb,"*41");
            }
          }

          //       *4                    *3                   *6 w/ exception for a haplotype rule for *10
          if (rs3892097.hasData() && rs4986774.hasData() && (rs5030655.hasData() || (rs1065852.is("t","c") && !rs5030655.hasData())) && validStar5) {

            // *2
            if (rs16947.hasData() && rs28371706.baseCount("c")>0 && rs28371725.baseCount("g")>0 && rs1065852.hasData()) {
              if (rs16947.baseCount("t")>0 && rs28371706.baseCount("t")<2 && rs28371725.baseCount("a")<2) {
                addAllele(alleleCallsPgkb, "*2");
                if (!alleleCallsPgkb.contains("*5") &&
                    (rs16947.is("t","t") && rs28371706.is("c","c") && rs28371725.is("g","g"))) {
                  addAllele(alleleCallsPgkb, "*2");
                }
              }
            }

            // *10
            if (rs1065852.hasData()) {
              if (rs1065852.contains("t") && rs3892097.baseCount("a")==0) {
                addAllele(alleleCallsPgkb,"*10");
              }
              if (!alleleCallsPgkb.contains("*5") && rs1065852.is("t","t") && rs3892097.contains("g")) {
                addAllele(alleleCallsPgkb,"*10");
              }
            }

            // *17
            if (rs28371706.hasData() && rs16947.hasData()) {
              if (rs28371706.contains("t") && rs16947.contains("t")) {
                addAllele(alleleCallsPgkb,"*17");
              }
              if (rs28371706.baseCount("t")==2 && rs16947.baseCount("t")==2
                  && validStar5 && !alleleCallsPgkb.contains("*5")) {
                addAllele(alleleCallsPgkb,"*17");
              }
            }
          }
          else if (!validStar5) { //what to do if we don't have complete *5 knowledge
            if (rs4986774.hasData() &&
                rs1065852.hasData() &&
                rs3892097.hasData() &&
                rs5030655.hasData() &&
                rs16947.is("t","t")   &&
                rs28371725.is("g","g") &&
                rs28371706.is("c","c")) {
              addAllele(alleleCallsPgkb, "*2");
            }
            else if (rs4986774.hasData() &&
                rs1065852.hasData() &&
                rs3892097.hasData() &&
                rs5030655.hasData() &&
                rs16947.hasData()   &&
                rs28371725.hasData() &&
                rs28371706.hasData() &&
                alleleCallsPgkb.size()==0) {
              addAllele(alleleCallsPgkb, "*1");
              addAllele(alleleCallsPgkb, "Unknown");
            }
          }
        }

        if (rs4986774.hasData() && // special case for partial unknown info., from Joan
            rs1065852.hasData() &&
            rs3892097.hasData() &&
            rs5030655.hasData() &&
            rs16947.is("c","t") &&
            (!rs28371706.hasData() || !rs28371725.hasData()) &&
            validStar5 &&
            alleleCallsPgkb.isEmpty()) {
          addAllele(alleleCallsPgkb,"*1");
          addAllele(alleleCallsPgkb,"Unknown");
        }


        while (alleleCallsPgkb.size() < 2) {
          if (rs4986774.hasData() &&
              rs1065852.hasData() &&
              rs3892097.hasData() &&
              rs5030655.hasData() &&
              rs16947.hasData()   &&
              validStar5 &&
              (rs28371725.hasData() || (!rs28371725.hasData() && (rs16947.is("C","C") || rs16947.is("C","-")))) &&
              (rs28371706.hasData() || (!rs28371706.hasData() && (rs16947.is("C","C") || rs16947.is("C","-")))) &&
              (!alleleCallsPgkb.contains("*5") || (alleleCallsPgkb.contains("*5") && (rs28371725.hasData() || rs28371706.hasData())))) // we can use rs16947 to exclude *41 calls so it doesn't always have to be available
          {
            addAllele(alleleCallsPgkb,"*1");
          }
          else {
            alleleCallsPgkb.add("Unknown");
          }
        }

        // output for characteristic data
        copyCharacterData(outputSummaryWriter, row);

        List<String> genotypes = new ArrayList<String>();

        Row newRow;
        if (filter != DataSet.ALL) {
          newRow = POIUtils.copyRowTo(inputSheet.getRow(idx),outputSheet,newRowNum);
        }
        else {
          newRow = outputSheet.getRow(idx);
        }

        // print PharmGKB allele calls
        Collections.sort(alleleCallsPgkb, String.CASE_INSENSITIVE_ORDER);
        writeCell(newRow, allele1idx, alleleCallsPgkb.get(0));
        writeCell(newRow, allele2idx, alleleCallsPgkb.get(1));

        List<String> alleleCallsFinal = new ArrayList<String>();
        // print final allele calls, PharmGKB or Amplichip
        if (alleleCallsAmplichip != null && alleleCallsAmplichip.size()==2) {
          alleleCallsFinal.addAll(alleleCallsAmplichip);
        }
        else {
          alleleCallsFinal.addAll(alleleCallsPgkb);
        }
        writeCell(newRow, allele1finalIdx, alleleCallsFinal.get(0));
        writeCell(newRow, allele2finalIdx, alleleCallsFinal.get(1));

        if (newRow.getCell(callCommentsIdx) != null) {
          writeCell(newRow, callCommentsIdx, newRow.getCell(callCommentsIdx).getStringCellValue());
        }

        if (!alleleCallsFinal.contains("*4")) {
          iterateMappedTotals(nonStar4CountMap, projectSite);
        }

        String isPotent;
        if ((row.get(paroxetineCol) != null && row.get(paroxetineCol).equals("1")) ||
            (row.get(fluoxetineCol) != null && row.get(fluoxetineCol).equals("1")) ||
            (row.get(quinidienCol)  != null && row.get(quinidienCol).equals("1")) ||
            (row.get(buproprionCol) != null && row.get(buproprionCol).equals("1")) ||
            (row.get(duloxetineCol) != null && row.get(duloxetineCol).equals("1"))) {
          isPotent = "True";
        }
        else if ((row.get(paroxetineCol) != null && row.get(paroxetineCol).equals("0")) &&
            (row.get(fluoxetineCol) != null && row.get(fluoxetineCol).equals("0")) &&
            (row.get(quinidienCol)  != null && row.get(quinidienCol).equals("0")) &&
            (row.get(buproprionCol) != null && row.get(buproprionCol).equals("0")) &&
            (row.get(duloxetineCol) != null && row.get(duloxetineCol).equals("0"))) {
          isPotent = "False";
        }
        else {
          isPotent="Unknown";
        }

        String isWeak;
        if ((row.get(cimetidineCol) != null && row.get(cimetidineCol).equals("1"))
            || (row.get(sertralineCol) != null && row.get(sertralineCol).equals("1"))
            || (row.get(citalopramCol) != null && row.get(citalopramCol).equals("1"))
            ) {
          isWeak = "True";
        }
        else if ((row.get(cimetidineCol) != null && row.get(cimetidineCol).equals("0"))
            && (row.get(sertralineCol) != null && row.get(sertralineCol).equals("0"))
            && (row.get(citalopramCol) != null && row.get(citalopramCol).equals("0"))
            ) {
          isWeak = "False";
        }
        else {
          isWeak = "Unknown";
        }

        // xxx: calculate a score
        float score = 0f;
        boolean incompleteScore = false;
        for (String a : alleleCallsFinal) {

          if (outputSummaryWriter != null) {
            outputSummaryWriter.write(a);
            outputSummaryWriter.write(",");
          }
          if (a.equals("Unknown") || !genoGroup.keySet().contains(a)) {
            genotypes.add("Unknown");
          }
          else {
            genotypes.add(genoGroup.get(a));
          }

          if (isPotent.equalsIgnoreCase("True")) {
            score = 0f;
          }
          else if (genoScore.containsKey(a) && !isPotent.equalsIgnoreCase("Unknown")) {
            score += genoScore.get(a);
          }
          else {
            incompleteScore = true;
          }
        }
        if (incompleteScore && !(genotypes.size()==2 && genotypes.get(0).equals("PM") && genotypes.get(1).equals("PM"))) {
          writeCell(newRow, scoreIdx, "Unknown");
        }
        else {
          if (isWeak.equalsIgnoreCase("true")) {
            score -= 0.5f;
          }
          if (score<0.0f) {
            score = 0.0f;
          }
          writeCell(newRow, scoreIdx, score);
          newRow.getCell(scoreIdx).setCellStyle(scoreStyle);

        }

        if (rs4986774.isUncertain() ||
            rs1065852.isUncertain() ||
            rs3892097.isUncertain() ||
            rs5030655.isUncertain() ||
            rs16947.isUncertain() ||
            rs28371706.isUncertain() ||
            rs28371725.isUncertain()) {
          sf_logger.warn("row " + (idx+1) + ": uncertain variant");
        }

        StringBuilder genoBuilder = new StringBuilder();
        Collections.sort(genotypes, String.CASE_INSENSITIVE_ORDER);
        for (int x = 0; x < genotypes.size(); x++) {
          genoBuilder.append(genotypes.get(x));
          if (x != genotypes.size() - 1) {
            genoBuilder.append("/");
          }
        }
        writeCell(newRow, genotypeIdx, genoBuilder.toString());


        writeCell(newRow, weakIdx, isWeak);
        writeCell(newRow, potentIdx, isPotent);

        String key = genoBuilder.toString() + "," + isWeak + "," + isPotent;
        if (genophenoMap.containsKey(key)) {
          genophenoMap.put(key, genophenoMap.get(key) + 1);
        } else {
          genophenoMap.put(key, 1);
        }

        //xxx: Metabolizer status totals caclulation
        // these categories should match the lines in metabTable
        String metabolizerStatus = "Unclassified";
        String geno = genoBuilder.toString();

        if (isPotent.equals("True") && geno.contains("Unknown")) {
          metabStatusTotals[17]++; metabolizerStatus = "Poor";
        }

        else if (isPotent.equals("True")) {
          metabStatusTotals[18]++; metabolizerStatus = "Poor";
        }

        else if (geno.equals("UM/UM") && isWeak.equals("False")) {
          metabStatusTotals[0]++; metabolizerStatus = "Ultrarapid one";
        }

        else if ((geno.equals("UM/UM")) && isWeak.equals("True")) {
          metabStatusTotals[1]++; metabolizerStatus = "Ultrarapid two";
        }

        else if (geno.equals("EM/UM") && isWeak.equals("False")) {
          metabStatusTotals[2]++; metabolizerStatus = "Ultrarapid three";
        }

        else if (geno.equals("IM/UM") && isWeak.equals("False")) {
          metabStatusTotals[3]++; metabolizerStatus = "Extensive one";
        }

        else if (geno.equals("EM/UM") && isWeak.equals("True")) {
          metabStatusTotals[4]++; metabolizerStatus = "Extensive one";
        }

        else if (geno.equals("EM/EM") && isWeak.equals("False")) {
          metabStatusTotals[5]++; metabolizerStatus = "Extensive two";
        }

        else if (geno.equals("IM/UM") && isWeak.equals("True")) {
          metabStatusTotals[6]++; metabolizerStatus = "Extensive two";
        }

        else if (geno.equals("PM/UM") && isWeak.equals("False")) {
          metabStatusTotals[7]++; metabolizerStatus = "Extensive two";
        }

        else if ((geno.equals("IM/PM")) && isWeak.equals("True")) {
          metabStatusTotals[8]++; metabolizerStatus = "Intermediate one";
        }

        else if ((geno.equals("EM/IM")) && isWeak.equals("False")) {
          metabStatusTotals[9]++; metabolizerStatus = "Intermediate one";
        }

        else if ((geno.equals("EM/EM")) && isWeak.equals("True")) {
          metabStatusTotals[10]++; metabolizerStatus = "Intermediate one";
        }

        else if ((geno.equals("IM/IM")) && isWeak.equals("False")) {
          metabStatusTotals[11]++; metabolizerStatus = "Intermediate two";
        }

        else if ((geno.equals("EM/PM")) && isWeak.equals("False")) {
          metabStatusTotals[12]++; metabolizerStatus = "Intermediate two";
        }

        else if ((geno.equals("EM/IM")) && isWeak.equals("True")) {
          metabStatusTotals[13]++; metabolizerStatus = "Intermediate two";
        }

        else if ((geno.equals("EM/PM")) && isWeak.equals("True")) {
          metabStatusTotals[14]++; metabolizerStatus = "Intermediate three";
        }

        else if ((geno.equals("IM/IM")) && isWeak.equals("True")) {
          metabStatusTotals[15]++; metabolizerStatus = "Intermediate three";
        }

        else if ((geno.equals("IM/PM")) && isWeak.equals("False")) {
          metabStatusTotals[16]++; metabolizerStatus = "Intermediate three";
        }

        else if ((geno.equals("IM/PM")) && isWeak.equals("True")) {
          metabStatusTotals[19]++; metabolizerStatus = "Poor";
        }

        else if ((geno.equals("PM/PM")) && isWeak.equals("True")) {
          metabStatusTotals[20]++; metabolizerStatus = "Poor";
        }

        else if ((geno.equals("PM/PM")) && isWeak.equals("False")) {
          metabStatusTotals[21]++; metabolizerStatus = "Poor";
        }

        else if (geno.equals("PM/PM") && isWeak.equals("Unknown") && isPotent.equals("Unknown")) {
          metabStatusTotals[22]++; metabolizerStatus = "Poor";
        }

        else if (isWeak.equals("Unknown") && isPotent.equals("Unknown")) {
          metabStatusTotals[23]++; metabolizerStatus = "Unclassified";
        }

        else if (!(geno.contains("Unknown") && isWeak.equals("False") && isPotent.equals("False"))) {
          sf_logger.warn("No metab. status for: " + row.get(subjectId) + " :: " + geno + " :: " + isWeak + "/" + isPotent);
          metabStatusTotals[24]++;
        }

        else {
          sf_logger.warn("No matching logic for: " + row.get(subjectId) + " :: " + geno + " :: " + isWeak + "/" + isPotent);
          metabStatusTotals[24]++;
        }

        writeCell(newRow, metabStatusIdx, metabolizerStatus);

        //xxx: inclusion criteria start
        boolean include = true;
        boolean inclusion1 = true;
        boolean inclusion2a = true;
        boolean inclusion2b = true;
        boolean inclusion3 = true;
        boolean inclusion4 = true;
        boolean inclusion4a = true;
        boolean inclusion4b = true;
        boolean inclusion4c = true;
        boolean inclusion5 = true;
        boolean inclusion6 = true;
        boolean inclusion7 = true;
        boolean inclusion8 = true;
        boolean inclusion9 = true;

        // inclusion 1
        if (!isBlank(menoStatus)) {
          if (menoStatus.equals("2")) {
            writeCell(newRow, incAgeIdx, "Y");
          }
          else {
            writeCell(newRow, incAgeIdx, "N");
            include = false;
            inclusion1 = false;
          }
        }
        else {
          try {
            Float ageFloat = Float.parseFloat(age);
            if (ageFloat>=50f) {
              writeCell(newRow, incAgeIdx, "Y");
            }
            else {
              writeCell(newRow, incAgeIdx, "N");

              include = false;
              inclusion1 = false;
            }
          } catch (NumberFormatException ex) {
            writeCell(newRow, incAgeIdx, "NA");

            include = false;
            inclusion1 = false;
          }
        }

        // inclusion 2a
        if (metastatic != null && metastatic.equals("0")) {
          writeCell(newRow, incNonmetaIdx, "Y");
        }
        else if (metastatic != null && metastatic.equals("1")) {
          writeCell(newRow, incNonmetaIdx, "N");

          include = false;
          inclusion2a = false;
        }
        else {
          writeCell(newRow, incNonmetaIdx, "NA");

          include = false;
          inclusion2a = false;
        }

        // inclusion 2b
        if ((priorHistory == null || priorHistory.equals("0"))
            && (priorDcis == null || !priorDcis.equals("1"))) {
          writeCell(newRow, incPriorHistIdx, "Y");
        }
        else {
          writeCell(newRow, incPriorHistIdx, "N");

          include = false;
          inclusion2b = false;
        }

        // inclusion 3
        if (erStatus != null && erStatus.equals("1")) {
          writeCell(newRow, incErPosIdx, "Y");
        }
        else {
          writeCell(newRow, incErPosIdx, "N");

          include = false;
          inclusion3 = false;
        }

        // inclusion 4
        if (systemicTherapy.equals("2")) {
          writeCell(newRow, incSysTherIdx, "Y");
        }
        else {
          writeCell(newRow, incSysTherIdx, "N");
          include = false;
          inclusion4 = false;
        }

        // inclusion 4a
        try {
          Integer daysBetween = Integer.parseInt(timeBtwSurgTamox);
          if (daysBetween<182 && firstAdjEndoTher.equals("1")) {
            writeCell(newRow, incAdjTamoxIdx, "Y");
          }
          else {
            writeCell(newRow, incAdjTamoxIdx, "N");
            include = false;
            inclusion4a = false;
          }
        }
        catch (NumberFormatException ex) {
          if (firstAdjEndoTher.equals("1") && (timeBtwSurgTamox.equalsIgnoreCase("< 6 weeks") || timeBtwSurgTamox.equalsIgnoreCase("28-42"))) {
            writeCell(newRow, incAdjTamoxIdx, "Y");
          }
          else {
            writeCell(newRow, incAdjTamoxIdx, "N");
            include = false;
            inclusion4a = false;
          }
        }

        // inclusion 4b
        if (duration != null && duration.equals("0")) {
          writeCell(newRow, incDurationIdx, "Y");
        }
        else {
          writeCell(newRow, incDurationIdx, "N");
          include = false;
          inclusion4b = false;
        }

        //inclusion 4c
        if (tamoxDose != null && tamoxDose.equals("0")) {
          writeCell(newRow, incTamoxDoseIdx, "Y");
        }
        else {
          writeCell(newRow, incTamoxDoseIdx, "N");
          include = false;
          inclusion4c = false;
        }

        // inclusion 5
        if (chemotherapy == null || !chemotherapy.equals("1")) {
          writeCell(newRow, incChemoIdx, "Y");
        }
        else {
          writeCell(newRow, incChemoIdx, "N");
          include = false;
          inclusion5 = false;
        }

        // inclusion 6
        if (hormoneTherapy == null || !hormoneTherapy.equals("1")) {
          writeCell(newRow, incHormoneIdx, "Y");
        }
        else {
          writeCell(newRow, incHormoneIdx, "N");
          include = false;
          inclusion6 = false;
        }

        // inclusion 7
        if (genoSource != null && (
            ((genoSource.equals("0") || genoSource.equals("3") || genoSource.equals("4")) && tumorSource.equals("1"))
            || ((genoSource.equals("1") || genoSource.equals("2")) && (bloodSource.equals("1") || bloodSource.equals("2") || bloodSource.equals("7")))
            )
            ) {
          writeCell(newRow, incDnaCollectionIdx, "Y");
        }
        else {
          writeCell(newRow, incDnaCollectionIdx, "N");
          include = false;
          inclusion7 = false;
        }

        // inclusion 8
        if (followup == null || !followup.equals("2")) {
          writeCell(newRow, incFollowupIdx, "Y");
        }
        else {
          writeCell(newRow, incFollowupIdx, "N");
          include = false;
          inclusion8 = false;
        }

        // inclusion 9
        if (!alleleCallsFinal.contains("Unknown")) {
          writeCell(newRow, incGenoDataAvailIdx, "Y");
        }
        else {
          writeCell(newRow, incGenoDataAvailIdx, "N");
          include = false;
          inclusion9 = false;
        }

        if (include) {
          writeCell(newRow, includeIdx, "Y");
        } else {
          writeCell(newRow, includeIdx, "N");
        }

        for (int i = allele1idx; i<=includeIdx; i++) {
          if (newRow.getCell(i) != null) {
            newRow.getCell(i).setCellStyle(style);
          }
        }

        if (outputSummaryWriter != null) {
          boolean includeMinus4a = inclusion1 && inclusion2a && inclusion2b && inclusion3 && inclusion4
              && inclusion4b && inclusion4c && inclusion5 && inclusion6 && inclusion7 && inclusion8 && inclusion9;

          writeBooleanColumn(outputSummaryWriter, inclusion1);
          writeBooleanColumn(outputSummaryWriter, inclusion2a);
          writeBooleanColumn(outputSummaryWriter, inclusion2b);
          writeBooleanColumn(outputSummaryWriter, inclusion3);
          writeBooleanColumn(outputSummaryWriter, inclusion4);
          writeBooleanColumn(outputSummaryWriter, inclusion4a);
          writeBooleanColumn(outputSummaryWriter, inclusion4b);
          writeBooleanColumn(outputSummaryWriter, inclusion4c);
          writeBooleanColumn(outputSummaryWriter, inclusion5);
          writeBooleanColumn(outputSummaryWriter, inclusion6);
          writeBooleanColumn(outputSummaryWriter, inclusion7);
          writeBooleanColumn(outputSummaryWriter, inclusion8);
          writeBooleanColumn(outputSummaryWriter, inclusion9);

          writeBooleanColumn(outputSummaryWriter, includeMinus4a);
          writeBooleanColumn(outputSummaryWriter, include);

          outputSummaryWriter.write(IOUtils.LINE_SEPARATOR);
        }
      }

      //xxx: make summary sheets
      // -------------------------------------------------------------------------------------------

      Sheet genoSheet = getNewSheet("Genotype Summary", inputWorkbook);

      Row row = genoSheet.createRow(0);
      row.createCell(0).setCellValue("Genotype");
      row.createCell(1).setCellValue("Weak");
      row.createCell(2).setCellValue("Potent");
      row.createCell(3).setCellValue("Count");
      int rowNum = 1;
      for (String key : genophenoMap.keySet()) {
        row = genoSheet.createRow(rowNum);
        String[] tokens = key.split(",");
        if (tokens.length == 3) {
          row.createCell(0).setCellValue(tokens[0]);
          row.createCell(1).setCellValue(tokens[1]);
          row.createCell(2).setCellValue(tokens[2]);
          row.createCell(3).setCellValue(genophenoMap.get(key));
        }
        rowNum++;
      }

      // -------------------------------------------------------------------------------------------
      // Metab Status worksheet

      Sheet metabSheet = getNewSheet("Metab Status", inputWorkbook);

      row = metabSheet.createRow(0);
      row.createCell(0).setCellValue("Final CYP2D6 metabolizer status and score");
      row.getCell(0).setCellStyle(style);
      row.createCell(1).setCellValue("Genotype and genotype score");
      row.getCell(1).setCellStyle(style);
      row.createCell(2).setCellValue("Weak CYP2D6 inhibitor administered");
      row.getCell(2).setCellStyle(style);
      row.createCell(3).setCellValue("Potent CYP2D6 inhibitor administered");
      row.getCell(3).setCellStyle(style);
      row.createCell(4).setCellValue("n");
      row.getCell(4).setCellStyle(style);

      for (int i=0; i<metabStatusTotals.length; i++) {
        row = metabSheet.createRow(i+1);
        String[] tokens = metabTable[i].split("\t");
        for (int j=0; j<tokens.length; j++) {
          row.createCell(j).setCellValue(tokens[j]);
        }
        row.createCell(4).setCellValue(metabStatusTotals[i]);
      }

      // -------------------------------------------------------------------------------------------

      Sheet alleleSheet = getNewSheet("Non-4 Alleles", inputWorkbook);

      row = alleleSheet.createRow((short)0);
      row.createCell(0).setCellValue("Non-*4 data available by site:");
      row = alleleSheet.createRow((short)1);
      row.createCell(0).setCellValue("Project Site");
      row.createCell(1).setCellValue("non-*4");
      row.createCell(2).setCellValue("total");

      int allTotal = 0;
      int allNonStar4 = 0;
      rowNum = 2;
      for (Integer site : siteTotalMap.keySet()) {
        int siteNon4Total = 0;
        if (nonStar4CountMap.get(site) != null) {
          siteNon4Total = nonStar4CountMap.get(site);
        }

        row = alleleSheet.createRow(rowNum);
        row.createCell(0).setCellValue(site);
        row.createCell(1).setCellValue(siteNon4Total);
        row.createCell(2).setCellValue(siteTotalMap.get(site));
        allTotal += siteTotalMap.get(site);
        allNonStar4 += siteNon4Total;
        rowNum++;
      }
      row = alleleSheet.createRow(rowNum);
      row.createCell(0).setCellValue("Total");
      row.createCell(1).setCellValue(allNonStar4);
      row.createCell(2).setCellValue(allTotal);

      FileOutputStream statsOut = new FileOutputStream(outputFile);
      inputWorkbook.write(statsOut);
      IOUtils.closeQuietly(statsOut);

    } catch (Exception ex) {
      sf_logger.error("Error on row: " + (idx+1), ex);
    } finally {
      IOUtils.closeQuietly(inputFileStream);
      IOUtils.closeQuietly(outputSummaryWriter);
    }
  }

  public void parseInput(String[] args) throws Exception {
    CliHelper cli = new CliHelper(getClass(), false);
    cli.addOption("f", "file", "ITPC excel file to read", "pathToFile");
    cli.addOption("ds", "dataSet", "Data set to generate: all, included, excluded", "dataSet");

    try {
      cli.parse(args);
      if (cli.isHelpRequested()) {
        cli.printHelp();
        System.exit(1);
      }
    }
    catch (Exception ex) {
      throw new Exception("Errror parsing params", ex);
    }

    if (cli.hasOption("-f")) {
      File fileInput = new File(cli.getValue("-f"));
      if (fileInput.exists()) {
        this.setFileInput(fileInput);
      }
      else {
        throw new Exception("File doesn't exist: " + fileInput);
      }
    }

    if (cli.hasOption("-ds")) {
      String dataset = cli.getValue("-ds");
      if (dataset.equalsIgnoreCase("all")) {
        setDataSet(DataSet.ALL);
      }
      else if (dataset.equalsIgnoreCase("included")) {
        setDataSet(DataSet.INCLUDED);
      }
      else if (dataset.equalsIgnoreCase("excluded")) {
        setDataSet(DataSet.EXCLUDED);
      }
    }
  }

  public File getFileInput() {
    return m_fileInput;
  }

  public void setFileInput(File fileInput) {
    m_fileInput = fileInput;
  }

  /**
   * Takes an input String, splits it on "/"s, reorders it alphabetically, and puts it back together.
   * This also nulls out Strings that are Blank, have "na", or contain the word "or"
   * @param string a String
   * @return a reformatted String
   */
  private static String cleanString(String string) {

    if (StringUtils.isBlank(string) || string.trim().equalsIgnoreCase("na") || string.trim().contains(" or ")) {
      return null;
    }
    string = string.trim().toLowerCase();
    String[] data = string.split("/");
    Arrays.sort(data, String.CASE_INSENSITIVE_ORDER);
    StringBuilder strBuilder = new StringBuilder();
    for (String s : data) {
      strBuilder.append(s);
    }
    return strBuilder.toString();
  }

  /**
   * Adds an allele to the List of alleles, taking into consideration the prioritization found
   * in the genoPriority Map for this class
   * @param alleles a String List of found alleles
   * @param allele a new String allele to add to the List
   */
  private static void addAllele(List<String> alleles, String allele) {
    if (!genoGroup.keySet().contains(allele)) {
      return;
    }

    if (alleles.isEmpty() || alleles.size()==1) {
      alleles.add(allele);
    }
    else {
      String removeAllele = null;
      for (String existingAllele : alleles) {
        // if the new allele has a higher priority (lower number) than an existing one, replace it
        if ((genoPriority.get(genoGroup.get(allele)) < genoPriority.get(genoGroup.get(existingAllele)))
            || (existingAllele.equals("*1") && genoPriority.get(existingAllele).equals(genoPriority.get(allele)))) {
          removeAllele = existingAllele;
        }
      }
      if (!StringUtils.isBlank(removeAllele)) {
        alleles.remove(removeAllele);
        alleles.add(allele);
      }
    }
  }

  private static List<String> processAmplichip(String amplichip, boolean stripLetters) throws Exception {
    List<String> alleles = new ArrayList<String>();

    if (amplichip != null && amplichip.contains("/")) {
      String[] tokens = amplichip.split("/");
      for (String token : tokens) {
        if (stripLetters) {
          try {
            alleles.add(alleleStrip(token));
          }
          catch (Exception ex) {
            throw new Exception("Error processing amplichip: " + amplichip, ex);
          }
        }
        else {
          alleles.add(token);
        }
      }
    }

    return alleles;
  }

  private static String alleleStrip(String allele) throws Exception {
    String alleleClean;

    Matcher m = sf_alleleRegex.matcher(allele);
    if (m.find()) {
      alleleClean = allele.substring(m.start(),m.end());
      if (allele.toLowerCase().contains("xn")) {
        alleleClean += "XN";
      }
    }
    else {
      throw new Exception("Malformed allele: " + allele);
    }

    return alleleClean;
  }

  /**
   * Iterate a running total in the value field of totalMap for a given key
   * @param totalMap a Map of keys to running totals
   * @param key the key Integer to iterate
   */
  private static void iterateMappedTotals(Map<Integer,Integer> totalMap, Integer key) {
    if (!totalMap.keySet().contains(key)) {
      totalMap.put(key,1);
    }
    else {
      totalMap.put(key, totalMap.get(key)+1);
    }
  }

  /**
   * Uses the summaryColumns list to cherry-pick the columns needed for compiling the patient
   * characteristics sheet
   * @param output the FileWriter to output to
   * @param row the List of Strings containing the data
   * @throws IOException can be thrown when writing to output
   */
  private static void copyCharacterData(FileWriter output, List<String> row) throws IOException {
    if (output != null) {
      StringBuffer summaryLine = new StringBuffer();
      for (Integer columnIdx : summaryColumns) {
        if (columnIdx == 44 && row.get(columnIdx) != null && !row.get(columnIdx).contains("Number of Months")) {
          if (stringIsNumber(row.get(columnIdx))) {
            summaryLine.append("\"").append(row.get(columnIdx)).append("\"");
          }
          summaryLine.append(",");
        }
        else {
          summaryLine.append("\"").append(row.get(columnIdx)).append("\"");
          summaryLine.append(",");
        }
      }
      output.write(summaryLine.toString());
    }
  }

  private static String getDate() {
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmm");
    return sdf.format(date);
  }

  public DataSet getDataSet() {
    return m_dataSet;
  }

  public void setDataSet(DataSet dataSet) {
    m_dataSet = dataSet;
  }

  protected CellStyle getStyle(Workbook wb) {
    CellStyle style = wb.createCellStyle();

    style.setBorderBottom(CellStyle.BORDER_THIN);
    style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
    style.setBorderLeft(CellStyle.BORDER_THIN);
    style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
    style.setBorderTop(CellStyle.BORDER_THIN);
    style.setTopBorderColor(IndexedColors.BLACK.getIndex());
    style.setBorderRight(CellStyle.BORDER_THIN);
    style.setRightBorderColor(IndexedColors.BLACK.getIndex());

    style.setAlignment(CellStyle.ALIGN_CENTER);
    style.setWrapText(true);

    return style;
  }

  protected boolean isBlank(String string) {
    return
        string == null
        || StringUtils.isBlank(string)
        || string.equalsIgnoreCase("na");
  }

  protected void writeCell(Row row, int idx, String value) {
    Cell cell = row.getCell(idx);
    if (cell == null) {
      row.createCell(idx).setCellValue(value);
    }
    else {
      if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
        if (!value.equals(cell.getStringCellValue())) {
          StringBuilder sb = new StringBuilder();
          sb.append("Changed value: ")
              .append(CellReference.convertNumToColString(cell.getColumnIndex()))
              .append(cell.getRowIndex()+1)
              .append(" = ")
              .append(cell.getStringCellValue())
              .append(" -> ")
              .append(value);
          sf_logger.info(sb.toString());
        }
      }
      else {
        Double existingValue = cell.getNumericCellValue();

        StringBuilder sb = new StringBuilder();
        sb.append("Changed value: ")
            .append(CellReference.convertNumToColString(cell.getColumnIndex()+1))
            .append(cell.getRowIndex()+1)
            .append(" = ")
            .append(existingValue)
            .append(" -> ")
            .append(value);
        sf_logger.info(sb.toString());

        row.removeCell(cell);
        row.createCell(idx).setCellType(Cell.CELL_TYPE_STRING);
      }
      cell.setCellValue(value);
    }
  }

  protected void writeCell(Row row, int idx, float value) {
    Cell cell = row.getCell(idx);
    if (cell == null) {
      row.createCell(idx).setCellValue(value);
    }
    else {
      if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
        Double existingValue = cell.getNumericCellValue();
        if (value != existingValue.floatValue()) {
          StringBuilder sb = new StringBuilder();
          sb.append("Changed value: ")
              .append(CellReference.convertNumToColString(cell.getColumnIndex()))
              .append(cell.getRowIndex()+1)
              .append(" = ")
              .append(cell.getStringCellValue())
              .append(" -> ")
              .append(value);
          sf_logger.info(sb.toString());
        }
      }
      else {
        String existingValue = cell.getStringCellValue();

        StringBuilder sb = new StringBuilder();
        sb.append("Changed value: ")
            .append(CellReference.convertNumToColString(cell.getColumnIndex()+1))
            .append(cell.getRowIndex()+1)
            .append(" = ")
            .append(existingValue)
            .append(" -> ")
            .append(value);
        sf_logger.info(sb.toString());
        
        row.removeCell(cell);
        row.createCell(idx).setCellType(Cell.CELL_TYPE_NUMERIC);
      }

      cell.setCellValue(value);
    }
  }

  protected Sheet getNewSheet(String sheetName, Workbook workbook) {
    Sheet genoSheet = workbook.getSheet(sheetName);
    if (genoSheet != null) {
      workbook.removeSheetAt(workbook.getSheetIndex(genoSheet));
    }
    genoSheet = workbook.createSheet(sheetName);

    return genoSheet;
  }

  protected void writeBooleanColumn(FileWriter writer, Boolean field) throws IOException {
    if (writer != null && field != null) {
      if (field) {
        writer.write("1");
      }
      else {
        writer.write("0");
      }
      writer.write(",");
    }
  }

  protected static boolean stringIsNumber(String in) {        
    try {
      Double.parseDouble(in);
    } catch (NumberFormatException ex) {
      return false;
    }

    return true;
  }
}
