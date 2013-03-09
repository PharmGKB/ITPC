/*
 * ----- BEGIN LICENSE BLOCK -----
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is PharmGen.
 *
 * The Initial Developer of the Original Code is PharmGKB (The Pharmacogenetics
 * and Pharmacogenetics Knowledge Base, supported by NIH U01GM61374). Portions
 * created by the Initial Developer are Copyright (C) 2013 the Initial Developer.
 * All Rights Reserved.
 *
 * Contributor(s):
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or the
 * GNU Lesser General Public License Version 2.1 or later (the "LGPL"), in
 * which case the provisions of the GPL or the LGPL are applicable instead of
 * those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ----- END LICENSE BLOCK -----
 */

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
  public static final Integer SITE_COUNT = 12;

  private static final Logger logger = Logger.getLogger(ItpcUtils.class);
  private static final Pattern sf_alleleRegex = Pattern.compile("\\*\\d+");

  public static boolean isBlank(String string) {
    String trimString = StringUtils.trimToNull(string);
    return StringUtils.isBlank(trimString) || trimString.equalsIgnoreCase("na");
  }

  public static File getOutputFile(File inputFile) {
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmm");
    String newExtension = new StringBuilder()
        .append(".")
        .append(sdf.format(date))
        .append(".xls")
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

  /**
   * Covert a Value enum to be displayed in an inclusion field
   * @param value a Value enum
   * @return a String of either "Include" or "Exclude" or "Unknown"
   */
  public static String valueToInclusion(Value value) {
    if (value == Value.Yes) {
      return "Include";
    }
    else if (value == Value.No) {
      return "Exclude";
    }
    else {
      return "Unknown";
    }
  }

  /**
   * Covert a Value enum to be displayed in an exclusion field
   * @param value a Value enum
   * @return a String of either "Include" or "Exclude" or "Unknown"
   */
  public static String valueToExclusion(Value value) {
    if (value == Value.Yes) {
      return "Exclude";
    }
    else if (value == Value.No) {
      return "Include";
    }
    else {
      return "Unknown";
    }
  }

  public static String floatDisplay(Float number) {
    if (number == null) {
      return Value.Unknown.toString();
    }
    else {
      return number.toString();
    }
  }
}
