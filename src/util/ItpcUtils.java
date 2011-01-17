package util;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: whaleyr
 * Date: Jul 16, 2010
 * Time: 9:55:50 AM
 */
public class ItpcUtils {
  private static final Logger logger = Logger.getLogger(ItpcUtils.class);
  private static final Pattern sf_alleleRegex = Pattern.compile("\\*\\d+");

  public static boolean isBlank(String string) {
    return
        string == null
            || StringUtils.isBlank(string)
            || string.equalsIgnoreCase("na");
  }

  public static File getOutputFile(File inputFile) {
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmm");
    String newExtension = new StringBuilder()
        .append(".")
        .append(sdf.format(date))
        .append(".all.xls")
        .toString();

    return new File(inputFile.getAbsolutePath().replaceAll("\\.xls", newExtension));  
  }

  /**
   * This method takes a String allele and returns a stripped version of that allele.  This way we don't have to store
   * every version of each allele.  For instance, *4K is stripped down to *4 for processing and mapping.
   * @param allele a String allele
   * @return a stripped version of <code>allele</code>
   */
  public static String alleleStrip(String allele) {
    String alleleClean = null;

    Matcher m = sf_alleleRegex.matcher(allele);
    if (allele.equalsIgnoreCase("Unknown")) {
      alleleClean = "Unknown";
    }
    else if (m.find()) {
      alleleClean = allele.substring(m.start(),m.end());
      if (allele.toLowerCase().contains("xn")) {
        alleleClean += "XN";
      }
      else if (allele.equalsIgnoreCase("*2a")) {
        alleleClean = "*2A";
      }
    }
    else {
      logger.warn("Malformed allele found: " + allele);
    }

    return alleleClean;
  }
}
