package org.pharmgkb;/*
 ----- BEGIN LICENSE BLOCK -----
 Version: MPL 1.1/GPL 2.0/LGPL 2.1

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with the
 License. You may obtain a copy of the License at http://www.mozilla.org/MPL/

 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 the specific language governing rights and limitations under the License.

 The Original Code is PharmGen.

 The Initial Developer of the Original Code is PharmGKB (The Pharmacogenetics
 and Pharmacogenetics Knowledge Base, supported by NIH U01GM61374). Portions
 created by the Initial Developer are Copyright (C) 2010 the Initial Developer.
 All Rights Reserved.

 Contributor(s):

 Alternatively, the contents of this file may be used under the terms of
 either the GNU General Public License Version 2 or later (the "GPL"), or the
 GNU Lesser General Public License Version 2.1 or later (the "LGPL"), in
 which case the provisions of the GPL or the LGPL are applicable instead of
 those above. If you wish to allow use of your version of this file only
 under the terms of either the GPL or the LGPL, and not to allow others to
 use your version of this file under the terms of the MPL, indicate your
 decision by deleting the provisions above and replace them with the notice
 and other provisions required by the GPL or the LGPL. If you do not delete
 the provisions above, a recipient may use your version of this file under
 the terms of any one of the MPL, the GPL or the LGPL.

 ----- END LICENSE BLOCK -----
 */
import java.io.File;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.poi.ss.usermodel.Row;
import util.Value;


/**
 * Created by IntelliJ IDEA. User: whaleyr Date: Jun 18, 2010 Time: 10:51:19 AM To change this template use File |
 * Settings | File Templates.
 */
public class ItpcSheetTest extends TestCase {

  File file = null;

  public void setUp() throws Exception {
    file = new File("/Users/whaleyr/Documents/Workbench/ItpcParser/test/sample.data.xls");
  }

  public void testParseColumnIndexes() throws Exception {
    ItpcSheet sheet = new ItpcSheet(file, false);

    assertTrue(sheet.ageIdx>=0);
    assertTrue(sheet.subjectId>=0);
    assertTrue(sheet.projectSiteIdx>=0);
    assertTrue(sheet.ageIdx>=0);
    assertTrue(sheet.menoStatusIdx>=0);
    assertTrue(sheet.metastaticIdx>=0);
    assertTrue(sheet.erStatusIdx>=0);
    assertTrue(sheet.durationIdx>=0);
    assertTrue(sheet.tamoxDoseIdx>=0);
    assertTrue(sheet.tumorSourceIdx>=0);
    assertTrue(sheet.bloodSourceIdx>=0);
    assertTrue(sheet.priorHistoryIdx>=0);
    assertTrue(sheet.priorSitesIdx>=0);
    assertTrue(sheet.priorDcisIdx>=0);
    assertTrue(sheet.chemoIdx>=0);
    assertTrue(sheet.hormoneIdx>=0);
    assertTrue(sheet.systemicTherIdx>=0);
    assertTrue(sheet.followupIdx>=0);
    assertTrue(sheet.timeBtwSurgTamoxIdx>=0);
    assertTrue(sheet.firstAdjEndoTherIdx>=0);
    assertTrue(sheet.genoSourceIdx1>=0);
    assertTrue(sheet.genoSourceIdx2>=0);
    assertTrue(sheet.genoSourceIdx3>=0);

    assertTrue(sheet.fluoxetineCol>=0);
    assertTrue(sheet.paroxetineCol>=0);
    assertTrue(sheet.quinidienCol>=0);
    assertTrue(sheet.buproprionCol>=0);
    assertTrue(sheet.duloxetineCol>=0);
    assertTrue(sheet.cimetidineCol>=0);
    assertTrue(sheet.sertralineCol>=0);
    assertTrue(sheet.citalopramCol>=0);

    assertTrue(sheet.rs4986774idx>=0);
    assertTrue(sheet.rs1065852idx>=0);
    assertTrue(sheet.rs3892097idx>=0);
    assertTrue(sheet.star5idx>=0);
    assertTrue(sheet.rs5030655idx>=0);
    assertTrue(sheet.rs16947idx>=0);
    assertTrue(sheet.rs28371706idx>=0);
    assertTrue(sheet.rs28371725idx>=0);

    assertTrue(sheet.amplichipidx>=0);

    assertTrue(sheet.allele1idx>=0);
    assertTrue(sheet.allele2idx>=0);
    assertTrue(sheet.allele1finalIdx>=0);
    assertTrue(sheet.allele2finalIdx>=0);
    assertTrue(sheet.genotypeIdx>=0);
    assertTrue(sheet.weakIdx>=0);
    assertTrue(sheet.potentIdx>=0);
    assertTrue(sheet.metabStatusIdx>=0);
    assertTrue(sheet.includeIdx>=0);
    assertTrue(sheet.scoreIdx>=0);

    assertTrue(sheet.incAgeIdx>=0);
    assertTrue(sheet.incNonmetaIdx>=0);
    assertTrue(sheet.incPriorHistIdx>=0);
    assertTrue(sheet.incErPosIdx>=0);
    assertTrue(sheet.incSysTherIdx>=0);
    assertTrue(sheet.incAdjTamoxIdx>=0);
    assertTrue(sheet.incDurationIdx>=0);
    assertTrue(sheet.incTamoxDoseIdx>=0);
    assertTrue(sheet.incChemoIdx>=0);
    assertTrue(sheet.incHormoneIdx>=0);
    assertTrue(sheet.incDnaCollectionIdx>=0);
    assertTrue(sheet.incFollowupIdx>=0);
    assertTrue(sheet.incGenoDataAvailIdx>=0);
  }

  public void testNext() throws Exception {
    ItpcSheet sheet = new ItpcSheet(file, false);

    assertTrue(sheet.hasNext());

    Subject subject = sheet.next();
    assertNotNull(subject);
    Assert.assertEquals(2,sheet.getCurrentRowIndex());

    Assert.assertEquals("ID1", subject.getSubjectId());
    String subject1 = subject.getSubjectId();
    Assert.assertEquals("999", subject.getProjectSite());

    Assert.assertEquals("Unknown/Unknown",subject.getGenotypePgkb().toString());
    Assert.assertEquals("*1/*1",subject.getGenotypeFinal().toString());

    Assert.assertEquals("EM/EM",subject.getGenotypeFinal().getMetabolizerStatus());

    Assert.assertEquals(Value.Unknown, subject.getWeak());
    Assert.assertEquals(Value.Unknown, subject.getPotent());

    Assert.assertEquals(Value.Yes, subject.passInclusion1());
    Assert.assertEquals(Value.Yes, subject.passInclusion2a());
    Assert.assertEquals(Value.Yes, subject.passInclusion2b());
    Assert.assertEquals(Value.Yes, subject.passInclusion3());
    Assert.assertEquals(Value.No, subject.passInclusion4());
    Assert.assertEquals(Value.No, subject.passInclusion4a());
    Assert.assertEquals(Value.Yes, subject.passInclusion4b());
    Assert.assertEquals(Value.Yes, subject.passInclusion4c());
    Assert.assertEquals(Value.Yes, subject.passInclusion5());
    Assert.assertEquals(Value.Yes, subject.passInclusion6());
    Assert.assertEquals(Value.Yes, subject.passInclusion7());
    Assert.assertEquals(Value.Yes, subject.passInclusion8());
    Assert.assertEquals(Value.Yes, subject.passInclusion9());
    Assert.assertEquals(Value.No, subject.include());

    assertTrue(sheet.hasNext());
    subject = sheet.next();
    Assert.assertEquals(3,sheet.getCurrentRowIndex());
    assertFalse(subject1.equals(subject.getSubjectId()));

    Row row = sheet.getCurrentRow();
    Assert.assertEquals(subject.getSubjectId(), row.getCell(sheet.subjectId).getStringCellValue());
  }
}
