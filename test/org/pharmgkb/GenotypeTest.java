package org.pharmgkb;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: whaleyr
 * Date: Jul 14, 2010
 * Time: 4:16:25 PM
 */
public class GenotypeTest extends TestCase {

  public void testIs() {
    Genotype genotype = new Genotype();

    genotype.addString("SpongeBob");

    genotype.addString("*1");
    genotype.addString("*4");

    assertTrue(genotype.is("*4","*1"));

    genotype = new Genotype("*1/*4");
    assertTrue(genotype.is("*4","*1"));

    genotype = new Genotype("*1","*4");
    assertTrue(genotype.is("*4","*1"));
  }

  public void testIsUncertain() {

    Genotype genotype = new Genotype("*1","Unknown");

    assertTrue(genotype.isUncertain());

    genotype.removeString("Unknown");
    genotype.addString("*3");

    assertTrue(!genotype.isUncertain());

    genotype = new Genotype();
    genotype.addString("*1");
    assertTrue(genotype.isUncertain());

    genotype = new Genotype();
    assertTrue(genotype.isUncertain());
  }

  public void testGetScore() {
    Genotype genotype = new Genotype("*1","*1");
    Assert.assertEquals(2f, genotype.getScore());

    genotype = new Genotype("*1","*9");
    Assert.assertEquals(1.5f, genotype.getScore());

    genotype = new Genotype("*3","*9");
    Assert.assertEquals(0.5f, genotype.getScore());

    genotype = new Genotype("*1XN","*1XN");
    Assert.assertEquals(4.0f, genotype.getScore());
  }

  public void testAdd() {
    Genotype genotype = new Genotype("*1XN","*1XN");
    genotype.addString("*1");

    assertTrue(genotype.is("*1","*1XN"));

    genotype.addString("*1XN");
    assertTrue(genotype.is("*1","*1XN"));
  }

  public void testGetMetabolizerStatus() {
    Genotype genotype = new Genotype("*1","*1");
    Assert.assertEquals("EM/EM", genotype.getMetabolizerStatus());

    genotype = new Genotype("*1","*3");
    Assert.assertEquals("EM/PM", genotype.getMetabolizerStatus());

    genotype = new Genotype("*1XN","*3");
    Assert.assertEquals("PM/UM", genotype.getMetabolizerStatus());

    genotype = new Genotype("*1XN","*9");
    Assert.assertEquals("IM/UM", genotype.getMetabolizerStatus());
    Assert.assertEquals("Unknown", genotype.getMetabolizerGroup());

    genotype = new Genotype("*3","*1XN");
    Assert.assertEquals("PM/UM", genotype.getMetabolizerStatus());
    Assert.assertEquals("Unknown", genotype.getMetabolizerGroup());
  }

  public void testToString() {
    Genotype unknownGenotype = new Genotype("Unknown/*1");
    Assert.assertEquals("*1/Unknown", unknownGenotype.toString());

    Genotype genotype = new Genotype("*3/*1");
    Assert.assertEquals("*1/*3", genotype.toString());
  }
}
