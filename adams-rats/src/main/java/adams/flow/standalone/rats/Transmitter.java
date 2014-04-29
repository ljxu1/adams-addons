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
 * Transmitter.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats;

import adams.core.Stoppable;
import adams.flow.core.AbstractActor;

/**
 * Interface for classes that transmit data in some fashion.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface Transmitter
  extends Stoppable {

  /**
   * Sets the actor the transmitter belongs to.
   * 
   * @param value	the owner
   */
  public void setOwner(AbstractActor value);

  /**
   * Returns the actor the transmitter belongs to.
   * 
   * @return		the owner
   */
  public AbstractActor getOwner();

  /**
   * Hook method for performing checks.
   * 
   * @throws Exception	if checks fail
   */
  public void check() throws Exception;

  /**
   * Starts the transmission of data.
   * 
   * @throws Execption	if transmitting of data fails
   */
  public void transmit() throws Exception;
}
