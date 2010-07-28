package util;

/**
 * Created by IntelliJ IDEA.
 * User: whaleyr
 * Date: Jul 16, 2010
 * Time: 9:54:36 AM
 * To change this template use File | Settings | File Templates.
 */
public enum Value {
  Unknown,
  Yes,
  No;

  public String toString() {
    if (this==Yes) {
      return "Yes";
    }
    else if (this==No) {
      return "No";
    }
    else {
      return "Unknown";
    }
  }
}
