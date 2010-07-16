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
    assertEquals(2f, genotype.getScore());

    genotype = new Genotype("*1","*9");
    assertEquals(1.5f, genotype.getScore());

    genotype = new Genotype("*3","*9");
    assertEquals(0.5f, genotype.getScore());

    genotype = new Genotype("*1XN","*1XN");
    assertEquals(4.0f, genotype.getScore());
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
    assertEquals("EM/EM", genotype.getMetabolizerStatus());

    genotype = new Genotype("*1","*3");
    assertEquals("EM/PM", genotype.getMetabolizerStatus());

    genotype = new Genotype("*1XN","*3");
    assertEquals("PM/UM", genotype.getMetabolizerStatus());
  }

  public void testToString() {
    Genotype unknownGenotype = new Genotype("Unknown/*1");
    assertEquals("*1/Unknown", unknownGenotype.toString());

    Genotype genotype = new Genotype("*3/*1");
    assertEquals("*1/*3", genotype.toString());
  }
}
