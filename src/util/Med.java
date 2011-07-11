package util;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: whaleyr
 * Date: 7/6/11
 */
public enum Med {
  Fluoxetine,
  Paroxetine,
  Quinidine,
  Buproprion,
  Duloxetine,
  Sertraline,
  Diphenhydramine,
  Thioridazine,
  Amiodarone,
  Trazodone,
  Cimetidine,
  Venlafaxine,
  Citalopram,
  Escitalopram;


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

  public static Med matchesMed(String string) {
    for (Pattern pattern : sf_medPatterns.keySet()) {
      if (pattern.matcher(string).matches()) {
        return sf_medPatterns.get(pattern);
      }
    }
    return null;
  }
}

