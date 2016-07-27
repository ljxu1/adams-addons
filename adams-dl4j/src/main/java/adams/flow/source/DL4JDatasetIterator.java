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

/**
 * DL4JDatasetIterator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.flow.core.Token;
import adams.ml.dl4j.datasetiterator.DataSetIteratorConfigurator;
import adams.ml.dl4j.datasetiterator.DataSetIteratorWithScriptedConfiguration;
import adams.ml.dl4j.datasetpreprocessor.DataSetPreProcessorConfigurator;
import adams.ml.dl4j.datasetpreprocessor.DataSetPreProcessorWithScriptedConfiguration;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.DataSet;

/**
 <!-- globalinfo-start -->
 * Outputs datasets generated by the DL4J dataset iterator created by the specified configurator.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;org.nd4j.linalg.dataset.DataSet<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: DL4JDatasetIterator
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-iterator &lt;adams.ml.dl4j.datasetiterator.DataSetIteratorConfigurator&gt; (property: iterator)
 * &nbsp;&nbsp;&nbsp;The iterator configurator to use for generating the dataset iterator that 
 * &nbsp;&nbsp;&nbsp;generates the data.
 * &nbsp;&nbsp;&nbsp;default: adams.ml.dl4j.datasetiterator.DataSetIteratorWithScriptedConfiguration -handler adams.core.scripting.Dummy
 * </pre>
 * 
 * <pre>-use-preprocessor &lt;boolean&gt; (property: usePreProcessor)
 * &nbsp;&nbsp;&nbsp;If enabled, uses the supplied preprocessor configurator to generate a preprocessor 
 * &nbsp;&nbsp;&nbsp;to be used with the iterator.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-preprocessor &lt;adams.ml.dl4j.datasetpreprocessor.DataSetPreProcessorConfigurator&gt; (property: preProcessor)
 * &nbsp;&nbsp;&nbsp;The preprocessor configurator to use for generating the dataset preprocessor 
 * &nbsp;&nbsp;&nbsp;that generates the data.
 * &nbsp;&nbsp;&nbsp;default: adams.ml.dl4j.datasetpreprocessor.DataSetPreProcessorWithScriptedConfiguration -handler adams.core.scripting.Dummy
 * </pre>
 * 
 * <pre>-normalize-zero-mean-zero-unit-variance &lt;boolean&gt; (property: normalizeZeroMeanZeroUnitVariance)
 * &nbsp;&nbsp;&nbsp;If enabled, subtract by the column means and divide by the standard deviation.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DL4JDatasetIterator
  extends AbstractSource {

  private static final long serialVersionUID = 711203375341324288L;

  /** the iterator. */
  protected DataSetIteratorConfigurator m_Iterator;

  /** whether to use a preprocessor. */
  protected boolean m_UsePreProcessor;

  /** the preprocessor. */
  protected DataSetPreProcessorConfigurator m_PreProcessor;

  /** Subtract by the column means and divide by the standard deviation. */
  protected boolean m_NormalizeZeroMeanZeroUnitVariance;

  /** the dataset iterator in use. */
  protected transient DataSetIterator m_ActualIterator;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Outputs datasets generated by the DL4J dataset iterator created by "
	+ "the specified configurator.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "iterator", "iterator",
      new DataSetIteratorWithScriptedConfiguration());

    m_OptionManager.add(
      "use-preprocessor", "usePreProcessor",
      false);

    m_OptionManager.add(
      "preprocessor", "preProcessor",
      new DataSetPreProcessorWithScriptedConfiguration());

    m_OptionManager.add(
      "normalize-zero-mean-zero-unit-variance", "normalizeZeroMeanZeroUnitVariance",
      false);
  }

  /**
   * Sets the iterator configurator to use.
   *
   * @param value	the configurator
   */
  public void setIterator(DataSetIteratorConfigurator value) {
    m_Iterator = value;
    reset();
  }

  /**
   * Returns the iterator configurator to use.
   *
   * @return 		the configurator
   */
  public DataSetIteratorConfigurator getIterator() {
    return m_Iterator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String iteratorTipText() {
    return "The iterator configurator to use for generating the dataset iterator that generates the data.";
  }

  /**
   * Sets whether to use the preprocessor.
   *
   * @param value	true if to use
   */
  public void setUsePreProcessor(boolean value) {
    m_UsePreProcessor = value;
    reset();
  }

  /**
   * Returns whether to use the preprocessor.
   *
   * @return 		true if to use
   */
  public boolean getUsePreProcessor() {
    return m_UsePreProcessor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String usePreProcessorTipText() {
    return "If enabled, uses the supplied preprocessor configurator to generate a preprocessor to be used with the iterator.";
  }

  /**
   * Sets the preprocessor configurator to use.
   *
   * @param value	the configurator
   */
  public void setPreProcessor(DataSetPreProcessorConfigurator value) {
    m_PreProcessor = value;
    reset();
  }

  /**
   * Returns the preprocessor configurator to use.
   *
   * @return 		the configurator
   */
  public DataSetPreProcessorConfigurator getPreProcessor() {
    return m_PreProcessor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String preProcessorTipText() {
    return "The preprocessor configurator to use for generating the dataset preprocessor that generates the data.";
  }

  /**
   * Sets whether to subtract by the column means and divide by the standard deviation.
   *
   * @param value	true if to do
   */
  public void setNormalizeZeroMeanZeroUnitVariance(boolean value) {
    m_NormalizeZeroMeanZeroUnitVariance = value;
    reset();
  }

  /**
   * Returns whether to subtract by the column means and divide by the standard deviation.
   *
   * @return 		true if to do
   */
  public boolean getNormalizeZeroMeanZeroUnitVariance() {
    return m_NormalizeZeroMeanZeroUnitVariance;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String normalizeZeroMeanZeroUnitVarianceTipText() {
    return "If enabled, subtract by the column means and divide by the standard deviation.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "iterator", m_Iterator, "iterator: ");
    if (m_UsePreProcessor || getOptionManager().hasVariableForProperty("usePreProcessor"))
      result += QuickInfoHelper.toString(this, "preProcessor", m_PreProcessor, ", preprocessor: ");

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    result = null;

    m_ActualIterator = m_Iterator.configureDataSetIterator();
    if (m_UsePreProcessor)
      m_ActualIterator.setPreProcessor(m_PreProcessor.configureDataSetPreProcessor());
    if (m_ActualIterator == null)
      result = "Failed to configure dataset iterator!";

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{DataSet.class};
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;
    DataSet	data;

    data = m_ActualIterator.next();
    if (m_NormalizeZeroMeanZeroUnitVariance)
      data.normalizeZeroMeanZeroUnitVariance();
    result = new Token(data);
    if (!m_ActualIterator.hasNext())
      m_ActualIterator = null;

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_ActualIterator != null) && m_ActualIterator.hasNext();
  }
}
