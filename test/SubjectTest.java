import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: whaleyr
 * Date: Jul 14, 2010
 * Time: 8:57:30 PM
 */
public class SubjectTest extends TestCase {
  public void testGetScore() {
    Subject subject = new Subject();

    Genotype genotype = new Genotype("*1/*1");

    assertNull(subject.getScore());

    subject.setGenotypeFinal(genotype);

    assertNull(subject.getScore());

    weaksToNo(subject);
    potentToNo(subject);

    assertEquals(2f, subject.getScore());

    subject.setHasCimetidine(Subject.Value.Yes);
    subject.setHasParoxetine(Subject.Value.Yes);

    assertEquals(1.5f, subject.getScore());

    Genotype unknownGenotype = new Genotype("Unknown/*1");
    subject.setGenotypeFinal(unknownGenotype);

    assertNull(subject.getScore());
  }

  public void testCalculateGenotypePgkb() {
    Subject subject = new Subject();
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

  private void weaksToNo(Subject subject) {
    subject.setHasCimetidine(Subject.Value.No);
    subject.setHasSertraline(Subject.Value.No);
    subject.setHasCitalopram(Subject.Value.No);
  }

  private void potentToNo(Subject subject) {
    subject.setHasParoxetine(Subject.Value.No);
    subject.setHasFluoxetine(Subject.Value.No);
    subject.setHasQuinidine(Subject.Value.No);
    subject.setHasBuproprion(Subject.Value.No);
    subject.setHasDuloxetine(Subject.Value.No);
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
