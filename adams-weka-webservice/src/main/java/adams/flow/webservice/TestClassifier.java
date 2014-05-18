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
 * TestClassifier.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.webservice;

import java.net.URL;

import javax.xml.ws.BindingProvider;

import nz.ac.waikato.adams.webservice.weka.Dataset;
import nz.ac.waikato.adams.webservice.weka.TestClassifierResponseObject;
import nz.ac.waikato.adams.webservice.weka.WekaService;
import nz.ac.waikato.adams.webservice.weka.WekaServiceService;

/**
 * client for the test webservice.
 * 
 * @author msf8
 * @version $Revision$
 */
public class TestClassifier 
extends AbstractWebServiceClientTransformer<nz.ac.waikato.adams.webservice.weka.TestClassifier, Dataset>{

  /** for serialization*/
  private static final long serialVersionUID = 1L;

  /** input object for the test web service */
  protected nz.ac.waikato.adams.webservice.weka.TestClassifier m_Test;

  /** dataset returned after testing  */
  protected Dataset m_ReturnedDataset;
  
  /**object response */
  protected TestClassifierResponseObject m_Returned;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Tests a dataset and returns the evaluation as a dataset.";
  }

  /**
   * Returns the classes that are accepted input.
   * 
   * @return		the classes that are accepted
   */
  @Override
  public Class[] accepts() {
    return new Class[] {nz.ac.waikato.adams.webservice.weka.TestClassifier.class};
  }

  /**
   * Sets the data for the request, if any.
   * 
   * @param value	the request data
   */
  @Override
  public void setRequestData(nz.ac.waikato.adams.webservice.weka.TestClassifier value) {
    m_Test = value;
  }

  /**
   * Returns the classes that this client generates.
   * 
   * @return		the classes
   */
  @Override
  public Class[] generates() {
    return new Class[] {Dataset.class};
  }

  /**
   * Checks whether there is any response data to be collected.
   * 
   * @return		true if data can be collected
   * @see		#getResponseData()
   */
  @Override
  public boolean hasResponseData() {
    return m_ReturnedDataset != null;
  }

  /**
   * Returns the response data, if any.
   * 
   * @return		the response data
   */
  @Override
  public Dataset getResponseData() {
    Dataset toReturn = m_ReturnedDataset;
    m_ReturnedDataset = null;
    return toReturn;
  }

  /**
   * Returns the WSDL location.
   * 
   * @return		the location
   */
  @Override
  protected URL getWsdlLocation() {
    return getClass().getClassLoader().getResource("wsdl/weka/WekaService.wsdl");

  }

  /**
   * Performs the actual webservice query.
   * 
   * @throws Exception	if accessing webservice fails for some reason
   */
  @Override
  protected void doQuery() throws Exception {
    WekaServiceService wekaServiceService;
    WekaService wekaService;
    wekaServiceService = new WekaServiceService(getWsdlLocation());
    wekaService = wekaServiceService.getWekaServicePort();
    WebserviceUtils.configureClient(wekaService, m_ConnectionTimeout, m_ReceiveTimeout, getUseAlternativeURL() ? getAlternativeURL() : null);
    //check against schema
    WebserviceUtils.enableSchemaValidation(((BindingProvider) wekaService));
    m_Returned = wekaService.testClassifier(m_Test.getDataset(), m_Test.getModelName()); 
    // failed to generate data?
    if (m_Returned.getErrorMessage() != null)
      throw new IllegalStateException(m_Returned.getErrorMessage());
    m_ReturnedDataset = m_Returned.getReturnDataset();
    
    m_Test = null;
  }
}
