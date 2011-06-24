import org.apache.log4j.Logger;
import org.pharmgkb.ItpcSheet;
import org.pharmgkb.Subject;
import summary.*;
import util.CliHelper;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: whaleyr
 * Date: Jul 27, 2010
 * Time: 7:56:34 AM
 */
public class Parser {
  private static final Logger sf_logger = Logger.getLogger(Parser.class);
  private File m_fileInput;
  private boolean m_doHighlight = false;
  protected ItpcSheet dataSheet;

  public static void main(String[] args) {
    try {
      System.out.println("ITPC Parser run: " + new Date());

      Parser parser = new Parser();
      parser.parseArgs(args);
      parser.parseFile();
    }
    catch (Exception ex) {
      sf_logger.error("Error running parser", ex);
    }
  }

  protected void parseFile() throws Exception {
    if (getFileInput() == null || !getFileInput().exists()) {
      throw new Exception("Input file doesn't exist");
    }

    dataSheet = new ItpcSheet(getFileInput(), m_doHighlight);
    List<AbstractSummary> summaries = Arrays.asList(
        new GenotypeSummary(),
        new MetabStatusSummary(),
        new NonFourSummary(),
        new InclusionSummary()
    );

    int sampleCount = 0;
    while (dataSheet.hasNext()) {
      try {
        Subject subject = dataSheet.next();
        dataSheet.writeSubjectCalculatedColumns(subject);

        for (AbstractSummary summ : summaries) {
          summ.addSubject(subject);
        }

        sampleCount++;
      }
      catch (Exception ex) {
        throw new Exception("Exception on line "+dataSheet.getCurrentRowIndex(), ex);
      }
    }
    sf_logger.info("Parsed " + sampleCount + " samples");

    for (AbstractSummary summ : summaries) {
      summ.writeToWorkbook(dataSheet.getWorkbook());
    }

    dataSheet.saveOutput();
  }

  protected void parseArgs(String[] args) throws Exception {
    CliHelper cli = new CliHelper(getClass(), false);
    cli.addOption("f", "file", "ITPC excel file to read", "pathToFile");
    cli.addOption("hi", "highlight", "Highlight changed values");

    try {
      cli.parse(args);
      if (cli.isHelpRequested()) {
        cli.printHelp();
        System.exit(1);
      }
    }
    catch (Exception ex) {
      throw new Exception("Errror parsing params", ex);
    }

    if (cli.hasOption("-f")) {
      File fileInput = new File(cli.getValue("-f"));
      if (fileInput.exists()) {
        this.setFileInput(fileInput);
      }
      else {
        throw new Exception("File doesn't exist: " + fileInput);
      }
    }

    if (cli.hasOption("-hi")) {
      m_doHighlight = true;
    }

  }

  public File getFileInput() {
    return m_fileInput;
  }

  public void setFileInput(File fileInput) {
    m_fileInput = fileInput;
  }
}
