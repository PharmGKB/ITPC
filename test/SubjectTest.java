import junit.framework.TestCase;
import util.Value;

/**
 * Created by IntelliJ IDEA.
 * User: whaleyr
 * Date: Jul 14, 2010
 * Time: 8:57:30 PM
 */
public class SubjectTest extends TestCase {
  public void testGetScore() {
    Subject subject = new Subject();

    assertNull(subject.getScore());

    /* Getting a score when genotype data is available but inhibitor information is not should be null */

    Genotype genotype = new Genotype("*1/*1");
    subject.setGenotypePgkb(genotype);

    assertNull(subject.getScore());

    /* First successful test, genotype and drug information is all available */

    weaksToNo(subject);
    potentToNo(subject);

    assertEquals(2f, subject.getScore());
    assertEquals("Extensive two", subject.getMetabolizerGroup());

    /* Test where a subject has both a potent and a weak inhibitor, Potents should always make score 0 */

    subject.setHasCimetidine(Value.Yes);
    subject.setHasParoxetine(Value.Yes);

    assertEquals(0f, subject.getScore());
    assertEquals("Poor", subject.getMetabolizerGroup());

    /* Keeps the inhibitor information but tries an unknown genotype
     * The rule states that Potents always cause 0 score, even if genotype is unknown */

    Genotype unknownGenotype = new Genotype("Unknown/*1");
    subject.setGenotypePgkb(unknownGenotype);

    assertEquals(0f, subject.getScore());

    /* Test to see if PM/PM with unknown inhibitors returns the correct values */

    subject = new Subject();
    subject.setGenotypePgkb(new Genotype("*3/*3"));
    assertEquals(0f, subject.getScore());
    assertEquals("Poor", subject.getMetabolizerGroup());
  }

  public void testCalculateGenotypePgkb() {
    Subject subject = new Subject();
    assertEquals("Unknown/Unknown",subject.getGenotypePgkb().toString());
    subject.setDeletion("no deletion");

    VariantAlleles va1 = new VariantAlleles("a/-");
    subject.setRs4986774(va1);
    va1 = new VariantAlleles("c/c");
    subject.setRs1065852(va1);
    va1 = new VariantAlleles("g/g");
    subject.setRs3892097(va1);
    va1 = new VariantAlleles("t/t");
    subject.setRs5030655(va1);

    assertEquals("*3/Unknown",subject.getGenotypePgkb().toString());

    va1 = new VariantAlleles("c/c");
    subject.setRs16947(va1);
    va1 = new VariantAlleles("c/c");
    subject.setRs28371706(va1);
    va1 = new VariantAlleles("g/g");
    subject.setRs28371725(va1);

    assertEquals("*1/*3",subject.getGenotypePgkb().toString());

    subject = makeDefaultSubject();
    assertEquals("*1/*1",subject.getGenotypePgkb().toString());

    subject.setDeletion("deletion");
    assertEquals("*1/*5",subject.getGenotypePgkb().toString());
  }

  public void testSetGenotypeAmplichip() {
    Subject subject = makeDefaultSubject();
    try {
      subject.setGenotypeAmplichip("*1AXN/*3B");
      assertEquals("*1XN/*3",subject.getGenotypeAmplichip().toString());

      subject.setGenotypeAmplichip("*1axn/*3");
      assertEquals("*1XN/*3",subject.getGenotypeAmplichip().toString());

      subject.setGenotypeAmplichip("*1/*1");
      assertEquals("*1/*1",subject.getGenotypeAmplichip().toString());
    }
    catch (Exception ex) {
      fail("Couldn't parse amplichip");
    }
  }

  public void testInclusion1() {
    Subject subject = new Subject();
    assertEquals(Value.Unknown, subject.passInclusion1());

    subject.setMenoStatus("1");
    assertEquals(Value.No, subject.passInclusion1());

    subject.setMenoStatus("2");
    assertEquals(Value.Yes, subject.passInclusion1());

    subject.setMenoStatus(null);
    subject.setAge("49");
    assertEquals(Value.No, subject.passInclusion1());

    subject.setAge("51");
    assertEquals(Value.Yes, subject.passInclusion1());    
  }

  public void testInclusion2a() {
    Subject subject = new Subject();
    assertEquals(Value.Unknown, subject.passInclusion2a());

    subject.setMetastatic("0");
    assertEquals(Value.Yes, subject.passInclusion2a());

    subject.setMetastatic("1");
    assertEquals(Value.No, subject.passInclusion2a());

    subject.setMetastatic("SpongeBob");
    assertEquals(Value.Unknown, subject.passInclusion2a());
  }

  public void testInclusion2b() {
    Subject subject = new Subject();
    assertEquals(Value.Yes, subject.passInclusion2b());

    subject.setPriorHistory("0");
    subject.setPriorDcis("0");
    assertEquals(Value.Yes, subject.passInclusion2b());

    subject.setPriorHistory("1");
    subject.setPriorDcis("0");
    assertEquals(Value.No, subject.passInclusion2b());

    subject.setPriorHistory("0");
    subject.setPriorDcis("1");
    assertEquals(Value.No, subject.passInclusion2b());
  }

  public void testInclusion3() {
    Subject subject = new Subject();
    assertEquals(Value.No, subject.passInclusion3());

    subject.setErStatus("0");
    assertEquals(Value.No, subject.passInclusion3());

    subject.setErStatus("1");
    assertEquals(Value.Yes, subject.passInclusion3());

    subject.setErStatus("SpongeBob");
    assertEquals(Value.No, subject.passInclusion3());
  }

  public void testInclusion4() {
    Subject subject = new Subject();
    assertEquals(Value.No, subject.passInclusion4());

    subject.setSystemicTher("0");
    assertEquals(Value.No, subject.passInclusion4());

    subject.setSystemicTher("2");
    assertEquals(Value.Yes, subject.passInclusion4());

    subject.setSystemicTher("SpongeBob");
    assertEquals(Value.No, subject.passInclusion4());
  }

  public void testInclusion4a() {
    Subject subject = new Subject();
    assertEquals(Value.No, subject.passInclusion4a());

    subject.setTimeBtwSurgTamox("200");
    subject.setFirstAdjEndoTher("0");
    assertEquals(Value.No, subject.passInclusion4a());

    subject.setTimeBtwSurgTamox("180");
    subject.setFirstAdjEndoTher("0");
    assertEquals(Value.No, subject.passInclusion4a());

    subject.setTimeBtwSurgTamox("180");
    subject.setFirstAdjEndoTher("1");
    assertEquals(Value.Yes, subject.passInclusion4a());

    subject.setTimeBtwSurgTamox("Spongebob");
    subject.setFirstAdjEndoTher("1");
    assertEquals(Value.No, subject.passInclusion4a());

    subject.setTimeBtwSurgTamox("< 6 weeks");
    subject.setFirstAdjEndoTher("1");
    assertEquals(Value.Yes, subject.passInclusion4a());

    subject.setTimeBtwSurgTamox("28-42");
    subject.setFirstAdjEndoTher("1");
    assertEquals(Value.Yes, subject.passInclusion4a());

  }

  public void testInclusion4b() {
    Subject subject = new Subject();
    assertEquals(Value.No, subject.passInclusion4b());

    subject.setDuration("1");
    assertEquals(Value.No, subject.passInclusion4b());

    subject.setDuration("0");
    assertEquals(Value.Yes, subject.passInclusion4b());

    subject.setDuration("SpongeBob");
    assertEquals(Value.No, subject.passInclusion4b());
  }

  public void testInclusion4c() {
    Subject subject = new Subject();
    assertEquals(Value.No, subject.passInclusion4c());

    subject.setTamoxDose("1");
    assertEquals(Value.No, subject.passInclusion4c());

    subject.setTamoxDose("0");
    assertEquals(Value.Yes, subject.passInclusion4c());

    subject.setTamoxDose("SpongeBob");
    assertEquals(Value.No, subject.passInclusion4c());
  }

  public void testInclusion5() {
    Subject subject = new Subject();
    assertEquals(Value.Yes, subject.passInclusion5());

    subject.setChemotherapy("0");
    assertEquals(Value.Yes, subject.passInclusion5());

    subject.setChemotherapy("1");
    assertEquals(Value.No, subject.passInclusion5());
  }

  public void testInclusion6() {
    Subject subject = new Subject();
    assertEquals(Value.Yes, subject.passInclusion6());

    subject.setHormoneTherapy("0");
    assertEquals(Value.Yes, subject.passInclusion6());

    subject.setHormoneTherapy("1");
    assertEquals(Value.No, subject.passInclusion6());
  }

  public void testInclusion7() {
    Subject subject = new Subject();
    assertEquals(Value.No, subject.passInclusion7());

    subject.setGenoSource("0");
    assertEquals(Value.No, subject.passInclusion7());

    subject.setTumorSource("1");
    assertEquals(Value.Yes, subject.passInclusion7());

    subject.setGenoSource("1");
    subject.setBloodSource("3");
    assertEquals(Value.No, subject.passInclusion7());

    subject.setGenoSource("1");
    subject.setBloodSource("2");
    assertEquals(Value.Yes, subject.passInclusion7());
  }

  public void testInclusion8() {
    Subject subject = new Subject();
    assertEquals(Value.Yes, subject.passInclusion8());

    subject.setFollowup("2");
    assertEquals(Value.No, subject.passInclusion8());

    subject.setFollowup("1");
    assertEquals(Value.Yes, subject.passInclusion8());
  }

  public void testInclusion9() {
    Subject subject = new Subject();
    assertEquals(Value.No, subject.passInclusion9());

    subject = makeDefaultSubject();
    assertEquals(Value.Yes, subject.passInclusion9());

    subject.setRs1065852(new VariantAlleles());
    assertEquals(Value.No, subject.passInclusion9());
  }

  public void testInclude() {
    Subject subject = makeDefaultSubject();
    setPhenotypes(subject);
    assertEquals(Value.Yes, subject.include());

    subject.setFollowup("2");
    assertEquals(Value.No, subject.include());
  }

  public void testIncludeWo4a() {
    Subject subject = makeDefaultSubject();
    setPhenotypes(subject);
    assertEquals(Value.Yes, subject.includeWo4a());

    subject.setTimeBtwSurgTamox("200");    
    assertEquals(Value.Yes, subject.includeWo4a());

    subject.setFollowup("2");
    assertEquals(Value.No, subject.includeWo4a());
  }

  private void setPhenotypes(Subject subject) {
    subject.setMenoStatus("2");
    subject.setMetastatic("0");
    subject.setPriorHistory("0");
    subject.setPriorDcis("0");
    subject.setErStatus("1");
    subject.setSystemicTher("2");
    subject.setTimeBtwSurgTamox("180");
    subject.setFirstAdjEndoTher("1");
    subject.setDuration("0");
    subject.setTamoxDose("0");
    subject.setChemotherapy("0");
    subject.setHormoneTherapy("0");
    subject.setGenoSource("1");
    subject.setBloodSource("2");
    subject.setFollowup("1");
  }

  private void weaksToNo(Subject subject) {
    subject.setHasCimetidine(Value.No);
    subject.setHasSertraline(Value.No);
    subject.setHasCitalopram(Value.No);
  }

  private void potentToNo(Subject subject) {
    subject.setHasParoxetine(Value.No);
    subject.setHasFluoxetine(Value.No);
    subject.setHasQuinidine(Value.No);
    subject.setHasBuproprion(Value.No);
    subject.setHasDuloxetine(Value.No);
  }

  private Subject makeDefaultSubject() {
    Subject subject = new Subject();

    weaksToNo(subject);
    potentToNo(subject);
    subject.setDeletion("no deletion");

    VariantAlleles va1 = new VariantAlleles("a/a");
    subject.setRs4986774(va1);
    va1 = new VariantAlleles("c/c");
    subject.setRs1065852(va1);
    va1 = new VariantAlleles("g/g");
    subject.setRs3892097(va1);
    va1 = new VariantAlleles("t/t");
    subject.setRs5030655(va1);
    va1 = new VariantAlleles("c/c");
    subject.setRs16947(va1);
    va1 = new VariantAlleles("c/c");
    subject.setRs28371706(va1);
    va1 = new VariantAlleles("g/g");
    subject.setRs28371725(va1);

    return subject;
  }

}
