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

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.pharmgkb.ItpcSheet;
import org.pharmgkb.Subject;
import summary.AbstractSummary;
import summary.GenotypeSummary;
import summary.InclusionSummary;
import summary.MetabStatusSummary;
import util.CliHelper;

import java.io.File;
import java.io.FileWriter;
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

    File sqlFile = new File(getFileInput().getAbsolutePath().replaceAll("\\.xls", ".sql"));
    FileWriter fw = new FileWriter(sqlFile);

    dataSheet = new ItpcSheet(getFileInput(), m_doHighlight);
    List<AbstractSummary> summaries = Arrays.asList(
        new GenotypeSummary(),
        new MetabStatusSummary(),
        new InclusionSummary()
    );

    int sampleCount = 0;
    while (dataSheet.hasNext()) {
      try {
        Subject subject = dataSheet.next();
        dataSheet.writeSubjectCalculatedColumns(subject);

        fw.write(subject.makeSqlInsert());
        fw.write("\n");

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

    fw.write("commit;\n");
    IOUtils.closeQuietly(fw);

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
