import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: whaleyr
 * Date: Jul 27, 2010
 * Time: 8:08:29 AM
 */
public class ParserTest extends TestCase {

  public void testOpenFile() throws Exception {
    Parser parser = new Parser();
    parser.parseArgs(new String[]{"-f","/Users/whaleyr/Documents/Workbench/ItpcParser/test/sample.data.xls"});

    assertNotNull(parser.getFileInput());
    assertTrue(parser.getFileInput().exists());

    try {
      parser.parseFile();
      assertTrue(parser.dataSheet != null);
      assertTrue(parser.dataSheet.getCurrentRowIndex()>0);
    }
    catch (Exception ex) {
      fail("Exception while parsing file\n" + ex.getMessage());
      ex.printStackTrace();
    }
  }

}
