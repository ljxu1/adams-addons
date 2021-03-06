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
 * AbstractDL4JModelReader.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;
import adams.flow.container.DL4JModelContainer;
import adams.flow.core.Token;
import adams.ml.dl4j.model.ModelType;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

import java.io.File;

/**
 * Ancestor for actors that deserialize models.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDL4JModelReader
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -2949611378612429555L;

  /** the model type. */
  protected ModelType m_Type;

  /** whether to only output the model. */
  protected boolean m_OutputOnlyModel;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      ModelType.MULTI_LAYER_NETWORK);

    m_OptionManager.add(
      "output-only-model", "outputOnlyModel",
      false);
  }

  /**
   * Sets the model type to instantiate.
   *
   * @param value	the type
   */
  public void setType(ModelType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the model type to instantiate.
   *
   * @return 		the type
   */
  public ModelType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of model to instantiate.";
  }

  /**
   * Sets whether to output only the model instead of the container.
   *
   * @param value	true if only to output the model
   */
  public void setOutputOnlyModel(boolean value) {
    m_OutputOnlyModel = value;
    reset();
  }

  /**
   * Returns whether only the model is output instead of the container.
   *
   * @return		true if only the model is output
   */
  public boolean getOutputOnlyModel() {
    return m_OutputOnlyModel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputOnlyModelTipText() {
    return "If enabled, only the model will be output instead of a model container.";
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
    result += QuickInfoHelper.toString(this, "outputOnlyModel", m_OutputOnlyModel, "only model", ", ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class, java.io.File.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Returns the class that the producer generates.
   *
   * @return		the classes it generates
   */
  public Class[] generates() {
    if (m_OutputOnlyModel) {
      switch (m_Type) {
        case MULTI_LAYER_NETWORK:
          return new Class[]{MultiLayerNetwork.class};
        case COMPUTATION_GRAPH:
          return new Class[]{ComputationGraph.class};
        default:
          throw new IllegalStateException("Unhanded model type: " + m_Type);
      }
    }
    else {
      return new Class[]{DL4JModelContainer.class};
    }
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    File			file;
    DL4JModelContainer		cont;
    Model			model;

    result = null;

    if (m_InputToken.getPayload() instanceof File)
      file = (File) m_InputToken.getPayload();
    else
      file = new PlaceholderFile((String) m_InputToken.getPayload());

    try {
      switch (m_Type) {
        case MULTI_LAYER_NETWORK:
          model = ModelSerializer.restoreMultiLayerNetwork(file.getAbsoluteFile());
          break;
        case COMPUTATION_GRAPH:
          model = ModelSerializer.restoreComputationGraph(file.getAbsoluteFile());
          break;
        default:
          throw new IllegalStateException("Unhanded model type: " + m_Type);
      }
      if (m_OutputOnlyModel) {
	m_OutputToken = new Token(model);
      }
      else {
	cont = new DL4JModelContainer(model);
	m_OutputToken = new Token(cont);
      }
    }
    catch (Exception e) {
      result = handleException("Failed to deserialize model data from '" + file + "':", e);
    }

    return result;
  }
}
