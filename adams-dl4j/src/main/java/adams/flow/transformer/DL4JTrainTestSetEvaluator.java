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
 * DL4JTrainTestSetEvaluator.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Randomizable;
import adams.flow.container.DL4JEvaluationContainer;
import adams.flow.container.DL4JTrainTestSetContainer;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;
import adams.ml.dl4j.datasetiterator.ShufflingDataSetIterator;
import adams.ml.dl4j.iterationlistener.IterationListenerConfigurator;
import adams.ml.dl4j.model.ModelConfigurator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.eval.RegressionEvaluation;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.api.IterationListener;
import org.nd4j.linalg.dataset.DataSet;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Trains a model on an incoming training dataset (from a container) and then evaluates it on the test set (also from a container).<br>
 * The model setup being used in the evaluation is obtained from the callable actor returning a model configurator.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.DL4JTrainTestSetContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.DL4JTrainTestSetContainer: Train, Test, Seed
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
 * &nbsp;&nbsp;&nbsp;default: DL4JTrainTestSetEvaluator
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this 
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical 
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-always-use-container &lt;boolean&gt; (property: alwaysUseContainer)
 * &nbsp;&nbsp;&nbsp;If enabled, always outputs an evaluation container.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-model &lt;adams.flow.core.CallableActorReference&gt; (property: model)
 * &nbsp;&nbsp;&nbsp;The callable model configurator actor to obtain the model from to train 
 * &nbsp;&nbsp;&nbsp;and evaluate on the test data.
 * &nbsp;&nbsp;&nbsp;default: DL4JModelConfigurator
 * </pre>
 * 
 * <pre>-num-epochs &lt;int&gt; (property: numEpochs)
 * &nbsp;&nbsp;&nbsp;The number of epochs to perform.
 * &nbsp;&nbsp;&nbsp;default: 1000
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-mini-batch-size &lt;int&gt; (property: miniBatchSize)
 * &nbsp;&nbsp;&nbsp;The mini-batch size to use; -1 to turn off.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-seed &lt;long&gt; (property: seed)
 * &nbsp;&nbsp;&nbsp;The seed value to use for randomization.
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 * 
 * <pre>-type &lt;CLASSIFICATION|REGRESSION&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of evaluation to perform.
 * &nbsp;&nbsp;&nbsp;default: CLASSIFICATION
 * </pre>
 * 
 * <pre>-iteration-listener &lt;adams.ml.dl4j.iterationlistener.IterationListenerConfigurator&gt; [-iteration-listener ...] (property: iterationListeners)
 * &nbsp;&nbsp;&nbsp;The iteration listeners to use (configurators).
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DL4JTrainTestSetEvaluator
  extends AbstractCallableDL4JModelEvaluator
  implements ProvenanceSupporter, Randomizable {

  /** for serialization. */
  private static final long serialVersionUID = -1092101024095887007L;

  /** the number of epochs. */
  protected int m_NumEpochs;

  /** the minibatch size. */
  protected int m_MiniBatchSize;

  /** the seed value. */
  protected long m_Seed;

  /** the evaluation type. */
  protected DL4JEvaluationType m_Type;

  /** the iteration listeners to use. */
  protected IterationListenerConfigurator[] m_IterationListeners;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Trains a model on an incoming training dataset (from a container) "
	+ "and then evaluates it on the test set (also from a container).\n"
	+ "The model setup being used in the evaluation is obtained from the "
	+ "callable actor returning a model configurator.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "num-epochs", "numEpochs",
      1000, 1, null);

    m_OptionManager.add(
      "mini-batch-size", "miniBatchSize",
      -1, -1, null);

    m_OptionManager.add(
      "seed", "seed",
      1L);

    m_OptionManager.add(
      "type", "type",
      DL4JEvaluationType.CLASSIFICATION);

    m_OptionManager.add(
      "iteration-listener", "iterationListeners",
      new IterationListenerConfigurator[0]);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String modelTipText() {
    return "The callable model configurator actor to obtain the model from to train and evaluate on the test data.";
  }

  /**
   * Sets the number of epochs.
   *
   * @param value	the epochs
   */
  public void setNumEpochs(int value) {
    m_NumEpochs = value;
    reset();
  }

  /**
   * Returns the number of epochs.
   *
   * @return  		the epochs
   */
  public int getNumEpochs() {
    return m_NumEpochs;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numEpochsTipText() {
    return "The number of epochs to perform.";
  }

  /**
   * Sets the minibatch size to use.
   *
   * @param value	the size (-1 to turn off)
   */
  public void setMiniBatchSize(int value) {
    m_MiniBatchSize = value;
    reset();
  }

  /**
   * Returns the type of evaluation to perform.
   *
   * @return  		the type
   */
  public int getMiniBatchSize() {
    return m_MiniBatchSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String miniBatchSizeTipText() {
    return "The mini-batch size to use; -1 to turn off.";
  }

  /**
   * Sets the seed value.
   *
   * @param value	the seed
   */
  public void setSeed(long value) {
    m_Seed = value;
    reset();
  }

  /**
   * Returns the seed value.
   *
   * @return  		the seed
   */
  public long getSeed() {
    return m_Seed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String seedTipText() {
    return "The seed value to use for randomization.";
  }

  /**
   * Sets the type of evaluation to perform.
   *
   * @param value	the type
   */
  public void setType(DL4JEvaluationType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of evaluation to perform.
   *
   * @return  		the type
   */
  public DL4JEvaluationType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of evaluation to perform.";
  }

  /**
   * Sets the iteration listeners to use.
   *
   * @param value	the configurators
   */
  public void setIterationListeners(IterationListenerConfigurator[] value) {
    m_IterationListeners = value;
    reset();
  }

  /**
   * Returns the iteration listeners to use.
   *
   * @return		the configurators
   */
  public IterationListenerConfigurator[] getIterationListeners() {
    return m_IterationListeners;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String iterationListenersTipText() {
    return "The iteration listeners to use (configurators).";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "type", m_Type, "type: ");
    result += QuickInfoHelper.toString(this, "numEpochs", m_NumEpochs, ", epochs: ");
    result += QuickInfoHelper.toString(this, "miniBatchSize", m_MiniBatchSize, ", minibatch: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.container.DL4JTrainTestSetContainer.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{DL4JTrainTestSetContainer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    DataSet 			train;
    DataSet			test;
    ModelConfigurator 		conf;
    Model			model;
    Evaluation 			evalCls;
    RegressionEvaluation 	evalReg;
    DL4JTrainTestSetContainer	cont;
    ShufflingDataSetIterator	iter;
    List<IterationListener> 	listeners;
    int				i;

    result = null;

    try {
      conf = getModelConfiguratorInstance();
      if (conf == null)
	throw new IllegalStateException("Model configurator '" + getModel() + "' not found!");

      cont  = (DL4JTrainTestSetContainer) m_InputToken.getPayload();
      train = (DataSet) cont.getValue(DL4JTrainTestSetContainer.VALUE_TRAIN);
      test  = (DataSet) cont.getValue(DL4JTrainTestSetContainer.VALUE_TEST);
      model = conf.configureModel(train.numInputs(), train.numOutcomes());
      evalCls = null;
      evalReg = null;
      if (model instanceof MultiLayerNetwork) {
	listeners = new ArrayList<>();
	for (IterationListenerConfigurator l: m_IterationListeners)
	  listeners.addAll(l.configureIterationListeners());
	((MultiLayerNetwork) model).setListeners(listeners);
	for (i = 0; i < m_NumEpochs; i++) {
	  if (isLoggingEnabled() && ((i+1) % 100 == 0))
	    getLogger().info("#epoch: " + i);
	  if (m_MiniBatchSize < 1) {
	    ((MultiLayerNetwork) model).fit(train);
	  }
	  else {
	    iter = new ShufflingDataSetIterator(train, m_MiniBatchSize, (int) m_Seed);
	    while (iter.hasNext() && !isStopped())
	      ((MultiLayerNetwork) model).fit(iter.next());
	  }
	  if (isStopped())
	    break;
	}
      }
      else {
	result = "Can only evaluate " + MultiLayerNetwork.class.getName() + "!";
      }
      if ((result == null) && !isStopped()) {
	switch (m_Type) {
	  case CLASSIFICATION:
	    evalCls = new Evaluation(train.numOutcomes());
	    evalCls.eval(test.getLabels(), ((MultiLayerNetwork) model).output(test.getFeatureMatrix(), Layer.TrainingMode.TEST));
	    break;

	  case REGRESSION:
	    evalReg = new RegressionEvaluation(train.numOutcomes());
	    evalReg.eval(test.getLabels(), ((MultiLayerNetwork) model).output(test.getFeatureMatrix(), Layer.TrainingMode.TEST));
	    break;

	  default:
	    throw new IllegalStateException("Unhandled evaluation type: " + m_Type);
	}
      }

      // broadcast result
      if (evalCls != null) {
	if (m_AlwaysUseContainer)
	  m_OutputToken = new Token(new DL4JEvaluationContainer(evalCls, model, m_NumEpochs));
	else
	  m_OutputToken = new Token(evalCls.stats());
      }
      else if (evalReg != null) {
	if (m_AlwaysUseContainer)
	  m_OutputToken = new Token(new DL4JEvaluationContainer(evalReg, model, m_NumEpochs));
	else
	  m_OutputToken = new Token(evalReg.stats());
      }
    }
    catch (Exception e) {
      m_OutputToken = null;
      result = handleException("Failed to evaluate: ", e);
    }

    if (m_OutputToken != null)
      updateProvenance(m_OutputToken);

    return result;
  }

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled()) {
      if (m_InputToken.hasProvenance())
	cont.setProvenance(m_InputToken.getProvenance().getClone());
      cont.addProvenance(new ProvenanceInformation(ActorType.EVALUATOR, m_InputToken.getPayload().getClass(), this, m_OutputToken.getPayload().getClass()));
    }
  }
}
