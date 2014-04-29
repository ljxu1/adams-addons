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
 * DownloadClassifier.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.webservice;

import java.net.URL;

import javax.xml.ws.BindingProvider;

import nz.ac.waikato.adams.webservice.weka.DownloadClassifierResponseObject;
import nz.ac.waikato.adams.webservice.weka.WekaService;
import nz.ac.waikato.adams.webservice.weka.WekaServiceService;
import weka.classifiers.Classifier;
import adams.core.SerializationHelper;

/**
 * Client for download a classifier model.
 * 
 * @author FracPete (fracpete at waikato ac dot nz)
 * @version $Revision$
 */
public class DownloadClassifier 
extends AbstractWebServiceClientTransformer<nz.ac.waikato.adams.webservice.weka.DownloadClassifier, Classifier>{

  /** for serialization*/
  private static final long serialVersionUID = -4596049331963785695L;

  /** download input object */
  protected nz.ac.waikato.adams.webservice.weka.DownloadClassifier m_Download;

  /** classifier returned */
  protected Classifier m_ReturnedClassifier;
  
  /** response object */
  protected DownloadClassifierResponseObject m_Returned;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Downloads a previously generated classifier model.";
  }

  /**
   * Returns the classes that are accepted input.
   * 
   * @return		the classes that are accepted
   */
  @Override
  public Class[] accepts() {
    return new Class[]{nz.ac.waikato.adams.webservice.weka.DownloadClassifier.class};
  }

  /**
   * Sets the data for the request, if any.
   * 
   * @param value	the request data
   */
  @Override
  public void setRequestData(nz.ac.waikato.adams.webservice.weka.DownloadClassifier value) {
    m_Download = value;

  }

  /**
   * Returns the classes that this client generates.
   * 
   * @return		the classes
   */
  @Override
  public Class[] generates() {
    return new Class[]{Classifier.class};
  }

  /**
   * Checks whether there is any response data to be collected.
   * 
   * @return		true if data can be collected
   * @see		#getResponseData()
   */
  @Override
  public boolean hasResponseData() {
    return m_ReturnedClassifier != null;
  }

  /**
   * Returns the response data, if any.
   * 
   * @return		the response data
   */
  @Override
  public Classifier getResponseData() {
    Classifier result = m_ReturnedClassifier;
    m_ReturnedClassifier = null;
    return result;
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
    WebserviceUtils.configureClient(wekaService, m_ConnectionTimeout, m_ReceiveTimeout);
    //check against schema
    WebserviceUtils.enableSchemaValidation(((BindingProvider) wekaService));
    
    m_Returned = wekaService.downloadClassifier(m_Download.getModelName()); 
    // failed to download model?
    if (m_Returned.getErrorMessage() != null)
      throw new IllegalStateException(m_Returned.getErrorMessage());
    m_ReturnedClassifier = (Classifier) SerializationHelper.read(m_Returned.getModelData().getInputStream());

    m_Download = null;
  }
}
