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

  /**
   * Determines whether the given String is a valid value for this StringPair
   * @param string a String to possibly add to this StringPair
   * @return true if string is valid, false otherwise
   */
  public abstract boolean isValid(String string);

  public void addString(String string) {
    if (isValid(string)) {
      m_strings.add(string);
    }
    else {
      m_strings.add(UNKNOWN);
    }
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
