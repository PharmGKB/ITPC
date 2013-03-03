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

package org.pharmgkb;

import util.StringPair;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * This class doesn't allow "Unknown" as a value, it only accepts a,c,g,t, or - (for deletion)
 * User: Ryan Whaley
 */
public class VariantAlleles extends StringPair {

  private static final Set<String> validAlleles = new HashSet<String>();
  static {
    validAlleles.add("a");
    validAlleles.add("t");
    validAlleles.add("g");
    validAlleles.add("c");
    validAlleles.add("-");
  }

  public VariantAlleles() {}

  public VariantAlleles(String alleles) {
    if (alleles == null) {
      return;
    }

    alleles = alleles.trim().toLowerCase();
    String[] data = alleles.split("/");
    Arrays.sort(data, String.CASE_INSENSITIVE_ORDER);
    for (String base : data) {
      this.addString(base.trim().toLowerCase());
    }
  }

  public boolean isValid(String string) {
    return validAlleles.contains(string);
  }

}
