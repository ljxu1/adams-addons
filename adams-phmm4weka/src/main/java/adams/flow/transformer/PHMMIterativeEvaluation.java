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
 * PHMMIterativeEvaluation.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import weka.classifiers.meta.IterativeHMMPropositionalizer;
import weka.utils.IterativeEvaluation;
import adams.flow.provenance.ProvenanceSupporter;

/**
 <!-- globalinfo-start -->
 * Evaluates an IterativeHMMPropositionalizer clasifier on an incoming dataset. The classifier setup being used in the evaluation is a callable 'Classifier' actor.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaEvaluationContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaEvaluationContainer: Evaluation, Model
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
 * &nbsp;&nbsp;&nbsp;default: PHMMIterativeEvaluation
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * <pre>-output &lt;weka.classifiers.evaluation.output.prediction.AbstractOutput&gt; (property: output)
 * &nbsp;&nbsp;&nbsp;The class for generating prediction output; if 'Null' is used, then an Evaluation 
 * &nbsp;&nbsp;&nbsp;object is forwarded instead of a String.
 * &nbsp;&nbsp;&nbsp;default: weka.classifiers.evaluation.output.prediction.Null
 * </pre>
 * 
 * <pre>-classifier &lt;adams.flow.core.CallableActorReference&gt; (property: classifier)
 * &nbsp;&nbsp;&nbsp;The global PHMM classifier actor to evaluate on the input data.
 * &nbsp;&nbsp;&nbsp;default: PHMMClassifier
 * </pre>
 * 
 * <pre>-folds &lt;int&gt; (property: folds)
 * &nbsp;&nbsp;&nbsp;The number of folds to use in the cross-validation; use -1 for leave-one-out 
 * &nbsp;&nbsp;&nbsp;cross-validation (LOOCV).
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-num-iterations &lt;int&gt; (property: numIterations)
 * &nbsp;&nbsp;&nbsp;The number of iterations to perform.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-iteration-step-size &lt;int&gt; (property: iterationStepSize)
 * &nbsp;&nbsp;&nbsp;The step size for the iterations.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PHMMIterativeEvaluation
  extends AbstractPHMMIterativeEvaluation<IterativeHMMPropositionalizer, IterativeEvaluation>
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 1579936614427356310L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Evaluates an IterativeHMMPropositionalizer clasifier on an incoming "
      + "dataset. The classifier setup being used in the evaluation is a "
      + "callable 'Classifier' actor.";
  }

  /**
   * Instantiates a new evaluation object.
   *
   * @return		the evaluation object
   */
  @Override
  protected IterativeEvaluation newEvaluation() {
    return new IterativeEvaluation();
  }
}
