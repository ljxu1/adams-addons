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
 * RecordReaderConfigurator.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j.recordreader;

import org.datavec.api.records.reader.RecordReader;

import java.io.Serializable;

/**
 * Interface for classes that can configure a {@link RecordReader}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface RecordReaderConfigurator
  extends Serializable {

  /**
   * Configures the {@link RecordReader} and returns it.
   *
   * @return		the iterator
   */
  public RecordReader configureRecordReader();
}
