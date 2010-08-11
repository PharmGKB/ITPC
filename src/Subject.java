import org.apache.log4j.Logger;
import util.ItpcUtils;
import util.Value;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: whaleyr
 * Date: Jul 14, 2010
 * Time: 8:16:01 AM
 */
public class Subject {
  private static final Logger logger = Logger.getLogger(Subject.class);
  private static final Pattern sf_alleleRegex = Pattern.compile("\\*\\d+");

  private String m_subjectId = null;
  private String m_projectSite = null;
  private String m_age = null;
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
  private String m_genoSource = null;
  private String m_curatorComment = null;
  private Deletion m_deletion = Deletion.Unknown;

  private Value m_hasParoxetine = Value.Unknown;
  private Value m_hasFluoxetine = Value.Unknown;
  private Value m_hasQuinidine = Value.Unknown;
  private Value m_hasBuproprion = Value.Unknown;
  private Value m_hasDuloxetine = Value.Unknown;

  private Value m_hasCimetidine = Value.Unknown;
  private Value m_hasSertraline = Value.Unknown;
  private Value m_hasCitalopram = Value.Unknown;

  private Genotype m_genotypePgkb = new Genotype();
  private Genotype m_genotypeAmplichip = new Genotype();

  private VariantAlleles m_rs1065852 = new VariantAlleles();
  private VariantAlleles m_rs4986774 = new VariantAlleles();
  private VariantAlleles m_rs3892097 = new VariantAlleles();
  private VariantAlleles m_rs5030655 = new VariantAlleles();
  private VariantAlleles m_rs16947 = new VariantAlleles();
  private VariantAlleles m_rs28371706 = new VariantAlleles();
  private VariantAlleles m_rs28371725 = new VariantAlleles();

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

  public Genotype getGenotypeFinal() {
    if (m_genotypeAmplichip != null && m_genotypeAmplichip.hasData()) {
      return m_genotypeAmplichip;
    }
    else {
      return m_genotypePgkb;
    }
  }

  public Value getWeak() {
    if (this.hasCimetidine() == Value.Yes
        || this.hasSertraline() == Value.Yes
        || this.hasCitalopram() == Value.Yes) {
      return Value.Yes;
    }
    else if (this.hasCimetidine() == Value.No
        && this.hasSertraline() == Value.No
        && this.hasCitalopram() == Value.No) {
      return Value.No;
    }
    else {
      return Value.Unknown;
    }
  }

  public Value getPotent() {
    if (this.hasParoxetine() == Value.Yes
        || this.hasFluoxetine() == Value.Yes
        || this.hasQuinidine() == Value.Yes
        || this.hasBuproprion() == Value.Yes
        || this.hasDuloxetine() == Value.Yes) {
      return Value.Yes;
    }
    else if (this.hasParoxetine() == Value.No
        && this.hasFluoxetine() == Value.No
        && this.hasQuinidine() == Value.No
        && this.hasBuproprion() == Value.No
        && this.hasDuloxetine() == Value.No) {
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
    Float score = null;

    if (getPotent()==Value.Yes) {
      score = 0f;
    }
    else if (!getGenotypeFinal().isUncertain()
             && ((getWeak()!=Value.Unknown && getPotent()!=Value.Unknown)
                 || getGenotypeFinal().getMetabolizerStatus().equals("PM/PM"))) 
    {
      Float genoScore = getGenotypeFinal().getScore();
      Float weakPenalty = getWeak()==Value.Yes ? -0.5f : 0f;

      score = genoScore + weakPenalty;

      if (score<0f) {score=0f;}
    }

    return score;
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

  public Value hasParoxetine() {
    return m_hasParoxetine;
  }

  public void setHasParoxetine(Value hasParoxetine) {
    m_hasParoxetine = hasParoxetine;
  }

  public Value hasFluoxetine() {
    return m_hasFluoxetine;
  }

  public void setHasFluoxetine(Value hasFluoxetine) {
    m_hasFluoxetine = hasFluoxetine;
  }

  public Value hasQuinidine() {
    return m_hasQuinidine;
  }

  public void setHasQuinidine(Value hasQuinidine) {
    m_hasQuinidine = hasQuinidine;
  }

  public Value hasBuproprion() {
    return m_hasBuproprion;
  }

  public void setHasBuproprion(Value hasBuproprion) {
    m_hasBuproprion = hasBuproprion;
  }

  public Value hasDuloxetine() {
    return m_hasDuloxetine;
  }

  public void setHasDuloxetine(Value hasDuloxetine) {
    m_hasDuloxetine = hasDuloxetine;
  }

  public Value hasCimetidine() {
    return m_hasCimetidine;
  }

  public void setHasCimetidine(Value hasCimetidine) {
    m_hasCimetidine = hasCimetidine;
  }

  public Value hasSertraline() {
    return m_hasSertraline;
  }

  public void setHasSertraline(Value hasSertraline) {
    m_hasSertraline = hasSertraline;
  }

  public Value hasCitalopram() {
    return m_hasCitalopram;
  }

  public void setHasCitalopram(Value hasCitalopram) {
    m_hasCitalopram = hasCitalopram;
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
        }
        if (this.getRs3892097().count("a")==2 && !geno.isHeteroDeletion()) {
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
        if (this.getRs16947().hasData() && this.getRs28371706().count("c")>0 && this.getRs28371725().count("g")>0 && this.getRs1065852().hasData()) {
          if (this.getRs16947().count("t")>0 && this.getRs28371706().count("t")<2 && this.getRs28371725().count("a")<2) {
            geno.addString("*2");
            if (!geno.isHeteroDeletion() &&
                (this.getRs16947().is("t","t") && this.getRs28371706().is("c","c") && this.getRs28371725().is("g","g"))) {
              geno.addString("*2");
            }
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
          (this.getRs28371706().hasData() || (!this.getRs28371706().hasData() && (this.getRs16947().is("C","C") || this.getRs16947().is("C","-")))) &&
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

  public Deletion getDeletion() {
    return m_deletion;
  }

  public void setDeletion(Deletion deletion) {
    m_deletion = deletion;
    this.calculateGenotypePgkb();
  }

  public void setDeletion(String deletion) {
    if (deletion == null) {
      this.setDeletion(Deletion.Unknown);
    }
    else {
      deletion = deletion.toLowerCase();

      if (deletion.equalsIgnoreCase("homozygous deletion")) {
        this.setDeletion(Deletion.Homo);
      }
      else if (deletion.contains("deletion") && !deletion.contains("no deletion")) {
        this.setDeletion(Deletion.Hetero);
      }
      else if (deletion.equalsIgnoreCase("NA")) {
        this.setDeletion(Deletion.Unknown);
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
          genotype.addString(alleleStrip(token));
        }
        catch (Exception ex) {
          throw new Exception("Error processing amplichip: " + amplichip, ex);
        }
      }
    }

    return genotype;
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

  public Value passInclusion1() {
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
    if (this.getMetastatic() != null && this.getMetastatic().equals("0")) {
      return Value.Yes;
    }
    else if (this.getMetastatic() != null && this.getMetastatic().equals("1")) {
      return Value.No;
    }
    else {
      return Value.Unknown;
    }
  }

  public Value passInclusion2b() {
    if ((this.getPriorHistory() == null || this.getPriorHistory().equals("0"))
        && (this.getPriorDcis() == null || !this.getPriorDcis().equals("1"))) {
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
    if (!ItpcUtils.isBlank(this.getSystemicTher()) && this.getSystemicTher().equals("2")) {
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
    if (this.getChemotherapy() == null || !this.getChemotherapy().equals("1")) {
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
    if (this.getGenoSource() != null && (
        (this.getTumorSource()!=null && (this.getGenoSource().equals("0") || this.getGenoSource().equals("3") || this.getGenoSource().equals("4")) && this.getTumorSource().equals("1"))
        || ((this.getGenoSource().equals("1") || this.getGenoSource().equals("2")) && this.getBloodSource()!=null && (this.getBloodSource().equals("1") || this.getBloodSource().equals("2") || this.getBloodSource().equals("7")))
        )
        ) {
      return Value.Yes;
    }
    else {
      return Value.No;
    }
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
    m_bloodSource = bloodSource;
  }

  public String getGenoSource() {
    return m_genoSource;
  }

  public void setGenoSource(String genoSource) {
    m_genoSource = genoSource;
  }

  public String getFollowup() {
    return m_followup;
  }

  public void setFollowup(String followup) {
    m_followup = followup;
  }

  public String getCuratorComment() {
    return m_curatorComment;
  }

  public void setCuratorComment(String curatorComment) {
    m_curatorComment = curatorComment;
  }

  enum Deletion {Unknown, None, Hetero, Homo}
}
