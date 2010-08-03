package util;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

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
}
