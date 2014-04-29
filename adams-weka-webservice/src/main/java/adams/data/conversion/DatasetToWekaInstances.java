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
 * DatasetToWekaInstances.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import nz.ac.waikato.adams.webservice.weka.Dataset;
import weka.core.Instances;
import adams.flow.core.DatasetHelper;

/**
 * Converts a webservice {@link Dataset} object into a WEKA {@link Instances} object.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DatasetToWekaInstances
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = -3724897556268123018L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts a webservice Dataset object into a WEKA Instances object.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Dataset.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return Instances.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    return DatasetHelper.toInstances((Dataset) m_Input);
  }
}
