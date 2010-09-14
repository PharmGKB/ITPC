package org.pharmgkb;/*
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
import java.io.*;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import util.*;


/**
 * Created by IntelliJ IDEA. User: whaleyr Date: Jun 18, 2010 Time: 10:07:11 AM To change this template use File |
 * Settings | File Templates.
 */
public class ItpcSheet implements Iterator {
  public static final String SHEET_NAME = "Combined_Data";
  private static final Logger sf_logger = Logger.getLogger(ItpcSheet.class);

  private File inputFile = null;

  private Sheet m_dataSheet = null;
  private int m_rowIndex = -1;

  protected int subjectId = -1;
  protected int projectSiteIdx = -1;
  protected int ageIdx = -1;
  protected int menoStatusIdx = -1;
  protected int metastaticIdx = -1;
  protected int erStatusIdx = -1;
  protected int durationIdx = -1;
  protected int tamoxDoseIdx = -1;
  protected int tumorSourceIdx = -1;
  protected int bloodSourceIdx = -1;
  protected int priorHistoryIdx = -1;
  protected int priorSitesIdx = -1;
  protected int priorDcisIdx = -1;
  protected int chemoIdx = -1;
  protected int hormoneIdx = -1;
  protected int systemicTherIdx = -1;
  protected int followupIdx = -1;
  protected int timeBtwSurgTamoxIdx = -1;
  protected int firstAdjEndoTherIdx = -1;
  protected int genoSourceIdx1 = -1;
  protected int genoSourceIdx2 = -1;
  protected int genoSourceIdx3 = -1;
  protected int projectNotesIdx = -1;

  protected int fluoxetineCol = -1;
  protected int paroxetineCol = -1;
  protected int quinidienCol = -1;
  protected int buproprionCol = -1;
  protected int duloxetineCol = -1;
  protected int cimetidineCol = -1;
  protected int sertralineCol = -1;
  protected int citalopramCol = -1;

  protected int rs4986774idx = -1;
  protected int rs1065852idx = -1;
  protected int rs3892097idx = -1;
  protected int star5idx = -1;
  protected int rs5030655idx = -1;
  protected int rs16947idx = -1;
  protected int rs28371706idx = -1;
  protected int rs28371725idx = -1;

  protected int amplichipidx = -1;

  protected int allele1idx = -1;
  protected int allele2idx = -1;
  protected int allele1finalIdx = -1;
  protected int allele2finalIdx = -1;
  protected int callCommentsIdx = -1;
  protected int genotypeIdx = -1;
  protected int weakIdx = -1;
  protected int potentIdx = -1;
  protected int metabStatusIdx = -1;
  protected int includeIdx = -1;
  protected int scoreIdx = -1;

  protected int incAgeIdx = -1;
  protected int incNonmetaIdx = -1;
  protected int incPriorHistIdx = -1;
  protected int incErPosIdx = -1;
  protected int incSysTherIdx = -1;
  protected int incAdjTamoxIdx = -1;
  protected int incDurationIdx = -1;
  protected int incTamoxDoseIdx = -1;
  protected int incChemoIdx = -1;
  protected int incHormoneIdx = -1;
  protected int incDnaCollectionIdx = -1;
  protected int incFollowupIdx = -1;
  protected int incGenoDataAvailIdx = -1;

  private PoiWorksheetIterator m_sampleIterator = null;

  /**
   * Constructor for an ITPC data file
   * <br/>
   * Expectations for <code>file</code> parameter:
   * <ol>
   * <li>file is an Excel .XLS formatted spreadsheet</li>
   * <li>there is a sheet in the file called "Combined_Data"</li>
   * <li>the sheet has the first row as column headers</li>
   * <li>the sheet has the second row as column legends</li>
   * </ol>
   * After this has been initialized, samples can be gathered by using the <code>getSampleIterator</code> method
   * @param file an Excel .XLS file
   * @throws Exception can occur from file I/O
   */
  public ItpcSheet(File file) throws Exception {
    if (file == null || !file.getName().endsWith(".xls")) {
      throw new Exception("File not in right format: " + file);
    }

    inputFile = file;
    InputStream inputFileStream = null;
    sf_logger.info("Using input file: " + inputFile);

    try {
      inputFileStream = new FileInputStream(inputFile);
      Workbook inputWorkbook = WorkbookFactory.create(inputFileStream);
      Sheet inputSheet = inputWorkbook.getSheet(SHEET_NAME);
      if (inputSheet == null) {
        throw new Exception("Cannot find worksheet named " + SHEET_NAME);
      }

      m_dataSheet = inputSheet;

      parseColumnIndexes();

      PoiWorksheetIterator sampleIterator = new PoiWorksheetIterator(m_dataSheet);
      this.setSampleIterator(sampleIterator);
      this.next(); // skip header row
      this.next(); // skip legend row
    }
    catch (Exception ex) {
      throw new Exception("Error initializing ITPC Sheet", ex);
    }
    finally {
      if (inputFileStream != null) {
        IOUtils.closeQuietly(inputFileStream);
      }
    }
  }

  protected void parseColumnIndexes() throws Exception {
    if (sf_logger.isDebugEnabled()) {
      sf_logger.debug("Parsing column indexes and headings");
    }

    Row headerRow = m_dataSheet.getRow(0);
    Iterator<Cell> headerCells = headerRow.cellIterator();

    while(headerCells.hasNext()) {
      Cell headerCell = headerCells.next();
      String header = headerCell.getStringCellValue();
      int idx = headerCell.getColumnIndex();

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
      } else if (header.contains("sites of prior cancer")) {
        priorSitesIdx = idx;
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
    }

    // new columns to add to the end of the template
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

    writeCellTitles(headerRow);

    POIUtils.styleTitleCells(headerRow, startPgkbColsIdx);
  }

  private void writeCellTitles(Row headerRow) {
    ExcelUtils.writeCell(headerRow, allele1idx, "CYP2D6 Allele 1 (PharmGKB)");
    ExcelUtils.writeCell(headerRow, allele2idx, "CYP2D6 Allele 2 (PharmGKB)");
    ExcelUtils.writeCell(headerRow, allele1finalIdx, "CYP2D6 Allele 1 (Final)");
    ExcelUtils.writeCell(headerRow, allele2finalIdx, "CYP2D6 Allele 2 (Final)");
    ExcelUtils.writeCell(headerRow, callCommentsIdx, "Curator comments on calls");
    ExcelUtils.writeCell(headerRow, scoreIdx, "Drug and CYP2D6 Genotype Score");

    ExcelUtils.writeCell(headerRow, genotypeIdx, "Genotype (PharmGKB)");
    ExcelUtils.writeCell(headerRow, weakIdx, "Weak Drug (PharmGKB)");
    ExcelUtils.writeCell(headerRow, potentIdx, "Potent Drug (PharmGKB)");
    ExcelUtils.writeCell(headerRow, metabStatusIdx, "Metabolizer Status (PharmGKB)");

    ExcelUtils.writeCell(headerRow, incAgeIdx, "Inc 1\nPostmenopausal");
    ExcelUtils.writeCell(headerRow, incNonmetaIdx, "Inc 2a\nNon-metastatic invasive cancer");
    ExcelUtils.writeCell(headerRow, incPriorHistIdx, "Inc 2b\nNo prior history of contralateral breast cancer");
    ExcelUtils.writeCell(headerRow, incErPosIdx, "Inc 3\nER Positive");
    ExcelUtils.writeCell(headerRow, incSysTherIdx, "Inc 4\nSystemic therapy prior to surgery");
    ExcelUtils.writeCell(headerRow, incAdjTamoxIdx, "Inc 4a\nAdjuvant tamoxifen initiated within 6 months");
    ExcelUtils.writeCell(headerRow, incDurationIdx, "Inc 4b\nTamoxifen duration intended 5 years");
    ExcelUtils.writeCell(headerRow, incTamoxDoseIdx, "Inc 4c\nTamoxifen dose intended 20mg/day");
    ExcelUtils.writeCell(headerRow, incChemoIdx, "Inc 5\nNo adjuvant chemotherapy");
    ExcelUtils.writeCell(headerRow, incHormoneIdx, "Inc 6\nNo additional adjuvant hormonal therapy");
    ExcelUtils.writeCell(headerRow, incDnaCollectionIdx, "Inc 7\nTiming of DNA Collection");
    ExcelUtils.writeCell(headerRow, incFollowupIdx, "Inc 8\nAdequate follow-up");
    ExcelUtils.writeCell(headerRow, incGenoDataAvailIdx, "Inc 9\nCYP2D6 genotype data available for assessment of *3, *4, *10, and *41");

    ExcelUtils.writeCell(headerRow, includeIdx, "Include");
  }

  private PoiWorksheetIterator getSampleIterator() {
    return m_sampleIterator;
  }

  private void setSampleIterator(PoiWorksheetIterator sampleIterator) {
    m_sampleIterator = sampleIterator;
  }

  public boolean hasNext() {
    return this.getSampleIterator().hasNext();
  }

  public Subject next() {
    rowIndexPlus();
    return parseSubject(this.getSampleIterator().next());
  }

  public void remove() {
    throw new UnsupportedOperationException("org.pharmgkb.ItpcSheet does not support removing Subjects");
  }

  protected Subject parseSubject(List<String> fields) {
    Subject subject = new Subject();

    subject.setSubjectId(fields.get(subjectId));
    subject.setProjectSite(fields.get(projectSiteIdx));
    subject.setAge(fields.get(ageIdx));
    subject.setMetastatic(fields.get(metastaticIdx));
    subject.setMenoStatus(fields.get(menoStatusIdx));
    subject.setErStatus(fields.get(erStatusIdx));
    subject.setDuration(fields.get(durationIdx));
    subject.setTamoxDose(fields.get(tamoxDoseIdx));
    subject.setTumorSource(fields.get(tumorSourceIdx));
    subject.setBloodSource(fields.get(bloodSourceIdx));
    subject.setPriorHistory(fields.get(priorHistoryIdx));
    subject.setPriorDcis(fields.get(priorDcisIdx));
    subject.setChemotherapy(fields.get(chemoIdx));
    subject.setHormoneTherapy(fields.get(hormoneIdx));
    subject.setSystemicTher(fields.get(systemicTherIdx));
    subject.setFollowup(fields.get(followupIdx));
    subject.setTimeBtwSurgTamox(fields.get(timeBtwSurgTamoxIdx));
    subject.setFirstAdjEndoTher(fields.get(firstAdjEndoTherIdx));

    if (!StringUtils.isBlank(fields.get(genoSourceIdx1)) && !fields.get(genoSourceIdx1).equals("NA")) {
      subject.setGenoSource(fields.get(genoSourceIdx1));
    }
    else if (!StringUtils.isBlank(fields.get(genoSourceIdx2)) && !fields.get(genoSourceIdx2).equals("NA")) {
      subject.setGenoSource(fields.get(genoSourceIdx2));
    }
    else if (!StringUtils.isBlank(fields.get(genoSourceIdx3)) && !fields.get(genoSourceIdx3).equals("NA")) {
      subject.setGenoSource(fields.get(genoSourceIdx3));
    }

    subject.setHasFluoxetine(translateDrugFieldToValue(fields.get(fluoxetineCol)));
    subject.setHasParoxetine(translateDrugFieldToValue(fields.get(paroxetineCol)));
    subject.setHasQuinidine(translateDrugFieldToValue(fields.get(quinidienCol)));
    subject.setHasBuproprion(translateDrugFieldToValue(fields.get(buproprionCol)));
    subject.setHasDuloxetine(translateDrugFieldToValue(fields.get(duloxetineCol)));
    subject.setHasCimetidine(translateDrugFieldToValue(fields.get(cimetidineCol)));
    subject.setHasSertraline(translateDrugFieldToValue(fields.get(sertralineCol)));
    subject.setHasCitalopram(translateDrugFieldToValue(fields.get(citalopramCol)));

    subject.setRs4986774(new VariantAlleles(fields.get(rs4986774idx)));
    subject.setRs1065852(new VariantAlleles(fields.get(rs1065852idx)));
    subject.setRs3892097(new VariantAlleles(fields.get(rs3892097idx)));
    subject.setRs5030655(new VariantAlleles(fields.get(rs5030655idx)));
    subject.setRs16947(new VariantAlleles(fields.get(rs16947idx)));
    subject.setRs28371706(new VariantAlleles(fields.get(rs28371706idx)));
    subject.setRs28371725(new VariantAlleles(fields.get(rs28371725idx)));
    subject.setDeletion(fields.get(star5idx));

    subject.setGenotypeAmplichip(fields.get(amplichipidx));
    subject.setCuratorComment(fields.get(callCommentsIdx));

    return subject;
  }

  private Value translateDrugFieldToValue(String field) {
    if (ItpcUtils.isBlank(field)) {
      return Value.Unknown;
    }
    else if (field.equals("1")) {
      return Value.Yes;
    }
    else if (field.equals("0")) {
      return Value.No;
    }
    else {
      return Value.Unknown;
    }
  }

  public int getCurrentRowIndex() {
    return m_rowIndex;
  }

  private void rowIndexPlus() {
    m_rowIndex++;
  }

  public Row getCurrentRow() {
    return m_dataSheet.getRow(this.getCurrentRowIndex());
  }

  public void writeSubjectCalculatedColumns(Subject subject) {
    Row row = this.getCurrentRow();

    ExcelUtils.writeCell(row, allele1idx, subject.getGenotypePgkb().get(0));
    ExcelUtils.writeCell(row, allele2idx, subject.getGenotypePgkb().get(1));
    ExcelUtils.writeCell(row, allele1finalIdx, subject.getGenotypeFinal().get(0));
    ExcelUtils.writeCell(row, allele2finalIdx, subject.getGenotypeFinal().get(1));
    ExcelUtils.writeCell(row, callCommentsIdx, subject.getCuratorComment());
    ExcelUtils.writeCell(row, genotypeIdx, subject.getGenotypeFinal().getMetabolizerStatus());
    ExcelUtils.writeCell(row, weakIdx, subject.getWeak().toString());
    ExcelUtils.writeCell(row, potentIdx, subject.getPotent().toString());
    if (subject.getScore()==null) {
      ExcelUtils.writeCell(row, scoreIdx, Value.Unknown.toString());
    }
    else {
      ExcelUtils.writeCell(row, scoreIdx, subject.getScore());
    }
    ExcelUtils.writeCell(row, metabStatusIdx, subject.getMetabolizerGroup());
    ExcelUtils.writeCell(row, incAgeIdx, subject.passInclusion1().toString());
    ExcelUtils.writeCell(row, incNonmetaIdx, subject.passInclusion2a().toString());
    ExcelUtils.writeCell(row, incPriorHistIdx, subject.passInclusion2b().toString());
    ExcelUtils.writeCell(row, incErPosIdx, subject.passInclusion3().toString());
    ExcelUtils.writeCell(row, incSysTherIdx, subject.passInclusion4().toString());
    ExcelUtils.writeCell(row, incAdjTamoxIdx, subject.passInclusion4a().toString());
    ExcelUtils.writeCell(row, incDurationIdx, subject.passInclusion4b().toString());
    ExcelUtils.writeCell(row, incTamoxDoseIdx, subject.passInclusion4c().toString());
    ExcelUtils.writeCell(row, incChemoIdx, subject.passInclusion5().toString());
    ExcelUtils.writeCell(row, incHormoneIdx, subject.passInclusion6().toString());
    ExcelUtils.writeCell(row, incDnaCollectionIdx, subject.passInclusion7().toString());
    ExcelUtils.writeCell(row, incFollowupIdx, subject.passInclusion8().toString());
    ExcelUtils.writeCell(row, incGenoDataAvailIdx, subject.passInclusion9().toString());
    ExcelUtils.writeCell(row, includeIdx, subject.include().toString());
  }

  public File saveOutput() throws IOException {
    File outputFile = ItpcUtils.getOutputFile(inputFile);
    sf_logger.info("Writing output to: " + outputFile);

    FileOutputStream statsOut = new FileOutputStream(outputFile);
    m_dataSheet.getWorkbook().write(statsOut);
    IOUtils.closeQuietly(statsOut);

    return outputFile;
  }

  public Workbook getWorkbook() {
    return m_dataSheet.getWorkbook();
  }
}
