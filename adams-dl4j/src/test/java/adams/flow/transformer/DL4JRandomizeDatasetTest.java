/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * DL4JRandomizeDatasetTest.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.core.scripting.Dummy;
import adams.env.Environment;
import adams.flow.AbstractDL4JFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.sink.DumpFile;
import adams.flow.source.DL4JDatasetIterator;
import adams.ml.dl4j.datasetiterator.RecordReaderDataSetIteratorConfigurator;
import adams.ml.dl4j.datasetpreprocessor.DataSetPreProcessorWithScriptedConfiguration;
import adams.ml.dl4j.inputsplit.SingleFileSplitConfigurator;
import adams.ml.dl4j.recordreader.CSVRecordReaderConfigurator;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for DL4JRandomizeDataset actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class DL4JRandomizeDatasetTest
  extends AbstractDL4JFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public DL4JRandomizeDatasetTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("iris.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("iris.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
    
    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("dumpfile.txt")
        });
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(DL4JRandomizeDatasetTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      List<Actor> actors = new ArrayList<>();

      // Flow.DL4JDatasetIterator
      DL4JDatasetIterator dl4jdatasetiterator = new DL4JDatasetIterator();
      RecordReaderDataSetIteratorConfigurator recordreaderdatasetiteratorconfigurator = new RecordReaderDataSetIteratorConfigurator();
      CSVRecordReaderConfigurator csvrecordreaderconfigurator = new CSVRecordReaderConfigurator();
      SingleFileSplitConfigurator singlefilesplitconfigurator = new SingleFileSplitConfigurator();
      argOption = (AbstractArgumentOption) singlefilesplitconfigurator.getOptionManager().findByProperty("source");
      singlefilesplitconfigurator.setSource((PlaceholderFile) argOption.valueOf("${TMP}/iris.csv"));
      csvrecordreaderconfigurator.setInputSplit(singlefilesplitconfigurator);

      argOption = (AbstractArgumentOption) csvrecordreaderconfigurator.getOptionManager().findByProperty("skipNumLines");
      csvrecordreaderconfigurator.setSkipNumLines((Integer) argOption.valueOf("1"));
      recordreaderdatasetiteratorconfigurator.setRecordReader(csvrecordreaderconfigurator);

      argOption = (AbstractArgumentOption) recordreaderdatasetiteratorconfigurator.getOptionManager().findByProperty("batchSize");
      recordreaderdatasetiteratorconfigurator.setBatchSize((Integer) argOption.valueOf("150"));
      argOption = (AbstractArgumentOption) recordreaderdatasetiteratorconfigurator.getOptionManager().findByProperty("labelIndex");
      recordreaderdatasetiteratorconfigurator.setLabelIndex((Integer) argOption.valueOf("4"));
      argOption = (AbstractArgumentOption) recordreaderdatasetiteratorconfigurator.getOptionManager().findByProperty("numPossibleLabels");
      recordreaderdatasetiteratorconfigurator.setNumPossibleLabels((Integer) argOption.valueOf("3"));
      dl4jdatasetiterator.setIterator(recordreaderdatasetiteratorconfigurator);

      DataSetPreProcessorWithScriptedConfiguration datasetpreprocessorwithscriptedconfiguration = new DataSetPreProcessorWithScriptedConfiguration();
      Dummy dummy = new Dummy();
      datasetpreprocessorwithscriptedconfiguration.setHandler(dummy);

      dl4jdatasetiterator.setPreProcessor(datasetpreprocessorwithscriptedconfiguration);

      actors.add(dl4jdatasetiterator);

      // Flow.DL4JRandomizeDataset
      DL4JRandomizeDataset dl4jrandomize = new DL4JRandomizeDataset();
      actors.add(dl4jrandomize);

      // Flow.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      actors.add(dumpfile);

      flow.setActors(actors.toArray(new Actor[0]));
    }
    catch (Exception e) {
      fail("Failed to set up actor: " + e);
    }
    
    return flow;
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}
