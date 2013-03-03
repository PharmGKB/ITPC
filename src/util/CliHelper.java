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

package util;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.util.HelpFormatter;
import org.apache.commons.cli2.validation.EnumValidator;

import java.io.IOException;
import java.util.List;
import java.util.Set;


/**
 * This is a helper class for command line utilities to interact with command line arguments.
 *
 * @author Mark Woon
 */
public class CliHelper {
  private static final String sf_verboseFlag = "verbose";
  private static final String sf_helpFlag = "help";
  private static final String sf_targets = "targets";
  private final DefaultOptionBuilder m_optBuilder = new DefaultOptionBuilder();
  private final ArgumentBuilder m_argBuilder = new ArgumentBuilder();
  private GroupBuilder m_groupBuilder = new GroupBuilder();
  private String m_name;
  private Group m_options;
  private Option m_helpOption;
  private Option m_verboseOption;
  private Option m_targetsOption;
  private CommandLine m_commandLine;


  /**
   * Standard constructor.
   *
   * @param cls the class with the main() method
   * @param hasTargets true if generic arguments (i.e. non-option arguments) are expected, false
   * otherwise
   */
  public CliHelper(Class cls, boolean hasTargets) {

    m_name = cls.getName();
    m_groupBuilder = m_groupBuilder.withName("options");
    m_helpOption = m_optBuilder.withDescription("print thismessage")
        .withLongName(sf_helpFlag)
        .withShortName("h")
        .create();
    m_groupBuilder = m_groupBuilder.withOption(m_helpOption);
    m_verboseOption = m_optBuilder.withDescription("enable verbose output")
        .withLongName(sf_verboseFlag)
        .withShortName("v")
        .create();
    m_groupBuilder = m_groupBuilder.withOption(m_verboseOption);
    if (hasTargets) {
      m_targetsOption = m_argBuilder.withName(sf_targets).create();
      m_groupBuilder = m_groupBuilder.withOption(m_targetsOption);
    }
  }


  /**
   * Add an option that doesn't take an argument.
   */
  public void addOption(String shortName, String longName, String description) {

    if (shortName.equals("h") || shortName.equals("v")) {
      throw new IllegalArgumentException("-h and -v are reserved arguments");
    }
    m_groupBuilder = m_groupBuilder.withOption(m_optBuilder
        .withDescription(description)
        .withLongName(longName)
        .withShortName(shortName)
        .create());
  }

  /**
   * Add an option that takes an argument.
   */
  public void addOption(String shortName, String longName, String description,
      String argName) {

    addOption(shortName, longName, description, argName, false);
  }

  /**
   * Add a required option that takes an argument.
   */
  public void addOption(String shortName, String longName, String description,
      String argName, boolean isRequired) {

    if (shortName.equals("h") || shortName.equals("v")) {
      throw new IllegalArgumentException("-h and -v are reserved arguments");
    }
    m_groupBuilder = m_groupBuilder.withOption(m_optBuilder
        .withDescription(description)
        .withLongName(longName)
        .withShortName(shortName)
        .withArgument(m_argBuilder
            .withName(argName)
            .withMinimum(1)
            .withMaximum(1)
            .create())
        .withRequired(isRequired)
        .create());
  }

  /**
   * Add a required option that takes more than one argument.
   */
  public void addOption(String shortName, String longName, String description,
      String argName, int numArgs, boolean isRequired) {

    if (shortName.equals("h") || shortName.equals("v")) {
      throw new IllegalArgumentException("-h and -v are reserved arguments");
    }
    m_groupBuilder = m_groupBuilder.withOption(m_optBuilder
        .withDescription(description)
        .withLongName(longName)
        .withShortName(shortName)
        .withArgument(m_argBuilder
            .withName(argName)
            .withMinimum(1)
            .withMaximum(numArgs)
            .create())
        .withRequired(isRequired)
        .create());
  }


  /**
   * Add a required option that takes an enumerated argument.
   */
  public void addOption(String shortName, String longName, String description,
      String argName, Set<String> arguments, boolean isRequired) {

    if (shortName.equals("h") || shortName.equals("v")) {
      throw new IllegalArgumentException("-h and -v are reserved arguments");
    }
    m_groupBuilder = m_groupBuilder.withOption(m_optBuilder
        .withDescription(description)
        .withLongName(longName)
        .withShortName(shortName)
        .withArgument(m_argBuilder
            .withName(argName)
            .withMinimum(1)
            .withMaximum(1)
            .withValidator(new EnumValidator(arguments))
            .create())
        .withRequired(isRequired)
        .create());
  }

  /**
   * Parses arguments.
   */
  public void parse(String[] args) throws OptionException {

    m_options = m_groupBuilder.create();
    Parser parser = new Parser();
    parser.setGroup(m_options);
    parser.setHelpOption(m_helpOption);
    m_commandLine = parser.parse(args);
  }


  /**
   * Checks whether the specified option exists.
   */
  public boolean hasOption(String opt) {
    return m_commandLine.hasOption(opt);
  }

  /**
   * Gets the String value for the given option.
   */
  public String getValue(String opt) {
    return (String)m_commandLine.getValue(opt);
  }

  /**
   * Gets the String values for the given option.
   */
  public List<String> getValues(String opt) {
    //noinspection unchecked
    return m_commandLine.getValues(opt);
  }

  /**
   * Gets the int value for the given option.
   */
  public int getIntValue(String opt) {
    return Integer.parseInt((String)m_commandLine.getValue(opt));
  }

  /**
   * Gets the targets
   */
  public List getTargets() {
    return m_commandLine.getValues(m_targetsOption);
  }


  /**
   * Gets whether to operate in verbose mode.
   */
  public boolean isVerbose() {
    return m_commandLine.hasOption(m_verboseOption);
  }


  /**
   * Gets whether help on command line arguments has been requested.
   */
  public boolean isHelpRequested() {
    return m_commandLine.hasOption(m_helpOption);
  }


  /**
   * Prints the help message.
   */
  public void printHelp() throws IOException {

    HelpFormatter hf = new HelpFormatter();
    hf.setShellCommand(m_name);
    hf.setGroup(m_options);
    hf.print();
  }
}
