import org.apache.log4j.Logger;
import util.CliHelper;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: whaleyr
 * Date: Jul 27, 2010
 * Time: 7:56:34 AM
 */
public class Parser {
  private static final Logger sf_logger = Logger.getLogger(Parser.class);
  private File m_fileInput;
  protected ItpcSheet dataSheet;

  public static void main(String[] args) {
    try {
      Parser parser = new Parser();
      parser.parseArgs(args);
      parser.parseFile();
    }
    catch (Exception ex) {
      sf_logger.error("Error running parser", ex);
      ex.printStackTrace();
    }
  }

  protected void parseFile() throws Exception {
    if (getFileInput() == null || !getFileInput().exists()) {
      throw new Exception("Input file doesn't exist");
    }

    dataSheet = new ItpcSheet(getFileInput());
    GenotypeSummary genotypeSummary = new GenotypeSummary();
    MetabStatusSummary metabSummary = new MetabStatusSummary();
    NonFourSummary nonFourSummary = new NonFourSummary();

    int sampleCount = 0;
    while (dataSheet.hasNext()) {
      try {
        Subject subject = dataSheet.next();
        dataSheet.writeSubjectCalculatedColumns(subject);

        genotypeSummary.addSubject(subject);
        metabSummary.addSubject(subject);
        nonFourSummary.addSubject(subject);

        sampleCount++;
      }
      catch (Exception ex) {
        throw new Exception("Exception on line "+dataSheet.getCurrentRowIndex(), ex);
      }
    }
    sf_logger.info("Parsed " + sampleCount + " samples");

    genotypeSummary.writeToWorkbook(dataSheet.getWorkbook());
    metabSummary.writeToWorkbook(dataSheet.getWorkbook());

    dataSheet.saveOutput();
  }

  protected void parseArgs(String[] args) throws Exception {
    CliHelper cli = new CliHelper(getClass(), false);
    cli.addOption("f", "file", "ITPC excel file to read", "pathToFile");

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

  }

  public File getFileInput() {
    return m_fileInput;
  }

  public void setFileInput(File fileInput) {
    m_fileInput = fileInput;
  }
}
