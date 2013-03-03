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

