package util;

import org.pharmgkb.Genotype;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: whaleyr
 * Date: 6/27/12
 */
public class GenotypeComparator implements Comparator<Genotype> {
  private static final Comparator<Genotype> sf_comparator = new GenotypeComparator();

  public static Comparator<Genotype> getComparator() {
    return sf_comparator;
  }

  @Override
  public int compare(Genotype o1, Genotype o2) {
    if (o1==o2) {
      return 0;
    }
    if (o1 == null) {
      return 1;
    }
    if (o2 == null) {
      return -1;
    }

    if (o1.getScore()==null && o2.getScore()==null) {
      return 0;
    }
    if (o1.getScore()==null) {
      return 1;
    }
    if (o2.getScore()==null) {
      return -1;
    }
    int rez = o1.getScore().compareTo(o2.getScore());
    if (rez != 0) {
      return -1*rez;
    }

    rez = o1.getMetabolizerStatus().compareTo(o2.getMetabolizerStatus());
    if (rez != 0) {
      return rez;
    }

    return 0;
  }
}
