package util;

import org.apache.commons.lang.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: whaleyr
 * Date: Jul 16, 2010
 * Time: 9:55:50 AM
 */
public class ItpcUtils {
  public static boolean isBlank(String string) {
    return
        string == null
            || StringUtils.isBlank(string)
            || string.equalsIgnoreCase("na");
  }
}
