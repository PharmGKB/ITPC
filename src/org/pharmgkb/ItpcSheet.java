package org.pharmgkb;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import util.*;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;


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

  private CellStyle styleHighlight = null;

  protected int subjectId = -1;
  protected int projectSiteIdx = -1;
  protected int ageIdx = -1;
  protected int genderIdx = -1;
  protected int raceIdx = -1;
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
  protected int projectNotesIdx = -1;
  protected int tumorDimensionIdx = -1;
  protected int numPositiveNodesIdx = -1;
  protected int tumorGradingIdx = -1;
  protected int pgrStatusIdx = -1;
  protected int radioIdx = -1;
  protected int additionalCancerIdx = -1;
  protected int addCxIpsilateralIdx = -1;
  protected int addCxDistantRecurIdx = -1;
  protected int addCxContralateralIdx = -1;
  protected int addCxSecondInvasiveIdx = -1;
  protected int addCxLastEvalIdx = -1;
  protected int daysDiagToDeathIdx = -1;
  protected int patientDiedIdx = -1;
  protected int diseaseFreeSurvivalTimeIdx = -1;
  protected int survivalNotDiedIdx = -1;
  protected int causeOfDeathIdx = -1;

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
  protected Set<Integer> sampleSourceIdxs = Sets.newHashSet();
  protected static final Set<String> genotypeSourceHeaderTitles = Sets.newHashSet();
  static {
    genotypeSourceHeaderTitles.add("rs4986774 genotyping source");
    genotypeSourceHeaderTitles.add("rs1065852 genotyping source");
    genotypeSourceHeaderTitles.add("rs3892097 genotyping source");
    genotypeSourceHeaderTitles.add("CYP2D6*5 genotyping source");
    genotypeSourceHeaderTitles.add("rs5030655 genotyping source");
    genotypeSourceHeaderTitles.add("rs16947 genotyping source");
    genotypeSourceHeaderTitles.add("rs28371706 genotyping source");
    genotypeSourceHeaderTitles.add("rs28371725 genotyping source");
  }

  protected int amplichipidx = -1;
  protected int otherGenoIdx = -1;

  protected int allele1finalIdx = -1;
  protected int allele2finalIdx = -1;
  protected int genotypeIdx = -1;
  protected int genoMetabStatusIdx = -1;
  protected int weakIdx = -1;
  protected int potentIdx = -1;
  protected int metabStatusIdx = -1;
  protected int includeCrit1Idx = -1;
  protected int includeCrit2Idx = -1;
  protected int includeCrit3Idx = -1;
  protected int scoreIdx = -1;
  protected int exclude1Idx = -1;
  protected int exclude2Idx = -1;
  protected int exclude3Idx = -1;
  protected int exclude4Idx = -1;
  protected int newFirstDiseaseEventIdx = -1;
  protected int diagToEventCalcIdx = -1;

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
  protected int bfciIdx = -1;
  protected int genoSourceIdx = -1;

  private PoiWorksheetIterator m_sampleIterator = null;

  protected static final Map<Pattern,Med> sf_medPatterns = Maps.newHashMap();
  static {
    sf_medPatterns.put(Pattern.compile("Fluoxetine"), Med.Fluoxetine);
    sf_medPatterns.put(Pattern.compile("Paroxetine"), Med.Paroxetine);
    sf_medPatterns.put(Pattern.compile("Quinidine"), Med.Quinidine);
    sf_medPatterns.put(Pattern.compile("Buproprion"), Med.Buproprion);
    sf_medPatterns.put(Pattern.compile("Duloxetine"), Med.Duloxetine);
    sf_medPatterns.put(Pattern.compile("Sertraline"), Med.Sertraline);
    sf_medPatterns.put(Pattern.compile("Diphenhydramine"), Med.Diphenhydramine);
    sf_medPatterns.put(Pattern.compile("Thioridazine"), Med.Thioridazine);
    sf_medPatterns.put(Pattern.compile("Amiodarone"), Med.Amiodarone);
    sf_medPatterns.put(Pattern.compile("Trazodone"), Med.Trazodone);
    sf_medPatterns.put(Pattern.compile("Cimetidine"), Med.Cimetidine);
    sf_medPatterns.put(Pattern.compile("Venlafaxine"), Med.Venlafaxine);
    sf_medPatterns.put(Pattern.compile("Citalopram"), Med.Citalopram);
    sf_medPatterns.put(Pattern.compile("Escitalopram"), Med.Escitalopram);
  }
  protected Map<Med,Integer> medIdx = Maps.newHashMap();

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
   * @param doHighlighting highlight changed cells in the output file
   * @throws Exception can occur from file I/O
   */
  public ItpcSheet(File file, boolean doHighlighting) throws Exception {
    if (file == null || !(file.getName().endsWith(".xls") || file.getName().endsWith(".xlsx"))) {
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
      if (doHighlighting) {
        doHighlighting();
      }

      parseColumnIndexes();

      PoiWorksheetIterator sampleIterator = new PoiWorksheetIterator(m_dataSheet);
      setSampleIterator(sampleIterator);
      skipNext(); // skip header row
      skipNext(); // skip legend row
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

      for (Pattern pattern : sf_medPatterns.keySet()) {
        if (pattern.matcher(header).matches()) {
          medIdx.put(sf_medPatterns.get(pattern), idx);
        }
      }

      if (StringUtils.isNotEmpty(header)) {
        header = header.trim().toLowerCase();
      }
      if (header.contains("subject id")) {
        subjectId = idx;
      } else if (header.equalsIgnoreCase("project site")) {
        projectSiteIdx = idx;
      } else if (header.contains("gender")) {
        genderIdx = idx;
      } else if (header.contains("age at diagnosis")) {
        ageIdx = idx;
      } else if (header.contains("race") && header.contains("omb")) {
        raceIdx = idx;
      } else if (header.equalsIgnoreCase("Metastatic Disease at Primary Disease")) {
        metastaticIdx = idx;
      } else if (header.contains("maximum dimension of tumor")) {
        tumorDimensionIdx = idx;
      } else if (header.equalsIgnoreCase("Number of Positive Nodes")) {
        numPositiveNodesIdx = idx;
      } else if (header.equalsIgnoreCase("Nottingham Grade")) {
        tumorGradingIdx = idx;
      } else if (header.equalsIgnoreCase("Progesterone Receptor")) {
        pgrStatusIdx = idx;
      } else if (header.equalsIgnoreCase("Radiation Treatment")) {
        radioIdx = idx;
      } else if (header.contains("menopause status at diagnosis")) {
        menoStatusIdx = idx;
      } else if (header.equals("estrogen receptor")) {
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
      } else if (header.equalsIgnoreCase("chemotherapy")) {
        chemoIdx = idx;
      } else if (header.contains("additional hormone or other treatment after breast surgery?")) {
        hormoneIdx = idx;
      } else if (header.contains("systemic therapy prior to surgery?")) {
        systemicTherIdx = idx;
      } else if (header.contains("annual physical exam after breast cancer surgery")) {
        followupIdx = idx;
      } else if (header.contains("time between definitive breast cancer surgery")) {
        timeBtwSurgTamoxIdx = idx;
      } else if (header.contains("first adjuvant endocrine therapy")) {
        firstAdjEndoTherIdx = idx;
      } else if (header.contains("project notes")) {
        projectNotesIdx = idx;
      } else if (header.equalsIgnoreCase("other cyp2d6 genotyping")) {
        otherGenoIdx = idx;
      } else if (header.contains("rs4986774") && !header.contains("source")) {
        rs4986774idx = idx;
      } else if (header.contains("rs1065852") && !header.contains("source")) {
        rs1065852idx = idx;
      } else if (header.contains("rs3892097") && !header.contains("source")) {
        rs3892097idx = idx;
      } else if (header.contains("rs5030655") && !header.contains("source")) {
        rs5030655idx = idx;
      } else if (header.contains("rs16947") && !header.contains("source")) {
        rs16947idx = idx;
      } else if (header.contains("rs28371706") && !header.contains("source")) {
        rs28371706idx = idx;
      } else if (header.contains("rs28371725") && !header.contains("source")) {
        rs28371725idx = idx;
      } else if (genotypeSourceHeaderTitles.contains(header)) {
        sampleSourceIdxs.add(idx);
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
      } else if(header.equals("citalopram")) {
        citalopramCol = idx;
      } else if (header.contains("amplichip call")) {
        amplichipidx = idx;
      } else if (header.equalsIgnoreCase("Additional cancer?")) {  // column BP
        additionalCancerIdx = idx;
      } else if (header.contains("time from diagnosis to ipsilateral local or regional recurrence")) {  // column BR
        addCxIpsilateralIdx = idx;
      } else if (header.contains("time from diagnosis to distant recurrence")) {  // column BS
        addCxDistantRecurIdx = idx;
      } else if (header.contains("time from diagnosis to contralateral breast cancer")) {  // column BT
        addCxContralateralIdx = idx;
      } else if (header.contains("time from diagnosis to second primary invasive cancer")) {  // column BU
        addCxSecondInvasiveIdx = idx;
      } else if (header.contains("time from diagnosis to date of last disease evaluation")) {  // column BX
        addCxLastEvalIdx = idx;
      } else if (header.equalsIgnoreCase("Time from diagnosis until death if the patient has died")) {  // column CE
        daysDiagToDeathIdx = idx;
      } else if (header.equalsIgnoreCase("Has the patient died?")) {  // column CD
        patientDiedIdx = idx;
      } else if (header.contains("enter disease-free survival time")) {  // column BO
        diseaseFreeSurvivalTimeIdx = idx;
      } else if (header.contains("survival time if patient has not died")) {  // column CI
        survivalNotDiedIdx = idx;
      } else if (header.equalsIgnoreCase("Cause of death if the patient has died")) {
        causeOfDeathIdx = idx;
      }
    }

    // new columns to add to the end of the template
    int startPgkbColsIdx = projectNotesIdx+1;

    newFirstDiseaseEventIdx = startPgkbColsIdx;
    diagToEventCalcIdx = startPgkbColsIdx   + 1;
    allele1finalIdx = startPgkbColsIdx      + 2;
    allele2finalIdx = startPgkbColsIdx      + 3;
    genotypeIdx = startPgkbColsIdx          + 4;
    genoMetabStatusIdx = startPgkbColsIdx   + 5;
    weakIdx = startPgkbColsIdx              + 6;
    potentIdx = startPgkbColsIdx            + 7;
    scoreIdx = startPgkbColsIdx             + 8;
    metabStatusIdx = startPgkbColsIdx       + 9;

    incAgeIdx = startPgkbColsIdx            + 10;
    incNonmetaIdx = startPgkbColsIdx        + 11;
    incPriorHistIdx = startPgkbColsIdx      + 12;
    incErPosIdx = startPgkbColsIdx          + 13;
    incSysTherIdx = startPgkbColsIdx        + 14;
    incAdjTamoxIdx = startPgkbColsIdx       + 15;
    incDurationIdx = startPgkbColsIdx       + 16;
    incTamoxDoseIdx = startPgkbColsIdx      + 17;
    incChemoIdx = startPgkbColsIdx          + 18;
    incHormoneIdx = startPgkbColsIdx        + 19;
    incDnaCollectionIdx = startPgkbColsIdx  + 20;
    incFollowupIdx = startPgkbColsIdx       + 21;
    incGenoDataAvailIdx = startPgkbColsIdx  + 22;

    exclude1Idx = startPgkbColsIdx          + 23;
    exclude2Idx = startPgkbColsIdx          + 24;
    exclude3Idx = startPgkbColsIdx          + 25;
    exclude4Idx = startPgkbColsIdx          + 26;

    includeCrit1Idx = startPgkbColsIdx      + 27;
    includeCrit2Idx = startPgkbColsIdx      + 28;
    includeCrit3Idx = startPgkbColsIdx      + 29;

    bfciIdx = startPgkbColsIdx              + 30;
    genoSourceIdx = startPgkbColsIdx        + 31;

    writeCellTitles(headerRow);
    styleCells(headerRow, startPgkbColsIdx, headerRow.getCell(0).getCellStyle());

    // write the description row
    Row descrRow = m_dataSheet.getRow(1);
    writeCellDescr(descrRow);
    styleCells(descrRow, startPgkbColsIdx, descrRow.getCell(0).getCellStyle());
  }

  private void writeCellTitles(Row headerRow) {
    ExcelUtils.writeCell(headerRow, newFirstDiseaseEventIdx, "First Disease Event (calculated)");
    ExcelUtils.writeCell(headerRow, diagToEventCalcIdx, "Time from Primary Diagnosis to First Disease Event (calculated)");

    ExcelUtils.writeCell(headerRow, allele1finalIdx, "CYP2D6 Allele 1 (Final)");
    ExcelUtils.writeCell(headerRow, allele2finalIdx, "CYP2D6 Allele 2 (Final)");
    ExcelUtils.writeCell(headerRow, genotypeIdx, "CYP2D6 Genotype (PharmGKB)");
    ExcelUtils.writeCell(headerRow, genoMetabStatusIdx, "Metabolizer Status based on Genotypes only (PharmGKB)");
    ExcelUtils.writeCell(headerRow, weakIdx, "Weak Drug (PharmGKB)");
    ExcelUtils.writeCell(headerRow, potentIdx, "Potent Drug (PharmGKB)");
    ExcelUtils.writeCell(headerRow, scoreIdx, "CYP2D6 Genotype Score (PharmGKB)");
    ExcelUtils.writeCell(headerRow, metabStatusIdx, "Metabolizer Status based on CYP2D6 Genotype Score (PharmGKB)");

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
    ExcelUtils.writeCell(headerRow, incGenoDataAvailIdx, "Inc 9\nCYP2D6 *4 genotype data available for assessment");

    ExcelUtils.writeCell(headerRow, exclude1Idx, "Exclusion 1:\ntime of event unknown");
    ExcelUtils.writeCell(headerRow, exclude2Idx, "Exclusion 2:\nDFST agrees with Additional Cancer and Patient Death");
    ExcelUtils.writeCell(headerRow, exclude3Idx, "Exclusion 3:\nCheck survival time against Patient Death");
    ExcelUtils.writeCell(headerRow, exclude4Idx, "Exclusion 4:\nDFST agrees with Additional Cancer and survival time");

    ExcelUtils.writeCell(headerRow, includeCrit1Idx, "Criterion 1");
    ExcelUtils.writeCell(headerRow, includeCrit2Idx, "Criterion 2");
    ExcelUtils.writeCell(headerRow, includeCrit3Idx, "Criterion 3");
    ExcelUtils.writeCell(headerRow, bfciIdx, "BCFI(Breast-Cancer Free Interval)");
    ExcelUtils.writeCell(headerRow, genoSourceIdx, "Genotyping Source");
  }

  private void writeCellDescr(Row descrRow) {
    ExcelUtils.writeCell(descrRow, newFirstDiseaseEventIdx, "none = 0, local/regional recurrence = 1,  distant recurrence = 2,  contralateral breast cancer = 3, other second non-breast primary = 4, death without recurrence, contralateral breast cancer or second non-breast primary cancer = 5, based on columns BR-BU and CD");
    ExcelUtils.writeCell(descrRow, diagToEventCalcIdx, "time to the first of a local/regional/distant recurrence, contralateral breast disease or a second primary cancer, death without recurrence, or, if none of these, then time to last disease evaluation (days)");

    ExcelUtils.writeCell(descrRow, allele1finalIdx, "");
    ExcelUtils.writeCell(descrRow, allele2finalIdx, "");
    ExcelUtils.writeCell(descrRow, genotypeIdx, "");
    ExcelUtils.writeCell(descrRow, genoMetabStatusIdx, "Extensive (EM/EM, EM/UM, IM/UM, UM/UM); Intermediate (EM/PM, EM/IM, IM/IM, IM/PM, PM/UM); Poor (PM/PM); anything else is categorized as unknown");
    ExcelUtils.writeCell(descrRow, weakIdx, "");
    ExcelUtils.writeCell(descrRow, potentIdx, "");
    ExcelUtils.writeCell(descrRow, scoreIdx, "The score of each allele added together");
    ExcelUtils.writeCell(descrRow, metabStatusIdx, "Extensive, Intermediate, Poor, or Unknown");

    ExcelUtils.writeCell(descrRow, incAgeIdx, "");
    ExcelUtils.writeCell(descrRow, incNonmetaIdx, "");
    ExcelUtils.writeCell(descrRow, incPriorHistIdx, "");
    ExcelUtils.writeCell(descrRow, incErPosIdx, "");
    ExcelUtils.writeCell(descrRow, incSysTherIdx, "");
    ExcelUtils.writeCell(descrRow, incAdjTamoxIdx, "");
    ExcelUtils.writeCell(descrRow, incDurationIdx, "");
    ExcelUtils.writeCell(descrRow, incTamoxDoseIdx, "");
    ExcelUtils.writeCell(descrRow, incChemoIdx, "");
    ExcelUtils.writeCell(descrRow, incHormoneIdx, "");
    ExcelUtils.writeCell(descrRow, incDnaCollectionIdx, "");
    ExcelUtils.writeCell(descrRow, incFollowupIdx, "");
    ExcelUtils.writeCell(descrRow, incGenoDataAvailIdx, "");

    ExcelUtils.writeCell(descrRow, exclude1Idx, "Column BP is Yes and all of BR-BU has no data or Column BP is No and one of BR-BU has data");
    ExcelUtils.writeCell(descrRow, exclude2Idx, "Column BO has days and either Column BP is yes or Column CD is yes");
    ExcelUtils.writeCell(descrRow, exclude3Idx, "Column CD is yes and Column CI has days");
    ExcelUtils.writeCell(descrRow, exclude4Idx, "Column BO is less than Column BX, or Column BX is greater than Column CI");

    ExcelUtils.writeCell(descrRow, includeCrit1Idx, "based on Inc 1, 2a, 3, 4b, 4c, 5, 6, 8, 9\nnot otherwise excluded");
    ExcelUtils.writeCell(descrRow, includeCrit2Idx, "based on Inc 2a, 3, 4c, 5, 6, 9\nnot otherwise excluded");
    ExcelUtils.writeCell(descrRow, includeCrit3Idx, "all subjects\nnot otherwise excluded");
    ExcelUtils.writeCell(descrRow, bfciIdx, "as per Huddis et al. 2000 (based on CE,CG,BR,BS,BT)");
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

  public void skipNext() {
    rowIndexPlus();
    this.getSampleIterator().next();
  }

  public void remove() {
    throw new UnsupportedOperationException("org.pharmgkb.ItpcSheet does not support removing Subjects");
  }

  protected Subject parseSubject(List<String> fields) {
    Subject subject = new Subject();

    subject.setSubjectId(fields.get(subjectId));
    subject.setProjectSite(fields.get(projectSiteIdx));
    subject.setAge(fields.get(ageIdx));
    subject.setGender(fields.get(genderIdx));
    subject.setRace(fields.get(raceIdx));
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
    subject.setTumorDimension(fields.get(tumorDimensionIdx));
    subject.setNumPositiveNodes(fields.get(numPositiveNodesIdx));
    subject.setTumorGrading(fields.get(tumorGradingIdx));
    subject.setProgesteroneReceptor(fields.get(pgrStatusIdx));
    subject.setRadiotherapy(fields.get(radioIdx));
    subject.setAdditionalCancer(fields.get(additionalCancerIdx));
    subject.setAddCxIpsilateral(fields.get(addCxIpsilateralIdx));
    subject.setAddCxDistantRecur(fields.get(addCxDistantRecurIdx));
    subject.setAddCxContralateral(fields.get(addCxContralateralIdx));
    subject.setAddCxSecondInvasive(fields.get(addCxSecondInvasiveIdx));
    subject.setAddCxLastEval(fields.get(addCxLastEvalIdx));
    subject.setDaysDiagtoDeath(fields.get(daysDiagToDeathIdx));
    subject.setPatientDied(fields.get(patientDiedIdx));
    subject.setDiseaseFreeSurvivalTime(fields.get(diseaseFreeSurvivalTimeIdx));
    subject.setSurvivalNotDied(fields.get(survivalNotDiedIdx));
    subject.setCauseOfDeath(fields.get(causeOfDeathIdx));

    subject.setRs4986774(new VariantAlleles(fields.get(rs4986774idx)));
    subject.setRs1065852(new VariantAlleles(fields.get(rs1065852idx)));
    subject.setRs3892097(new VariantAlleles(fields.get(rs3892097idx)));
    subject.setRs5030655(new VariantAlleles(fields.get(rs5030655idx)));
    subject.setRs16947(new VariantAlleles(fields.get(rs16947idx)));
    subject.setRs28371706(new VariantAlleles(fields.get(rs28371706idx)));
    subject.setRs28371725(new VariantAlleles(fields.get(rs28371725idx)));
    subject.setDeletion(fields.get(star5idx));

    for (Integer idx : sampleSourceIdxs) {
      if (fields.get(idx) != null) {
        if (fields.get(idx).contains("0")) {
          subject.addSampleSource(Subject.SampleSource.TUMOR_FFP);
        }
        if (fields.get(idx).contains("1")) {
          subject.addSampleSource(Subject.SampleSource.BLOOD);
        }
        if (fields.get(idx).contains("2")) {
          subject.addSampleSource(Subject.SampleSource.BUCCAL);
        }
        if (fields.get(idx).contains("3")) {
          subject.addSampleSource(Subject.SampleSource.TUMOR_FROZEN);
        }
        if (fields.get(idx).contains("4")) {
          subject.addSampleSource(Subject.SampleSource.NORMAL_PARAFFIN);
        }
      }
    }
    if (subject.getSampleSources().isEmpty()) {
      subject.addSampleSource(Subject.SampleSource.UNKNOWN);
    }

    subject.setGenotypeAmplichip(fields.get(amplichipidx));
    if (fields.size()>otherGenoIdx && !StringUtils.isBlank(fields.get(otherGenoIdx))) {
      subject.setGenotypeAmplichip(fields.get(otherGenoIdx));
    }

    for (Med med : medIdx.keySet()) {
      subject.addMedStatus(med, translateDrugFieldToValue(fields.get(medIdx.get(med))));
    }

    subject.setDcisStatus(isDcis(fields.get(projectNotesIdx)));

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
    CellStyle highlight = getHighlightStyle();
    subject.calculateGenotypeLimited();

    ExcelUtils.writeCell(row, newFirstDiseaseEventIdx, subject.getFirstDiseaseEventCalc(), highlight);
    ExcelUtils.writeCell(row, diagToEventCalcIdx, subject.getDiagToEventDaysCalc(), highlight);

    ExcelUtils.writeCell(row, allele1finalIdx, subject.getGenotypeFinal().get(0), highlight);
    ExcelUtils.writeCell(row, allele2finalIdx, subject.getGenotypeFinal().get(1), highlight);
    ExcelUtils.writeCell(row, genotypeIdx, subject.getGenotypeFinal().getMetabolizerStatus(), highlight);
    ExcelUtils.writeCell(row, genoMetabStatusIdx, subject.getGenotypeFinal().getMetabolizerGroup(), highlight);
    ExcelUtils.writeCell(row, weakIdx, subject.getWeak().toString(), highlight);
    ExcelUtils.writeCell(row, potentIdx, subject.getPotent().toString(), highlight);
    ExcelUtils.writeCell(row, scoreIdx, ItpcUtils.floatDisplay(subject.getScore()), highlight);
    ExcelUtils.writeCell(row, metabStatusIdx, subject.getMetabolizerGroup(), highlight);

    ExcelUtils.writeCell(row, incAgeIdx, ItpcUtils.valueToInclusion(subject.passInclusion1()), highlight);
    ExcelUtils.writeCell(row, incNonmetaIdx, ItpcUtils.valueToInclusion(subject.passInclusion2a()), highlight);
    ExcelUtils.writeCell(row, incPriorHistIdx, ItpcUtils.valueToInclusion(subject.passInclusion2b()), highlight);
    ExcelUtils.writeCell(row, incErPosIdx, ItpcUtils.valueToInclusion(subject.passInclusion3()), highlight);
    ExcelUtils.writeCell(row, incSysTherIdx, ItpcUtils.valueToInclusion(subject.passInclusion4()), highlight);
    ExcelUtils.writeCell(row, incAdjTamoxIdx, ItpcUtils.valueToInclusion(subject.passInclusion4a()), highlight);
    ExcelUtils.writeCell(row, incDurationIdx, ItpcUtils.valueToInclusion(subject.passInclusion4b()), highlight);
    ExcelUtils.writeCell(row, incTamoxDoseIdx, ItpcUtils.valueToInclusion(subject.passInclusion4c()), highlight);
    ExcelUtils.writeCell(row, incChemoIdx, ItpcUtils.valueToInclusion(subject.passInclusion5()), highlight);
    ExcelUtils.writeCell(row, incHormoneIdx, ItpcUtils.valueToInclusion(subject.passInclusion6()), highlight);
    ExcelUtils.writeCell(row, incDnaCollectionIdx, ItpcUtils.valueToInclusion(subject.passInclusion7()), highlight);
    ExcelUtils.writeCell(row, incFollowupIdx, ItpcUtils.valueToInclusion(subject.passInclusion8()), highlight);
    ExcelUtils.writeCell(row, incGenoDataAvailIdx, ItpcUtils.valueToInclusion(subject.passInclusion9()), highlight);

    ExcelUtils.writeCell(row, exclude1Idx, ItpcUtils.valueToExclusion(subject.exclude1()), highlight);
    ExcelUtils.writeCell(row, exclude2Idx, ItpcUtils.valueToExclusion(subject.exclude4()), highlight);
    ExcelUtils.writeCell(row, exclude3Idx, ItpcUtils.valueToExclusion(subject.exclude5()), highlight);
    ExcelUtils.writeCell(row, exclude4Idx, ItpcUtils.valueToExclusion(subject.exclude6()), highlight);

    ExcelUtils.writeCell(row, includeCrit1Idx, ItpcUtils.valueToInclusion(subject.includeCrit1()), highlight);
    ExcelUtils.writeCell(row, includeCrit2Idx, ItpcUtils.valueToInclusion(subject.includeCrit2()), highlight);
    ExcelUtils.writeCell(row, includeCrit3Idx, ItpcUtils.valueToInclusion(subject.includeCrit3()), highlight);

    ExcelUtils.writeCell(row, bfciIdx, subject.getBreastCancerFreeInterval(), highlight);
    ExcelUtils.writeCell(row, genoSourceIdx, subject.getSampleSource().toString(), highlight);
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

  public void doHighlighting() {
    if (styleHighlight == null) {
      styleHighlight = getWorkbook().createCellStyle();

      styleHighlight.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
      styleHighlight.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
      styleHighlight.setAlignment(HSSFCellStyle.ALIGN_CENTER);
      styleHighlight.setWrapText(true);
    }
  }

  public CellStyle getHighlightStyle() {
    return styleHighlight;
  }

  /**
   * Styles the given row with the Title Style specified in <code>getTitleStyle</code>. The <code>startIndex</code>
   * parameter specifies which column column to start applying the style on (0 = all columns) inclusively.
   * @param row an Excel Row
   * @param startIndex the index of the column to start applying the style on
   * @param style the CellStyle to apply
   */
  public void styleCells(Row row, int startIndex, CellStyle style) {
    Iterator<Cell> headerCells = row.cellIterator();

    while (headerCells.hasNext()) {
      Cell headerCell=headerCells.next();
      if (headerCell.getColumnIndex()>=startIndex) {
        headerCell.setCellStyle(style);
      }
    }
  }

  /**
   * Returns whether a given String contains the DCIS descriptor
   * @param notes the Subject's notes field as a String
   * @return a Value if the note contains DCIS test
   */
  private Value isDcis(String notes) {
    Value isDcis = Value.Unknown;

    if (!StringUtils.isBlank(notes)) {
      if (notes.contains("DCIS, no invasive component")) {
        isDcis = Value.Yes;
      }
      else {
        isDcis = Value.No;
      }
    }
    return isDcis;
  }
}
