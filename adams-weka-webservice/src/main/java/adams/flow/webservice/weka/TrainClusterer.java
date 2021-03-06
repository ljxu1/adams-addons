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
 * TrainClusterer.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice.weka;

import adams.flow.webservice.AbstractWebServiceClientTransformer;
import adams.flow.webservice.WebserviceUtils;
import nz.ac.waikato.adams.webservice.weka.TrainClustererResponseObject;
import nz.ac.waikato.adams.webservice.weka.WekaService;
import nz.ac.waikato.adams.webservice.weka.WekaServiceService;

import javax.xml.ws.BindingProvider;
import java.net.URL;

/**
 * Builds a clusterer model.
 * 
 * @author msf8
 * @version $Revision$
 */
public class TrainClusterer 
extends AbstractWebServiceClientTransformer<nz.ac.waikato.adams.webservice.weka.TrainClusterer, String> {

  /** fro serialization*/
  private static final long serialVersionUID = -2621193325978827170L;
  
  /** train cluster input */
  protected nz.ac.waikato.adams.webservice.weka.TrainClusterer m_Train;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
   return "trains a dataset into clusters";
  }

  /**
   * Returns the classes that are accepted input.
   * 
   * @return		the classes that are accepted
   */
  @Override
  public Class[] accepts() {
    return new Class[] {nz.ac.waikato.adams.webservice.weka.TrainClusterer.class};
  }

  /**
   * Sets the data for the request, if any.
   * 
   * @param value	the request data
   */
  @Override
  public void setRequestData(nz.ac.waikato.adams.webservice.weka.TrainClusterer value) {
    m_Train = value;
  }

  /**
   * Returns the classes that this client generates.
   * 
   * @return		the classes
   */
  @Override
  public Class[] generates() {
   return new Class[] {String.class};
  }

  /**
   * Returns the WSDL location.
   * 
   * @return		the location
   */
  @Override
  public URL getWsdlLocation() {
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
    WebserviceUtils.configureClient(
	m_Owner,
	wekaService, 
	m_ConnectionTimeout, 
	m_ReceiveTimeout, 
	(getUseAlternativeURL() ? getAlternativeURL() : null),
	m_InInterceptor,
	m_OutInterceptor);
    //check against schema
    WebserviceUtils.enableSchemaValidation(((BindingProvider) wekaService));
    TrainClustererResponseObject response = wekaService.trainClusterer(m_Train.getDataset(), m_Train.getClusterer(), m_Train.getModelName());
    // failed to generate data?
    if (response.getErrorMessage() != null)
      throw new IllegalStateException(response.getErrorMessage());
    setResponseData(response.getModel());

    m_Train = null;
  }
}
