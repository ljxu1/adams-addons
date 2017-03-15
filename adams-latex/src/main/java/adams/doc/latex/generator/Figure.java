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
 * Figure.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.doc.latex.generator;

/**
 <!-- globalinfo-start -->
 * Inserts a figure with the code generated by the base generator.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-no-var-expansion &lt;boolean&gt; (property: noVariableExpansion)
 * &nbsp;&nbsp;&nbsp;If enabled, variable expansion gets skipped.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-generator &lt;adams.doc.latex.generator.AbstractCodeGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;Generates the code for the figure.
 * &nbsp;&nbsp;&nbsp;default: adams.doc.latex.generator.Image
 * </pre>
 * 
 * <pre>-options &lt;java.lang.String&gt; (property: options)
 * &nbsp;&nbsp;&nbsp;The options for the figure.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-center &lt;boolean&gt; (property: center)
 * &nbsp;&nbsp;&nbsp;Whether to center the figure.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-caption &lt;java.lang.String&gt; (property: caption)
 * &nbsp;&nbsp;&nbsp;The caption of the figure.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-label &lt;java.lang.String&gt; (property: label)
 * &nbsp;&nbsp;&nbsp;The optional label of the figure (for cross-referencing).
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Figure
  extends AbstractMetaCodeGenerator {

  private static final long serialVersionUID = -2504232052630130162L;

  /** optional parameters for the image. */
  protected String m_Options;

  /** whether to center the figure. */
  protected boolean m_Center;
    
  /** the caption of the section. */
  protected String m_Caption;

  /** the (optional) label for the section. */
  protected String m_Label;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Inserts a figure with the code generated by the base generator.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "options", "options",
      "");

    m_OptionManager.add(
      "center", "center",
      false);

    m_OptionManager.add(
      "caption", "caption",
      "");

    m_OptionManager.add(
      "label", "label",
      "");
  }

  /**
   * Returns the default code generator to use.
   *
   * @return		the default
   */
  protected AbstractCodeGenerator getDefaultGenerator() {
    return new Image();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String generatorTipText() {
    return "Generates the code for the figure.";
  }

  /**
   * Sets the options to use for the figure.
   *
   * @param value	the options
   */
  public void setOptions(String value) {
    m_Options = value;
    reset();
  }

  /**
   * Returns the options to use for the figure.
   *
   * @return		the options
   */
  public String getOptions() {
    return m_Options;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String optionsTipText() {
    return "The options for the figure.";
  }

  /**
   * Sets whether to center the figure.
   *
   * @param value	true if to center
   */
  public void setCenter(boolean value) {
    m_Center = value;
    reset();
  }

  /**
   * Returns whether to center the figure.
   *
   * @return		true if to center
   */
  public boolean getCenter() {
    return m_Center;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String centerTipText() {
    return "Whether to center the figure.";
  }

  /**
   * Sets the caption.
   *
   * @param value	the caption
   */
  public void setCaption(String value) {
    m_Caption = value;
    reset();
  }

  /**
   * Returns the caption.
   *
   * @return		the caption
   */
  public String getCaption() {
    return m_Caption;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String captionTipText() {
    return "The caption of the figure.";
  }

  /**
   * Sets the label.
   *
   * @param value	the label
   */
  public void setLabel(String value) {
    m_Label = value;
    reset();
  }

  /**
   * Returns the label.
   *
   * @return		the label
   */
  public String getLabel() {
    return m_Label;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelTipText() {
    return "The optional label of the figure (for cross-referencing).";
  }

  /**
   * Generates the actual code.
   *
   * @return		the generated code
   */
  @Override
  protected String doGenerate() {
    StringBuilder	result;

    result = new StringBuilder();
    result.append("\\begin{figure}");
    if (!m_Options.isEmpty())
      result.append("[").append(m_Options).append("]");
    result.append("\n");
    if (m_Center)
      result.append("  \\centering\n");
    result.append("  ").append(m_Generator.generate());
    ensureTrailingNewLine(result);
    if (!m_Caption.isEmpty())
      result.append("  \\caption{").append(expandEscape(m_Caption)).append("}\n");
    if (!m_Label.isEmpty())
      result.append("  \\label{").append(expand(m_Label)).append("}\n");
    result.append("\\end{figure}\n");

    return result.toString();
  }
}
