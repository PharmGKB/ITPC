import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ryan Whaley
 * Date: Jul 13, 2010
 * Time: 11:16:54 PM
 */
public class Genotype extends StringPair {
  public enum Metabolizer {Unknown, PM, IM, EM, UM}

  private static final Map<String,Metabolizer> metabMap = new HashMap<String,Metabolizer>();
  static {
    metabMap.put("*3",Metabolizer.PM);
    metabMap.put("*4",Metabolizer.PM);
    metabMap.put("*5",Metabolizer.PM);
    metabMap.put("*6",Metabolizer.PM);
    metabMap.put("*7",Metabolizer.PM);
    metabMap.put("*8",Metabolizer.PM);
    metabMap.put("*11",Metabolizer.PM);
    metabMap.put("*12",Metabolizer.PM);
    metabMap.put("*13",Metabolizer.PM);
    metabMap.put("*14",Metabolizer.PM);
    metabMap.put("*15",Metabolizer.PM);
    metabMap.put("*16",Metabolizer.PM);
    metabMap.put("*18",Metabolizer.PM);
    metabMap.put("*19",Metabolizer.PM);
    metabMap.put("*20",Metabolizer.PM);
    metabMap.put("*40",Metabolizer.PM);
    metabMap.put("*42",Metabolizer.PM);
    metabMap.put("*44",Metabolizer.PM);
    metabMap.put("*56",Metabolizer.PM);
    metabMap.put("*36",Metabolizer.PM);
    metabMap.put("*38",Metabolizer.PM);
    metabMap.put("*4XN",Metabolizer.PM);

    metabMap.put("*9",Metabolizer.IM);
    metabMap.put("*10",Metabolizer.IM);
    metabMap.put("*17",Metabolizer.IM);
    metabMap.put("*29",Metabolizer.IM);
    metabMap.put("*37",Metabolizer.IM);
    metabMap.put("*41",Metabolizer.IM);
    metabMap.put("*45",Metabolizer.IM);
    metabMap.put("*46",Metabolizer.IM);

    metabMap.put("*1",Metabolizer.EM);
    metabMap.put("*2",Metabolizer.EM);
    metabMap.put("*33",Metabolizer.EM);
    metabMap.put("*35",Metabolizer.EM);
    metabMap.put("*39",Metabolizer.EM);
    metabMap.put("*43",Metabolizer.EM);

    metabMap.put("*1XN", Metabolizer.UM);
    metabMap.put("*2XN", Metabolizer.UM);
    metabMap.put("*9XN", Metabolizer.UM);
    metabMap.put("*10XN", Metabolizer.UM);
    metabMap.put("*35XN", Metabolizer.UM);
    metabMap.put("*39XN", Metabolizer.UM);
    metabMap.put("*41XN", Metabolizer.UM);
    metabMap.put("*45XN", Metabolizer.UM);
  }

  private static final Map<Metabolizer,Float> scoreMap = new HashMap<Metabolizer,Float>();
  static {
    scoreMap.put(Metabolizer.PM, 0f);
    scoreMap.put(Metabolizer.IM, 0.5f);
    scoreMap.put(Metabolizer.EM, 1f);
    scoreMap.put(Metabolizer.UM, 2f);
  }

  private static final Map<Metabolizer,Integer> priorityMap = new HashMap<Metabolizer,Integer>();
  static {
    priorityMap.put(Metabolizer.PM,1);
    priorityMap.put(Metabolizer.IM,2);
    priorityMap.put(Metabolizer.EM,3);
    priorityMap.put(Metabolizer.UM,4);
    priorityMap.put(Metabolizer.Unknown, 5);
  }

  public Genotype() {}

  public Genotype(String string) {
    if (string != null && string.contains("/")) {
      String[] tokens = string.split("/");
      for (String token : tokens) {
        this.addString(token);
      }
    }
  }

  public Genotype(String s1, String s2) {
    this.addString(s1);
    this.addString(s2);
  }

  public boolean isValid(String string) {
    return metabMap.keySet().contains(string) || string.equals("Unknown");
  }

  public Float getScore() {
    Float score = null;

    if (!this.isUncertain()) {
      score = 0f;
      for (String allele : this.getStrings()) {
        score += scoreMap.get(metabMap.get(allele));
      }
    }

    return score;
  }

  /**
   * Adds an allele to the List of alleles, taking into consideration the prioritization found
   * in the genoPriority Map for this class
   * @param string a new String allele to add to the List
   */
  public void addString(String string) {
    if (!isValid(string)) {
      return;
    }

    if (this.getStrings().isEmpty() || this.getStrings().size()==1) {
      super.addString(string);
    }
    else {
      String removeAllele = null;
      for (String existingAllele : this.getStrings()) {
        // if the new allele has a higher priority (lower number) than an existing one, replace it
        if ((priority(string) < priority(existingAllele))
            || (existingAllele.equals("*1") && priority(existingAllele)==(priority(string)))
            ) {
          removeAllele = existingAllele;
        }
      }
      if (!StringUtils.isBlank(removeAllele)) {
        removeString(removeAllele);
        super.addString(string);
      }
    }
  }

  protected static String getText(Metabolizer value) {
    switch (value) {
      case IM: return "IM";
      case PM: return "PM";
      case EM: return "EM";
      case UM: return "UM";
      default: return "Unknown";
    }
  }

  public String getMetabolizerStatus() {
    if (this.getStrings().isEmpty()) {
      return getText(Metabolizer.Unknown);
    }

    List<String> genotypes = new ArrayList<String>();
    for (String allele : this.getStrings()) {
      genotypes.add(getText(metabMap.get(allele)));
    }

    StringBuilder genoBuilder = new StringBuilder();
    Collections.sort(genotypes, String.CASE_INSENSITIVE_ORDER);
    for (int x = 0; x < genotypes.size(); x++) {
      genoBuilder.append(genotypes.get(x));
      if (x != genotypes.size() - 1) {
        genoBuilder.append("/");
      }
    }
    return genoBuilder.toString();
  }

  public boolean isHeteroDeletion() {
    return this.getStrings().contains("*5");
  }

  public boolean isHomoDeletion() {
    return this.is("*5","*5");
  }

  private float priority(String allele) {
    return priorityMap.get(metabMap.get(allele));
  }
}
