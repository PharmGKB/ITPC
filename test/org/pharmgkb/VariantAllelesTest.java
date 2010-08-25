package org.pharmgkb;

import junit.framework.TestCase;
import util.StringPair;

/**
 * Created by IntelliJ IDEA.
 * User: whaleyr
 * Date: Aug 11, 2010
 */
public class VariantAllelesTest extends TestCase {

  public void testVariantAlleles() {

    /* Testing typical assignments */
    VariantAlleles va = new VariantAlleles("A/A");
    assertTrue(va.is("a","a"));

    va = new VariantAlleles("G/C");
    assertTrue(va.is("g","c"));
    assertTrue(va.is("c","g")); // order shouldn't matter

    va = new VariantAlleles("-/-");
    assertTrue(va.is("-","-"));

    va = new VariantAlleles("T/NA");
    assertTrue(va.is("t", StringPair.UNKNOWN));

  }

}
