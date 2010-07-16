import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: whaleyr
 * Date: Jul 13, 2010
 * Time: 11:38:12 PM
 */
public abstract class StringPair {
  public static final String UNKNOWN = "Unknown";
  private List<String> m_strings = new ArrayList<String>();

  public List<String> getStrings() {
    return m_strings;
  }

  public void setStrings(List<String> strings) {
    m_strings = strings;
  }

  public abstract boolean isValid(String string);

  public void addString(String string) {
    if (isValid(string)) m_strings.add(string);
    Collections.sort(m_strings, String.CASE_INSENSITIVE_ORDER);
  }

  public void removeString(String string) {
    m_strings.remove(string);
  }

  public void addAll(StringPair pair) {
    for (String string : pair.getStrings()) {
      this.addString(string);
    }
  }

  public String get(int i) {
    return m_strings.get(i);
  }

  public int count(String datum) {
    int count = 0;
    for (String element : m_strings) {
      if (element.equalsIgnoreCase(datum)) count++;
    }
    return count;
  }

  public boolean contains(String inString) {
    return count(inString)>0;
  }

  public boolean is(String string1, String string2) {
    if (string1 != null && string2 != null) {
      if (string1.equalsIgnoreCase(string2)) {
        return count(string1)==2;
      }
      else {
        return (count(string1) == 1 && count(string2)==1);
      }
    }
    return false;
  }

  public boolean isUncertain() {
    return this.getStrings().contains(UNKNOWN) || this.getStrings().size()!=2;
  }

  public boolean hasData() {
    return !m_strings.isEmpty() && m_strings.size()==2;
  }

  public boolean isEmpty() {
    return m_strings.isEmpty();
  }

  public int size() {
    return m_strings.size();
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (this.getStrings().size()>=1) {
      sb.append(this.getStrings().get(0));
    }
    if (this.getStrings().size()==2) {
      sb.append("/");
      sb.append(this.getStrings().get(1));
    }
    return sb.toString();
  }
}
