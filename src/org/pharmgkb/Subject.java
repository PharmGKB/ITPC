package org.pharmgkb;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import util.ItpcUtils;
import util.Med;
import util.Value;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: whaleyr
 * Date: Jul 14, 2010
 * Time: 8:16:01 AM
 */
public class Subject {
  private static final Logger logger = Logger.getLogger(Subject.class);

  private String m_subjectId = null;
  private String m_projectSite = null;
  private String m_age = null;
  private String m_gender = null;
  private String m_race = null;
  private String m_menoStatus = null;
  private String m_metastatic = null;
  private String m_erStatus = null;
  private String m_duration = null;
  private String m_tamoxDose = null;
  private String m_tumorSource = null;
  private String m_bloodSource = null;
  private String m_priorHistory = null;
  private String m_priorDcis = null;
  private String m_chemotherapy = null;
  private String m_hormoneTherapy = null;
  private String m_systemicTher = null;
  private String m_followup = null;
  private String m_timeBtwSurgTamox = null;
  private String m_firstAdjEndoTher = null;
  private String m_tumorDimension = null;
  private String m_numPositiveNodes = null;
  private String m_tumorGrading = null;
  private String progesteroneReceptor = null;
  private String radiotherapy = null;
  private Deletion m_deletion = Deletion.Unknown;
  private Value m_additionalCancer = null;
  private String m_addCxIpsilateral = null;
  private String m_addCxDistantRecur = null;
  private String m_addCxContralateral = null;
  private String m_addCxSecondInvasive = null;
  private String m_addCxLastEval = null;
  private String m_daysDiagtoDeath = null;
  private Value m_patientDied = null;
  private String m_diseaseFreeSurvivalTime = null;
  private String m_survivalNotDied = null;
  private String m_causeOfDeath = null;
  private Value m_dcisStatus = Value.Unknown;

  private Genotype m_genotypePgkb = new Genotype();
  private Genotype m_genotypeAmplichip = new Genotype();
  private Genotype m_genotypeLimited = new Genotype();

  private VariantAlleles m_rs1065852 = new VariantAlleles();
  private VariantAlleles m_rs4986774 = new VariantAlleles();
  private VariantAlleles m_rs3892097 = new VariantAlleles();
  private VariantAlleles m_rs5030655 = new VariantAlleles();
  private VariantAlleles m_rs16947 = new VariantAlleles();
  private VariantAlleles m_rs28371706 = new VariantAlleles();
  private VariantAlleles m_rs28371725 = new VariantAlleles();
  private Set<SampleSource> m_sampleSources = Sets.newHashSet();

  private Map<Med,Value> m_medStatus = Maps.newHashMap();

  public Subject() {
    this.calculateGenotypePgkb();
  }

  public Genotype getGenotypePgkb() {
    return m_genotypePgkb;
  }

  protected void setGenotypePgkb(Genotype genotypePgkb) {
    m_genotypePgkb = genotypePgkb;
  }

  public Genotype getGenotypeAmplichip() {
    return m_genotypeAmplichip;
  }

  public void setGenotypeAmplichip(Genotype genotypeAmplichip) {
    m_genotypeAmplichip = genotypeAmplichip;
  }

  public void setGenotypeAmplichip(String alleles) {
    try {
      this.setGenotypeAmplichip(processAmplichip(alleles));
    }
    catch (Exception ex) {
      logger.warn("Cannot parse amplichip genotype: " + alleles);
    }
  }

  public Genotype getGenotypeAllFinal() {
    if (m_genotypeAmplichip != null && m_genotypeAmplichip.hasData()) {
      return m_genotypeAmplichip;
    }
    else {
      return m_genotypePgkb;
    }
  }

  public Genotype getGenotypeFinal() {
    if (getGenotypeAllFinal().isUncertain() && !getGenotypeLimited().isUncertain()) {
      return getGenotypeLimited();
    }
    else {
      return getGenotypeAllFinal();
    }
  }

  public Value getWeak() {
    if (hasMed(Med.Cimetidine) == Value.Yes
        || hasMed(Med.Sertraline) == Value.Yes
        || hasMed(Med.Citalopram) == Value.Yes) {
      return Value.Yes;
    }
    else if (hasMed(Med.Cimetidine) == Value.No
        && hasMed(Med.Sertraline) == Value.No
        && hasMed(Med.Citalopram) == Value.No) {
      return Value.No;
    }
    else {
      return Value.Unknown;
    }
  }

  public Value getPotent() {
    if (hasMed(Med.Paroxetine) == Value.Yes
        || hasMed(Med.Fluoxetine) == Value.Yes
        || hasMed(Med.Quinidine) == Value.Yes
        || hasMed(Med.Buproprion) == Value.Yes
        || hasMed(Med.Duloxetine) == Value.Yes) {
      return Value.Yes;
    }
    else if (hasMed(Med.Paroxetine) == Value.No
        && hasMed(Med.Fluoxetine) == Value.No
        && hasMed(Med.Quinidine) == Value.No
        && hasMed(Med.Buproprion) == Value.No
        && hasMed(Med.Duloxetine) == Value.No) {
      return Value.No;
    }
    else {
      return Value.Unknown;
    }
  }

  public VariantAlleles getRs1065852() {
    return m_rs1065852;
  }

  public void setRs1065852(VariantAlleles rs1065852) {
    m_rs1065852 = rs1065852;
    this.calculateGenotypePgkb();
  }

  public VariantAlleles getRs4986774() {
    return m_rs4986774;
  }

  public void setRs4986774(VariantAlleles rs4986774) {
    m_rs4986774 = rs4986774;
    this.calculateGenotypePgkb();
  }

  public VariantAlleles getRs3892097() {
    return m_rs3892097;
  }

  public void setRs3892097(VariantAlleles rs3892097) {
    m_rs3892097 = rs3892097;
    this.calculateGenotypePgkb();
  }

  public VariantAlleles getRs5030655() {
    return m_rs5030655;
  }

  public void setRs5030655(VariantAlleles rs5030655) {
    m_rs5030655 = rs5030655;
    this.calculateGenotypePgkb();
  }

  public VariantAlleles getRs16947() {
    return m_rs16947;
  }

  public void setRs16947(VariantAlleles rs16947) {
    m_rs16947 = rs16947;
    this.calculateGenotypePgkb();
  }

  public VariantAlleles getRs28371706() {
    return m_rs28371706;
  }

  public void setRs28371706(VariantAlleles rs28371706) {
    m_rs28371706 = rs28371706;
    this.calculateGenotypePgkb();
  }

  public VariantAlleles getRs28371725() {
    return m_rs28371725;
  }

  public void setRs28371725(VariantAlleles rs28371725) {
    m_rs28371725 = rs28371725;
    this.calculateGenotypePgkb();
  }

  public Float getScore() {
    return getGenotypeFinal().getScore();
  }

  public String getMetabolizerGroup() {
    Float score = this.getScore();
    if (score == null) {
      return "Uncategorized";
    }
    else if (score>=4.0) {
      return "Ultrarapid one";
    }
    else if (score>=3.5) {
      return "Ultrarapid two";
    }
    else if (score>=3.0) {
      return "Ultrarapid three";
    }
    else if (score>=2.5) {
      return "Extensive one";
    }
    else if (score>=2.0) {
      return "Extensive two";
    }
    else if (score>=1.5) {
      return "Intermediate one";
    }
    else if (score>=1.0) {
      return "Intermediate two";
    }
    else if (score>=0.5) {
      return "Intermediate three";
    }
    else if (score==0.0) {
      return "Poor";
    }
    else {
      return "Uncategorized";
    }
  }

  public boolean deletionDetectable() {
    return this.getDeletion()!=Deletion.Unknown
        || this.getRs4986774().is("-","a")
        || this.getRs1065852().is("c","t")
        || this.getRs3892097().is("a","g")
        || this.getRs5030655().is("-","t")
        || this.getRs16947().is("c","t")
        || this.getRs28371706().is("c","t")
        || this.getRs28371725().is("g","a");
  }

  public void calculateGenotypePgkb() {
    Genotype geno = new Genotype();

    switch (this.getDeletion()) {
      case Hetero: geno.addString("*5"); break;
      case Homo: geno = new Genotype("*5/*5"); break;
    }

    if (!geno.isHomoDeletion()) {
      // *3
      if (this.getRs4986774().hasData()) {
        if (geno.isHeteroDeletion() && this.getRs4986774().count("-")==2) {
          geno.addString("*3");
        }
        else if (!geno.isHeteroDeletion()) {
          for (int i=0; i<this.getRs4986774().count("-"); i++) {
            geno.addString("*3");
          }
        }
      }

      // *6
      if (this.getRs5030655().hasData()) {
        if (geno.isHeteroDeletion() && this.getRs5030655().count("-")==2) {
          geno.addString("*6");
        }
        else if (!geno.isHeteroDeletion()) {
          for (int i=0; i<this.getRs5030655().count("-"); i++) {
            geno.addString("*6");
          }
        }
      }

      // *4
      if (this.getRs3892097().hasData()) {
        if (this.getRs3892097().contains("a")) {
          geno.addString("*4");

          // this is the *4k which we don't separately call, but is still distinct from *4
          if (getRs3892097().count("a")==1 && getRs3892097().count("a")==1 && getRs16947().count("t")==1) {
            geno.addString("Unknown");
          }
        }
        if (this.getRs3892097().count("a")==2 && deletionDetectable() && !geno.isHeteroDeletion()) {
          geno.addString("*4");
        }
      }

      // *41
      if (this.getRs28371725().hasData()) {
        if (this.getRs28371725().contains("a")) {
          geno.addString("*41");
        }
        if (this.getRs28371725().count("a")==2 && deletionDetectable() && !geno.isHeteroDeletion()) {
          geno.addString("*41");
        }
      }

      //       *4                            *3                                *6 w/ exception for a haplotype rule for *10
      if (this.getRs3892097().hasData() && this.getRs4986774().hasData() && (this.getRs5030655().hasData() || (this.getRs1065852().is("t","c") && !this.getRs5030655().hasData())) && deletionDetectable()) {

        // *2
        if (getRs16947().hasData() && getRs28371706().hasData() && getRs28371725().hasData()
            && getRs16947().count("t") > getRs28371706().count("t")
            && getRs16947().count("t") > getRs28371725().count("a")
            && !(getRs16947().count("t") <= getRs1065852().count("t") && getRs16947().count("t") <= getRs3892097().count("a"))) {
          geno.addString("*2");
          if (!geno.isHeteroDeletion() &&
              (getRs16947().is("t","t") && getRs28371706().is("c","c") && getRs28371725().is("g","g"))) {
            geno.addString("*2");
          }
        }

        // *10
        if (this.getRs1065852().hasData()) {
          if (this.getRs1065852().contains("t") && this.getRs3892097().count("a")==0) {
            geno.addString("*10");
          }
          if (!geno.isHeteroDeletion() && this.getRs1065852().is("t","t") && this.getRs3892097().contains("g")) {
            geno.addString("*10");
          }
        }

        // *17
        if (this.getRs28371706().hasData() && this.getRs16947().hasData()) {
          if (this.getRs28371706().contains("t") && this.getRs16947().contains("t")) {
            geno.addString("*17");
          }
          if (this.getRs28371706().count("t")==2 && this.getRs16947().count("t")==2
              && deletionDetectable() && !geno.isHeteroDeletion()) {
            geno.addString("*17");
          }
        }
      }
      else if (!deletionDetectable()) { //what to do if we don't have complete *5 knowledge
        if (this.getRs4986774().hasData() &&
            this.getRs1065852().hasData() &&
            this.getRs3892097().hasData() &&
            this.getRs5030655().hasData() &&
            this.getRs16947().is("t","t")   &&
            this.getRs28371725().is("g","g") &&
            this.getRs28371706().is("c","c")) {
          geno.addString("*2");
        }
        else if (this.getRs4986774().hasData() &&
            this.getRs1065852().hasData() &&
            this.getRs3892097().hasData() &&
            this.getRs5030655().hasData() &&
            this.getRs16947().hasData()   &&
            this.getRs28371725().hasData() &&
            this.getRs28371706().hasData() &&
            geno.isEmpty()) {
          geno.addString("*1");
          geno.addString("Unknown");
        }
      }
    }

    if (this.getRs4986774().hasData() && // special case for partial unknown info., from Joan
        this.getRs1065852().hasData() &&
        this.getRs3892097().hasData() &&
        this.getRs5030655().hasData() &&
        this.getRs16947().is("c","t") &&
        (!this.getRs28371706().hasData() || !this.getRs28371725().hasData()) &&
        deletionDetectable() &&
        geno.isEmpty()) {
      geno.addString("*1");
      geno.addString("Unknown");
    }


    while (geno.size() < 2) {
      if (this.getRs4986774().hasData() &&
          this.getRs1065852().hasData() &&
          this.getRs3892097().hasData() &&
          this.getRs5030655().hasData() &&
          this.getRs16947().hasData()   &&
          deletionDetectable() &&
          (this.getRs28371725().hasData() || (!this.getRs28371725().hasData() && (this.getRs16947().is("C","C") || this.getRs16947().is("C","-")))) &&
          (!geno.isHeteroDeletion() || (geno.isHeteroDeletion() && (this.getRs28371725().hasData() || this.getRs28371706().hasData())))) // we can use rs16947 to exclude *41 calls so it doesn't always have to be available
      {
        geno.addString("*1");
      }
      else {
        geno.addString("Unknown");
      }
    }

    this.setGenotypePgkb(geno);
  }

  public void calculateGenotypeLimited() {
    Genotype geno = new Genotype();

    applyStarFiveLogic(geno);
    applyStarThreeLogic(geno);
    boolean starFourAble = applyStarFourLogic(geno);
    applyStarTenLogic(geno);
    applyStarFortyOneLogic(geno);
    applyStarTwoLogic(geno);
    applyStarSixLogic(geno);
    applyStarSeventeenLogic(geno);

    while (geno.size() < 2) {
      if (starFourAble) {
        geno.addString("*1");
      }
      else {
        geno.addString("Unknown");
      }
    }

    setGenotypeLimited(geno);
  }

  protected boolean applyStarFiveLogic(Genotype geno) {
    if (getDeletion() == Deletion.Unknown) {
      return false;
    }

    if (getDeletion() == Deletion.Hetero || getDeletion() == Deletion.Homo) {
      geno.addString("*5");
    }
    if (getDeletion() == Deletion.Homo) {
      geno.addString("*5");
    }
    return true;
  }

  protected boolean applyStarTwoLogic(Genotype geno) {
    if (getRs16947().hasData() && getRs28371706().hasData() && getRs28371725().hasData()
        && getRs16947().count("t") > getRs28371706().count("t")
        && getRs16947().count("t") > getRs28371725().count("a")
        && !(getRs16947().count("t") <= getRs1065852().count("t") && getRs16947().count("t") <= getRs3892097().count("a"))) {

      geno.addString("*2");

      if (!geno.isHeteroDeletion()
          && (getRs16947().is("t","t")
          && getRs28371706().is("c","c")
          && getRs28371725().is("g","g"))) {
        geno.addString("*2");
      }
      return !getRs16947().isUncertain() && !getRs28371706().isUncertain() && !getRs28371725().isUncertain();
    }
    else {
      return false;
    }
  }

  protected boolean applyStarSixLogic(Genotype geno) {
    if (this.getRs5030655().hasData()) {
      if (geno.isHeteroDeletion() && this.getRs5030655().count("-")==2) {
        geno.addString("*6");
      }
      else if (!geno.isHeteroDeletion()) {
        for (int i=0; i<this.getRs5030655().count("-"); i++) {
          geno.addString("*6");
        }
      }
      return !getRs5030655().isUncertain();
    }
    else {
      return false;
    }
  }

  protected boolean applyStarSeventeenLogic(Genotype geno) {
    if (getRs28371706().hasData() && getRs16947().hasData()) {
      if (getRs28371706().contains("t") && getRs16947().contains("t")) {
        geno.addString("*17");
      }
      if (getRs28371706().count("t")==2 && getRs16947().count("t")==2
          && deletionDetectable() && !geno.isHeteroDeletion()) {
        geno.addString("*17");
      }
      return !getRs28371706().isUncertain() && !getRs16947().isUncertain();
    }
    else return getRs28371706().hasData() || getRs16947().hasData();
  }

  protected boolean applyStarThreeLogic(Genotype geno) {
    if (getRs4986774().hasData()) {
      if (geno.isHeteroDeletion() && getRs4986774().count("-")==2) {
        geno.addString("*3");
      }
      else if (!geno.isHeteroDeletion()) {
        for (int i=0; i<getRs4986774().count("-"); i++) {
          geno.addString("*3");
        }
      }
      return !getRs4986774().isUncertain();
    }
    else {
      return false;
    }
  }

  protected boolean applyStarFourLogic(Genotype geno) {
    if (getRs3892097().hasData()) {
      if (getRs3892097().contains("a")) {
        geno.addString("*4");
      }
      if (getRs3892097().is("a","a")) {
        geno.addString("*4");
      }
      return !getRs3892097().isUncertain();
    }
    else {
      return false;
    }
  }

  protected boolean applyStarTenLogic(Genotype geno) {
    if (this.getRs1065852().hasData()) {
      if (this.getRs1065852().contains("t") && this.getRs3892097().count("a")==0) {
        geno.addString("*10");
      }
      if (!geno.isHeteroDeletion() && this.getRs1065852().is("t","t") && this.getRs3892097().contains("g")) {
        geno.addString("*10");
      }
      return !getRs1065852().isUncertain();
    }
    else {
      return false;
    }
  }

  protected boolean applyStarFortyOneLogic(Genotype geno) {
    if (this.getRs28371725().hasData()) {
      if (this.getRs28371725().contains("a")) {
        geno.addString("*41");
      }
      if (this.getRs28371725().count("a")==2 && deletionDetectable() && !geno.isHeteroDeletion()) {
        geno.addString("*41");
      }
      return !getRs28371725().isUncertain();
    }

    // rs16947 and rs28371725 are in LD so if rs28371725 is missing we can still make some calls
    if (getRs16947().hasData()) {
      if (getRs16947().is("c","t") && geno.size()==0) {
        geno.addString("*1");
        return false;
      }
      else if (getRs16947().is("c","c") || (getRs16947().is("c","-") && geno.isHeteroDeletion() )) {
        return true;
      }
    }

    return false;
  }

  public Deletion getDeletion() {
    return m_deletion;
  }

  public void setDeletion(Deletion deletion) {
    m_deletion = deletion;
    this.calculateGenotypePgkb();
  }

  public void setDeletion(String deletion) {
    if (deletion == null || deletion.equalsIgnoreCase("NA")) {
      if (getRace().equals("Asian")) {
        setDeletion(Deletion.Unknown);
      }
      else {
        setDeletion(Deletion.None);
      }
    }
    else {
      deletion = deletion.toLowerCase();

      if (deletion.equalsIgnoreCase("homozygous deletion")) {
        this.setDeletion(Deletion.Homo);
      }
      else if (deletion.contains("deletion") && !deletion.contains("no deletion")) {
        this.setDeletion(Deletion.Hetero);
      }
      else {
        this.setDeletion(Deletion.None);
      }
    }
  }

  protected static Genotype processAmplichip(String amplichip) throws Exception {
    Genotype genotype = new Genotype();

    if (amplichip != null && amplichip.contains("/")) {
      String[] tokens = amplichip.split("/");
      for (String token : tokens) {
        try {
          genotype.addString(token);
        }
        catch (Exception ex) {
          throw new Exception("Error processing amplichip: " + amplichip, ex);
        }
      }
    }

    return genotype;
  }

  public Value passInclusion1() {
    if (!ItpcUtils.isBlank(getGender()) && getGender().equals("2")) {
        return Value.No;
    }

    if (!ItpcUtils.isBlank(this.getMenoStatus())) {
      if (this.getMenoStatus().equals("2")) {
        return Value.Yes;
      }
      else {
        return Value.No;
      }
    }
    else {
      if (!ItpcUtils.isBlank(this.getAge())) {
        try {
          Float ageFloat = Float.parseFloat(this.getAge());
          if (ageFloat>=50f) {
            return Value.Yes;
          }
          else {
            return Value.No;
          }
        } catch (NumberFormatException ex) {
          return Value.Unknown;
        }
      }
      else {
        return Value.Unknown;
      }
    }
  }

  public Value passInclusion2a() {
    if ((StringUtils.equals(getMetastatic(), "0") && isValidTumorDimension()) && getDcisStatus()!=Value.Yes) {
      return Value.Yes;
    }
    else {
      return Value.No;
    }
  }

  public Value passInclusion2b() {
    if ((ItpcUtils.isBlank(getPriorHistory()) || this.getPriorHistory().equals("0"))
        && (ItpcUtils.isBlank(getPriorDcis()) || !this.getPriorDcis().equals("1"))) {
      return Value.Yes;
    }
    else {
      return Value.No;
    }
  }

  public Value passInclusion3() {
    if (this.getErStatus() != null && this.getErStatus().equals("1")) {
      return Value.Yes;
    }
    else {
      return Value.No;
    }
  }

  public Value passInclusion4() {
    if (ItpcUtils.isBlank(getSystemicTher()) || !getSystemicTher().equals("1")) {
      return Value.Yes;
    }
    else {
      return Value.No;
    }
  }

  public Value passInclusion4a() {
    try {
      Integer daysBetween = Integer.parseInt(this.getTimeBtwSurgTamox());
      if (daysBetween<182 && this.getFirstAdjEndoTher().equals("1")) {
        return Value.Yes;
      }
      else {
        return Value.No;
      }
    }
    catch (NumberFormatException ex) {
      if (!ItpcUtils.isBlank(this.getFirstAdjEndoTher()) && !ItpcUtils.isBlank(this.getTimeBtwSurgTamox())) {
        if (this.getFirstAdjEndoTher().equals("1")
            && (this.getTimeBtwSurgTamox().equalsIgnoreCase("< 6 weeks")
            || this.getTimeBtwSurgTamox().equalsIgnoreCase("< 6 months")
            || this.getTimeBtwSurgTamox().equalsIgnoreCase("< 90 days")
            || this.getTimeBtwSurgTamox().equalsIgnoreCase("28-42"))) {
          return Value.Yes;
        }
        else {
          return Value.No;
        }
      }
      else {
        return Value.No;
      }
    }
  }

  public Value passInclusion4b() {
    if (this.getDuration() != null && this.getDuration().equals("0")) {
      return Value.Yes;
    }
    else {
      return Value.No;
    }
  }

  public Value passInclusion4c() {
    if (this.getTamoxDose() != null && this.getTamoxDose().equals("0")) {
      return Value.Yes;
    }
    else {
      return Value.No;
    }
  }

  public Value passInclusion5() {
    if (getChemotherapy() != null && getChemotherapy().equals("0")) {
      return Value.Yes;
    }
    else {
      return Value.No;
    }
  }

  public Value passInclusion6() {
    if (this.getHormoneTherapy() == null || !this.getHormoneTherapy().equals("1")) {
      return Value.Yes;
    }
    else {
      return Value.No;
    }
  }

  public Value passInclusion7() {
    Value inclusion = Value.No;

    List<String> passingBloodTimings = Arrays.asList("1","2","7");
    List<String> passingTumorTimings = Arrays.asList("1");

    if (getSampleSources().isEmpty() || (getSampleSources().size()==1 && getSampleSources().contains(SampleSource.UNKNOWN))) {
      return inclusion;
    }

    if (getSampleSources().contains(SampleSource.BLOOD) || getSampleSources().contains(SampleSource.BUCCAL)) {
      if (passingBloodTimings.contains(getBloodSource())) {
        inclusion = Value.Yes;
      }
    }
    if (getSampleSources().contains(SampleSource.TUMOR_FFP)
            || getSampleSources().contains(SampleSource.TUMOR_FROZEN)
            || getSampleSources().contains(SampleSource.NORMAL_PARAFFIN)) {
      if (passingTumorTimings.contains(getTumorSource())) {
        inclusion = Value.Yes;
      }
    }
    return inclusion;
  }

  public Value passInclusion8() {
    if (this.getFollowup() == null || !this.getFollowup().equals("2")) {
      return Value.Yes;
    }
    else {
      return Value.No;
    }
  }

  public Value passInclusion9() {
    if (!this.getGenotypeFinal().isUncertain()) {
      return Value.Yes;
    }
    else {
      return Value.No;
    }
  }

  public Value include() {
    if (passInclusion1() == Value.Yes
        && passInclusion2a() == Value.Yes
        && passInclusion2b() == Value.Yes
        && passInclusion3() == Value.Yes
        && passInclusion4() == Value.Yes
        && passInclusion4a() == Value.Yes
        && passInclusion4b() == Value.Yes
        && passInclusion4c() == Value.Yes
        && passInclusion5() == Value.Yes
        && passInclusion6() == Value.Yes
        && passInclusion7() == Value.Yes
        && passInclusion8() == Value.Yes
        && passInclusion9() == Value.Yes) {
      return Value.Yes;
    }
    else {
      return Value.No;
    }
  }

  public Value includeCrit1() {
    if (passInclusion1() == Value.Yes
        && passInclusion2a() == Value.Yes
        && passInclusion3() == Value.Yes
        && passInclusion4b() == Value.Yes
        && passInclusion4c() == Value.Yes
        && passInclusion5() == Value.Yes
        && passInclusion6() == Value.Yes
        && passInclusion8() == Value.Yes
        && passInclusion9() == Value.Yes
        && excludeSummary()==Value.No) {
      return Value.Yes;
    }
    else {
      return Value.No;
    }
  }

  public Value includeCrit2() {
    if (passInclusion2a() == Value.Yes
        && passInclusion3() == Value.Yes
        && passInclusion4c() == Value.Yes
        && passInclusion5() == Value.Yes
        && passInclusion6() == Value.Yes
        && passInclusion9() == Value.Yes
        && excludeSummary()==Value.No) {
      return Value.Yes;
    }
    else {
      return Value.No;
    }
  }

  public Value includeCrit3() {
    if (excludeSummary()==Value.No) {
      return Value.Yes;
    }
    else {
      return Value.No;
    }
  }

  /**
   * Same as include() but disregards inclusion 4a.  This is used for analysis to see if 4a is causing many records to
   * be removed.
   * @return Value of whether the subject should be included
   */
  public Value includeWo4a() {
    if (passInclusion1() == Value.Yes
        && passInclusion2a() == Value.Yes
        && passInclusion2b() == Value.Yes
        && passInclusion3() == Value.Yes
        && passInclusion4() == Value.Yes
        && passInclusion4b() == Value.Yes
        && passInclusion4c() == Value.Yes
        && passInclusion5() == Value.Yes
        && passInclusion6() == Value.Yes
        && passInclusion7() == Value.Yes
        && passInclusion8() == Value.Yes
        && passInclusion9() == Value.Yes) {
      return Value.Yes;
    }
    else {
      return Value.No;
    }
  }

  public Value exclude1() {
    if (getAdditionalCancer() == Value.Yes
        && (ItpcUtils.isBlank(getAddCxIpsilateral()) || getAddCxIpsilateral().equals("0"))
        && (ItpcUtils.isBlank(getAddCxDistantRecur()) || getAddCxDistantRecur().equals("0"))
        && (ItpcUtils.isBlank(getAddCxContralateral()) || getAddCxContralateral().equals("0"))
        && (ItpcUtils.isBlank(getAddCxSecondInvasive()) || getAddCxSecondInvasive().equals("0"))
        ) {
      return Value.Yes;
    }
    else if ((getAdditionalCancer() == Value.No || getAdditionalCancer() == Value.Unknown)
        && (!(ItpcUtils.isBlank(getAddCxIpsilateral()) || getAddCxIpsilateral().equals("0"))
            || !(ItpcUtils.isBlank(getAddCxDistantRecur()) || getAddCxDistantRecur().equals("0"))
            || !(ItpcUtils.isBlank(getAddCxContralateral()) || getAddCxContralateral().equals("0"))
            || !(ItpcUtils.isBlank(getAddCxSecondInvasive()) || getAddCxSecondInvasive().equals("0")))
      ) {
      return Value.Yes;
    }
    else {
      return Value.No;
    }
  }

  // joan suggestion 1
  public Value exclude4() {
    if ((getAdditionalCancer()==Value.Yes || getPatientDied()==Value.Yes) && !ItpcUtils.isBlank(getDiseaseFreeSurvivalTime())) {
      return Value.Yes;
    }
    return Value.No;
  }

  // joan suggestion 3
  public Value exclude5() {
    if (getPatientDied()==Value.Yes && !ItpcUtils.isBlank(getSurvivalNotDied())) {
      return Value.Yes;
    }
    return Value.No;
  }

  // joan suggestion 5
  public Value exclude6() {
    Integer dfst = parseDays(getDiseaseFreeSurvivalTime());
    Integer lde = parseDays(getAddCxLastEval());
    Integer snd = parseDays(getSurvivalNotDied());

    if (dfst != null && lde != null && dfst<lde) {
      return Value.Yes;
    }

    if (lde != null && snd != null && lde>snd) {
      return Value.Yes;
    }

    return Value.No;
  }

  public Value excludeSummary() {
    if (exclude1() == Value.Yes
        || exclude4() == Value.Yes
        || exclude6() == Value.Yes
        || exclude5() == Value.Yes) {
      return Value.Yes;
    }
    else {
      return Value.No;
    }
  }

  public String getMenoStatus() {
    return m_menoStatus;
  }

  public void setMenoStatus(String menoStatus) {
    m_menoStatus = menoStatus;
  }

  public String getSubjectId() {
    return m_subjectId;
  }

  public void setSubjectId(String subjectId) {
    m_subjectId = subjectId;
  }

  public String getProjectSite() {
    return m_projectSite;
  }

  public void setProjectSite(String projectSite) {
    m_projectSite = projectSite;
  }

  public String getAge() {
    return m_age;
  }

  public void setAge(String age) {
    m_age = age;
  }

  public String getGender() {
    return m_gender;
  }

  public void setGender(String gender) {
    m_gender = gender;
  }

  public String getMetastatic() {
    return m_metastatic;
  }

  public void setMetastatic(String metastatic) {
    m_metastatic = metastatic;
  }

  public String getPriorHistory() {
    return m_priorHistory;
  }

  public void setPriorHistory(String priorHistory) {
    m_priorHistory = priorHistory;
  }

  public String getPriorDcis() {
    return m_priorDcis;
  }

  public void setPriorDcis(String priorDcis) {
    m_priorDcis = priorDcis;
  }

  public String getErStatus() {
    return m_erStatus;
  }

  public void setErStatus(String erStatus) {
    m_erStatus = erStatus;
  }

  public String getSystemicTher() {
    return m_systemicTher;
  }

  public void setSystemicTher(String systemicTher) {
    m_systemicTher = systemicTher;
  }

  public String getTimeBtwSurgTamox() {
    return m_timeBtwSurgTamox;
  }

  public void setTimeBtwSurgTamox(String timeBtwSurgTamox) {
    m_timeBtwSurgTamox = timeBtwSurgTamox;
  }

  public String getFirstAdjEndoTher() {
    return m_firstAdjEndoTher;
  }

  public void setFirstAdjEndoTher(String firstAdjEndoTher) {
    m_firstAdjEndoTher = firstAdjEndoTher;
  }

  public String getDuration() {
    return m_duration;
  }

  public void setDuration(String duration) {
    m_duration = duration;
  }

  public String getTamoxDose() {
    return m_tamoxDose;
  }

  public void setTamoxDose(String tamoxDose) {
    m_tamoxDose = tamoxDose;
  }

  public String getChemotherapy() {
    return m_chemotherapy;
  }

  public void setChemotherapy(String chemotherapy) {
    m_chemotherapy = chemotherapy;
  }

  public String getHormoneTherapy() {
    return m_hormoneTherapy;
  }

  public void setHormoneTherapy(String hormoneTherapy) {
    m_hormoneTherapy = hormoneTherapy;
  }

  public String getTumorSource() {
    return m_tumorSource;
  }

  public void setTumorSource(String tumorSource) {
    m_tumorSource = tumorSource;
  }

  public String getBloodSource() {
    return m_bloodSource;
  }

  public void setBloodSource(String bloodSource) {
    m_bloodSource = StringUtils.replace(bloodSource,"*","");
  }

  public String getFollowup() {
    return m_followup;
  }

  public void setFollowup(String followup) {
    m_followup = followup;
  }

  public Genotype getGenotypeLimited() {
    return m_genotypeLimited;
  }

  public void setGenotypeLimited(Genotype genotypeLimited) {
    m_genotypeLimited = genotypeLimited;
  }

  public String getRace() {
    return m_race;
  }

  public void setRace(String race) {
    m_race = race;
  }

  public String getTumorDimension() {
    return m_tumorDimension;
  }

  public void setTumorDimension(String tumorDimension) {
    m_tumorDimension = tumorDimension;
  }

  protected boolean isValidTumorDimension() {
    boolean valid = true;

    if (StringUtils.trimToNull(getTumorDimension()) != null) {
      String tumorDimension = getTumorDimension().toLowerCase();

      valid = !(tumorDimension.contains("dcis")
          || tumorDimension.contains("lcis")
          || tumorDimension.contains("atypical lobular hyperplasia")
          || tumorDimension.contains("taking for high risk"));
    }

    return valid;
  }

  public Value getAdditionalCancer() {
    return m_additionalCancer;
  }

  public void setAdditionalCancer(Value additionalCancer) {
    m_additionalCancer = additionalCancer;
  }

  public void setAdditionalCancer(String additionalCancer) {
    if (additionalCancer == null) {
      m_additionalCancer = Value.Unknown;
    }
    else if (additionalCancer.equals("1")) {
      m_additionalCancer = Value.Yes;
    }
    else if (additionalCancer.equals("2")) {
      m_additionalCancer = Value.No;
    }
    else {
      m_additionalCancer = Value.Unknown;
    }
  }

  public String getAddCxIpsilateral() {
    return m_addCxIpsilateral;
  }

  public void setAddCxIpsilateral(String addCxIpsilateral) {
    m_addCxIpsilateral = addCxIpsilateral;
  }

  public String getAddCxDistantRecur() {
    return m_addCxDistantRecur;
  }

  public void setAddCxDistantRecur(String addCxDistantRecur) {
    m_addCxDistantRecur = addCxDistantRecur;
  }

  public String getAddCxContralateral() {
    return m_addCxContralateral;
  }

  public void setAddCxContralateral(String addCxContralateral) {
    m_addCxContralateral = addCxContralateral;
  }

  public String getAddCxSecondInvasive() {
    return m_addCxSecondInvasive;
  }

  public void setAddCxSecondInvasive(String addCxSecondInvasive) {
    m_addCxSecondInvasive = addCxSecondInvasive;
  }

  public String getAddCxLastEval() {
    return m_addCxLastEval;
  }

  public void setAddCxLastEval(String addCxLastEval) {
    m_addCxLastEval = addCxLastEval;
  }

  public String getFirstDiseaseEventCalc() {
    return getFirstEventData()[0];
  }

  public String getDiagToEventDaysCalc() {
    return getFirstEventData()[1];
  }

  /**
   * Returns information ont eh first disease event in a String array
   * @return String array: element 0: code of the first disease event, element 1: days to first disease event
   */
  public String[] getFirstEventData() {
    String code = "0";
    
    if (getAdditionalCancer()==Value.Yes) {
      List<String> eventCodes = Lists.newArrayList();

      int days = 999999;
      if (!isInvasive(getAddCxContralateral()) || !(ItpcUtils.isBlank(getAddCxContralateral()) || getAddCxContralateral().equals("0"))) {
        eventCodes.add("3");
        Integer contraDays = parseDays(getAddCxContralateral());
        if (contraDays != null && contraDays<days) {
          days = contraDays;
          code = "3";
        }
      }
      if (!(ItpcUtils.isBlank(getAddCxDistantRecur()) || getAddCxDistantRecur().equals("0"))) {
        eventCodes.add("2");
        Integer distantDays = parseDays(getAddCxDistantRecur());
        if (distantDays != null && distantDays<days) {
          days = distantDays;
          code = "2";
        }
      }
      if (!isInvasive(getAddCxIpsilateral()) || !(ItpcUtils.isBlank(getAddCxIpsilateral()) || getAddCxIpsilateral().equals("0"))) {
        eventCodes.add("1");
        Integer ipsiDays = parseDays(getAddCxIpsilateral());
        if (ipsiDays != null && ipsiDays<days) {
          days = ipsiDays;
          code = "1";
        }
      }
      if (!(ItpcUtils.isBlank(getAddCxSecondInvasive()) || getAddCxSecondInvasive().equals("0"))) {
        eventCodes.add("4");
        Integer secondDays = parseDays(getAddCxSecondInvasive());
        if (secondDays != null && secondDays<days) {
          days = secondDays;
          code = "4";
        }
      }
      if (days<999999 && days>0) {
        return new String[]{code, Integer.toString(days)};
      }
      else {
        String events = eventCodes.isEmpty() ? Value.Unknown.toString() : Joiner.on("; ").join(eventCodes);
        return new String[]{events, Value.Unknown.toString()};
      }
    }
    else if (getAdditionalCancer()==Value.No && getPatientDied()==Value.Yes) {
      Integer deathDays = parseDays(getDaysDiagtoDeath());
      if (deathDays != null && deathDays>0) {
        return new String[]{"5", String.valueOf(deathDays)};
      }
    }
    else if (getAdditionalCancer()==Value.No && getPatientDied()==Value.No) {
      Integer days = parseDays(getAddCxLastEval());
      if (days != null && days>0) {
        return new String[]{"0", String.valueOf(days)};
      }
      else {
        return new String[]{"0", Value.Unknown.toString()};
      }
    }
    return new String[]{Value.Unknown.toString(), Value.Unknown.toString()};
  }

  private Integer parseDays(String daysString) {
    Integer daysInt = null;

    if (!ItpcUtils.isBlank(daysString)) {
      String workingString = daysString;
      workingString = workingString.replaceAll(";","");
      workingString = workingString.replaceAll("NI","");
      workingString = StringUtils.trim(workingString);

      try {
        daysInt = Integer.valueOf(workingString);
      } catch (NumberFormatException ex) {
        return -1;
      }
    }

    return daysInt;
  }

  public String getDaysDiagtoDeath() {
    return m_daysDiagtoDeath;
  }

  public void setDaysDiagtoDeath(String daysDiagtoDeath) {
    m_daysDiagtoDeath = daysDiagtoDeath;
  }

  public Value getPatientDied() {
    return m_patientDied;
  }

  public void setPatientDied(Value patientDied) {
    m_patientDied = patientDied;
  }

  public void setPatientDied(String died) {
    String patientDied = died.trim();
    if (patientDied == null) {
      m_patientDied = Value.Unknown;
    }
    else if (patientDied.equals("2")) {
      m_patientDied = Value.Yes;
    }
    else if (patientDied.equals("1")) {
      m_patientDied = Value.No;
    }
    else {
      m_patientDied = Value.Unknown;
    }
  }

  public String getDiseaseFreeSurvivalTime() {
    return m_diseaseFreeSurvivalTime;
  }

  public void setDiseaseFreeSurvivalTime(String diseaseFreeSurvivalTime) {
    m_diseaseFreeSurvivalTime = diseaseFreeSurvivalTime;
  }

  public String getSurvivalNotDied() {
    return m_survivalNotDied;
  }

  public void setSurvivalNotDied(String survivalNotDied) {
    m_survivalNotDied = survivalNotDied;
  }

  public void addMedStatus(Med med, Value value) {
    m_medStatus.put(med, value);
  }

  public Value hasMed(Med med) {
    if (med != null && m_medStatus.keySet().contains(med)) {
      return m_medStatus.get(med);
    }
    else {
      return Value.Unknown;
    }
  }

  public boolean isInvasive(String days) {
    return days != null && !days.contains("NI");
  }

  public String getBreastCancerFreeInterval() {
    SortedSet<Integer> freeIntervals = Sets.newTreeSet();

    if (getCauseOfDeath()!=null && getCauseOfDeath().equals("1")) {
      freeIntervals.add(parseDays(getDaysDiagtoDeath()));
    }

    if (!ItpcUtils.isBlank(getAddCxIpsilateral())) {
      Integer ipsiDays = parseDays(getAddCxIpsilateral());
      if (ipsiDays>0) {
        freeIntervals.add(ipsiDays);
      }
    }
    if (!ItpcUtils.isBlank(getAddCxDistantRecur())) {
      Integer days = parseDays(getAddCxDistantRecur());
      if (days>0) {
        freeIntervals.add(days);
      }
    }
    if (!ItpcUtils.isBlank(getAddCxContralateral())) {
      Integer days = parseDays(getAddCxContralateral());
      if (days>0) {
        freeIntervals.add(days);
      }
    }

    if (!freeIntervals.isEmpty()) {
      return Integer.toString(freeIntervals.first());
    }
    else {
      return "";
    }
  }

  public String getCauseOfDeath() {
    return m_causeOfDeath;
  }

  public void setCauseOfDeath(String causeOfDeath) {
    m_causeOfDeath = causeOfDeath;
  }

  public Value getDcisStatus() {
    return m_dcisStatus;
  }

  public void setDcisStatus(Value dcisStatus) {
    m_dcisStatus = dcisStatus;
  }

  public SampleSource getSampleSource() {
    if ((ItpcUtils.isBlank(getTumorSource()) || getTumorSource().equals("3")) && (ItpcUtils.isBlank(getBloodSource()) || getBloodSource().equals("3"))) {
      return SampleSource.UNKNOWN;
    }
    else if (!(ItpcUtils.isBlank(getTumorSource()) || getTumorSource().equals("3")) && (ItpcUtils.isBlank(getBloodSource()) || getBloodSource().equals("3"))) {
      return SampleSource.TUMOR_FFP;
    }
    else if ((ItpcUtils.isBlank(getTumorSource()) || getTumorSource().equals("3")) && !(ItpcUtils.isBlank(getBloodSource()) || getBloodSource().equals("3"))) {
      return SampleSource.BLOOD;
    }
    else {
      return SampleSource.UNKNOWN;
    }
  }

  public String makeSqlInsert() {
    String insertStmt = "insert into tamoxdata(subjectid," +
            "projectid," +
            "ageatdiagnosis," +
            "menostatusatdx," +
            "maxtumordim," +
            "numposnodes," +
            "grading," +
            "erstatus," +
            "pgrstatus," +
            "radiotherapy," +
            "cyp2d6_1," +
            "cyp2d6_2," +
            "crit1," +
            "crit2," +
            "crit3," +
            "genosource) values (%s);";
    List<String> fields = Lists.newArrayList();

    fields.add("'"+getSubjectId()+"'");
    fields.add("'"+getProjectSite()+"'");
    fields.add("'"+getAge()+"'");
    fields.add("'"+getMenoStatus()+"'");
    fields.add("'"+getTumorDimension()+"'");
    fields.add("'"+getNumPositiveNodes()+"'");
    fields.add("'"+getTumorGrading()+"'");
    fields.add("'"+getErStatus()+"'");
    fields.add("'"+getProgesteroneReceptor()+"'");
    fields.add("'"+getRadiotherapy()+"'");
    fields.add("'"+getGenotypeFinal().get(0)+"'");
    fields.add("'"+getGenotypeFinal().get(1)+"'");
    fields.add("'"+includeCrit1()+"'");
    fields.add("'"+includeCrit2()+"'");
    fields.add("'"+includeCrit3()+"'");
    fields.add("'"+Joiner.on(",").join(getSampleSources())+"'");

    return String.format(insertStmt, Joiner.on(",").join(fields));
  }

  public String getNumPositiveNodes() {
    return m_numPositiveNodes;
  }

  public void setNumPositiveNodes(String m_numPositiveNodes) {
    this.m_numPositiveNodes = m_numPositiveNodes;
  }

  public String getTumorGrading() {
    return m_tumorGrading;
  }

  public void setTumorGrading(String m_tumorGrading) {
    this.m_tumorGrading = m_tumorGrading;
  }

  public String getProgesteroneReceptor() {
    return progesteroneReceptor;
  }

  public void setProgesteroneReceptor(String progesteroneReceptor) {
    this.progesteroneReceptor = progesteroneReceptor;
  }

  public String getRadiotherapy() {
    return radiotherapy;
  }

  public void setRadiotherapy(String radiotherapy) {
    this.radiotherapy = radiotherapy;
  }

  public Set<SampleSource> getSampleSources() {
    return m_sampleSources;
  }

  public void addSampleSource(SampleSource sampleSource) {
    m_sampleSources.add(sampleSource);
  }

  enum Deletion {Unknown, None, Hetero, Homo}

  public enum SampleSource {TUMOR_FFP, BLOOD, BUCCAL, TUMOR_FROZEN, NORMAL_PARAFFIN, UNKNOWN}
}
