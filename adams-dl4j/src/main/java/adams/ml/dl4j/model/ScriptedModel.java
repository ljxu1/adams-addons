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
 * ScriptedModel.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.ml.dl4j.model;

import adams.core.scripting.AbstractScriptingHandler;
import adams.core.scripting.Dummy;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.gradient.Gradient;
import org.deeplearning4j.optimize.api.ConvexOptimizer;
import org.deeplearning4j.optimize.api.IterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.primitives.Pair;

import java.util.Collection;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * A model that uses any scripting handler for managing the model in the specified script file.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-script &lt;adams.core.io.PlaceholderFile&gt; (property: scriptFile)
 * &nbsp;&nbsp;&nbsp;The script file to load and execute.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-options &lt;adams.core.base.BaseText&gt; (property: scriptOptions)
 * &nbsp;&nbsp;&nbsp;The options for the script; must consist of 'key=value' pairs separated 
 * &nbsp;&nbsp;&nbsp;by blanks; the value of 'key' can be accessed via the 'getAdditionalOptions
 * &nbsp;&nbsp;&nbsp;().getXYZ("key")' method in the script actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 *
 * <pre>-handler &lt;adams.core.scripting.AbstractScriptingHandler&gt; (property: handler)
 * &nbsp;&nbsp;&nbsp;The handler to use for scripting.
 * &nbsp;&nbsp;&nbsp;default: adams.core.scripting.Dummy
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ScriptedModel
  extends AbstractScriptedModel {

  /** for serialization. */
  private static final long serialVersionUID = 1304903578667689350L;

  /** the loaded script object. */
  protected transient Model m_ModelObject;

  /** the scripting handler to use. */
  protected AbstractScriptingHandler m_Handler;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "A model that uses any scripting handler for managing the "
        + "model in the specified script file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "handler", "handler",
      new Dummy());
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  @Override
  public String scriptOptionsTipText() {
    return
      "The options for the script; must consist of 'key=value' pairs "
        + "separated by blanks; the value of 'key' can be accessed via the "
        + "'getAdditionalOptions().getXYZ(\"key\")' method in the script actor.";
  }

  /**
   * Sets the handler to use for scripting.
   *
   * @param value 	the handler
   */
  public void setHandler(AbstractScriptingHandler value) {
    m_Handler = value;
    reset();
  }

  /**
   * Gets the handler to use for scripting.
   *
   * @return 		the handler
   */
  public AbstractScriptingHandler getHandler() {
    return m_Handler;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String handlerTipText() {
    return "The handler to use for scripting.";
  }

  /**
   * Loads the scripts object and sets its options.
   *
   * @return		null if OK, otherwise the error message
   */
  @Override
  protected String loadScriptObject() {
    Object[]	result;

    result = m_Handler.loadScriptObject(
      Model.class,
      m_ScriptFile,
      m_ScriptOptions,
      getOptionManager().getVariables());
    m_ScriptObject = result[1];

    return (String) result[0];
  }

  /**
   * Checks the script object.
   *
   * @return		null if OK, otherwise the error message
   */
  @Override
  protected String checkScriptObject() {
    // TODO checks?
    return null;
  }

  /**
   * Hook method for checks before the actual execution.
   *
   * @return		null if checks passed, otherwise error message
   */
  @Override
  protected String check() {
    String	result;

    result = super.check();

    if (result == null)
      m_ModelObject = (Model) m_ScriptObject;

    return result;
  }

  /**
   * Frees up memory in a "destructive" non-reversible way.
   */
  @Override
  public void destroy() {
    super.destroy();

    m_ModelObject = null;
  }

  /**
   * Returns the model. Raises an {@link IllegalStateException} if not
   * model object loaded.
   *
   * @return		the model
   */
  protected synchronized Model getModel() {
    if (m_ModelObject != null)
      return m_ModelObject;
    else
      throw new IllegalStateException("No model script loaded!");
  }

  /**
   * Perform one update  applying the gradient
   * @param gradient the gradient to apply
   */
  @Override
  public void update(INDArray gradient, String paramType) {
    getModel().update(gradient, paramType);
  }

  /**
   * The score for the model
   * @return the score for the model
   */
  @Override
  public double score() {
    return getModel().score();
  }

  /**
   * Update the score
   */
  @Override
  public void computeGradientAndScore() {
    getModel().computeGradientAndScore();
  }

  /**
   * Sets a rolling tally for the score. This is useful for mini batch learning when
   * you are accumulating error across a dataset.
   * @param accum the amount to accum
   */
  @Override
  public void accumulateScore(double accum) {
    getModel().accumulateScore(accum);
  }

  /**
   * Parameters of the model (if any)
   * @return the parameters of the model
   */
  @Override
  public INDArray params() {
    return getModel().params();
  }

  /**
   * the number of parameters for the model
   * @return the number of parameters for the model
   *
   */
  @Override
  public int numParams() {
    return getModel().numParams();
  }

  /**
   * the number of parameters for the model
   * @return the number of parameters for the model
   *
   */
  @Override
  public int numParams(boolean backwards) {
    return getModel().numParams(backwards);
  }

  /**
   * Set the parameters for this model.
   * This expects a linear ndarray which then be unpacked internally
   * relative to the expected ordering of the model
   * @param params the parameters for the model
   */
  @Override
  public void setParams(INDArray params) {
    getModel().setParams(params);
  }

  @Override
  public void setParamsViewArray(INDArray indArray) {
    getModel().setParamsViewArray(indArray);
  }

  @Override
  public INDArray getGradientsViewArray() {
    return getModel().getGradientsViewArray();
  }

  @Override
  public void setBackpropGradientsViewArray(INDArray indArray) {
    getModel().setBackpropGradientsViewArray(indArray);
  }

  /**
   * Update learningRate using for this model.
   * Use the learningRateScoreBasedDecay to adapt the score
   * if the Eps termination condition is met
   */
  @Override
  public void applyLearningRateScoreDecay() {
    getModel().applyLearningRateScoreDecay();
  }

  /**
   * Init the model
   */
  @Override
  public void init() {
    getModel().init();
  }

  /**
   * Set the IterationListeners for the ComputationGraph (and all layers in the network)
   */
  @Override
  public void setListeners(Collection<IterationListener> listeners) {
    getModel().setListeners(listeners);
  }

  /**
   * Set the IterationListeners for the ComputationGraph (and all layers in the network)
   */
  @Override
  public void setListeners(IterationListener... listeners) {
    getModel().setListeners(listeners);
  }

  /**
   * This method ADDS additional IterationListener to existing listeners
   *
   * @param listener
   */
  @Override
  public void addListeners(IterationListener... listener) {
    getModel().addListeners(listener);
  }

  /**
   * Fits the model.
   */
  @Override
  public void fit() {
    getModel().fit();
  }

  /**
   * Update layer weights and biases with gradient change
   */
  @Override
  public void update(Gradient gradient) {
    getModel().update(gradient);
  }

  /**
   * Fit the model to the given data
   * @param data the data to fit the model to
   */
  @Override
  public void fit(INDArray data) {
    getModel().fit(data);
  }

  /**
   * Run one iteration
   * @param input the input to iterate on
   */
  @Override
  public void iterate(INDArray input) {
    getModel().iterate(input);
  }

  /**
   * Calculate a gradient
   * @return the gradient for this model
   */
  @Override
  public Gradient gradient() {
    return getModel().gradient();
  }

  /**
   * Get the gradient and score
   * @return the gradient and score
   */
  @Override
  public Pair<Gradient, Double> gradientAndScore() {
    return getModel().gradientAndScore();
  }

  /**
   * The current inputs batch size
   * @return the current inputs batch size
   */
  @Override
  public int batchSize() {
    return getModel().batchSize();
  }

  /**
   * The configuration for the neural network
   * @return the configuration for the neural network
   */
  @Override
  public NeuralNetConfiguration conf() {
    return getModel().conf();
  }

  /**
   * Setter for the configuration
   * @param conf
   */
  @Override
  public void setConf(NeuralNetConfiguration conf) {
    getModel().setConf(conf);
  }

  /**
   * The input/feature matrix for the model
   * @return the input/feature matrix for the model
   */
  @Override
  public INDArray input() {
    return getModel().input();
  }

  /**
   * Validate the input
   */
  @Override
  public void validateInput() {
    getModel().validateInput();
  }

  /**
   * Returns this models optimizer
   * @return this models optimizer
   */
  @Override
  public ConvexOptimizer getOptimizer() {
    return getModel().getOptimizer();
  }

  /**
   * Get the parameter
   * @param param the key of the parameter
   * @return the parameter vector/matrix with that particular key
   */
  @Override
  public INDArray getParam(String param) {
    return getModel().getParam(param);
  }

  /**
   * Initialize the parameters
   */
  @Override
  public void initParams() {
    getModel().initParams();
  }

  /**
   * The param table
   * @return
   */
  @Override
  public Map<String, INDArray> paramTable() {
    return getModel().paramTable();
  }

  /**
   * Table of parameters by key, for backprop
   * For many models (dense layers, etc) - all parameters are backprop parameters
   * @param backpropParamsOnly If true, return backprop params only. If false: return all params (equivalent to
   *                           paramsTable())
   */
  @Override
  public Map<String, INDArray> paramTable(boolean backpropParamsOnly) {
    return getModel().paramTable(backpropParamsOnly);
  }

  /**
   * Setter for the param table
   * @param paramTable
   */
  @Override
  public void setParamTable(Map<String, INDArray> paramTable) {
    getModel().setParamTable(paramTable);
  }

  /**
   * Set the parameter with a new ndarray
   * @param key the key to se t
   * @param val the new ndarray
   */
  @Override
  public void setParam(String key, INDArray val) {
    getModel().setParam(key, val);
  }

  /**
   * Clear input
   */
  @Override
  public void clear() {
    getModel().clear();
  }
}
