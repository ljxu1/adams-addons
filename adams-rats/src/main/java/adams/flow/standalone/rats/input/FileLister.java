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
 * FileLister.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.input;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.io.DirectoryLister;
import adams.core.io.DirectoryLister.Sorting;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;

/**
 <!-- globalinfo-start -->
 * Polls files in a directory and forwards them.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-source &lt;adams.core.io.PlaceholderDirectory&gt; (property: source)
 * &nbsp;&nbsp;&nbsp;The directory to watch for incoming files.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-regexp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression that the files must match.
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 * 
 * <pre>-max-files &lt;int&gt; (property: maxFiles)
 * &nbsp;&nbsp;&nbsp;The maximum number of files to list; -1 for unlimited.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-sorting &lt;NO_SORTING|SORT_BY_NAME|SORT_BY_LAST_MODIFIED&gt; (property: sorting)
 * &nbsp;&nbsp;&nbsp;The type of sorting to perform.
 * &nbsp;&nbsp;&nbsp;default: NO_SORTING
 * </pre>
 * 
 * <pre>-sort-descending &lt;boolean&gt; (property: sortDescending)
 * &nbsp;&nbsp;&nbsp;If enabled, the sort direction is descending.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-wait-list &lt;int&gt; (property: waitList)
 * &nbsp;&nbsp;&nbsp;The number of milli-seconds to wait after listing the files.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-move-files &lt;boolean&gt; (property: moveFiles)
 * &nbsp;&nbsp;&nbsp;If enabled, the files get moved to the specified directory first before 
 * &nbsp;&nbsp;&nbsp;being transmitted (with their new filename).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-target &lt;adams.core.io.PlaceholderDirectory&gt; (property: target)
 * &nbsp;&nbsp;&nbsp;The directory to move the files to before transmitting their names.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileLister
  extends AbstractRatInput {

  /** for serialization. */
  private static final long serialVersionUID = 4089376907540465883L;

  /** the lister for listing the files. */
  protected DirectoryLister m_Lister;
  
  /** the located files. */
  protected List<String> m_Files;

  /** the waiting period in msec after listing the files. */
  protected int m_WaitList;

  /** whether to move the files before transmitting them. */
  protected boolean m_MoveFiles;
  
  /** the directory to move the files to. */
  protected PlaceholderDirectory m_Target;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Polls files in a directory and forwards them.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "source", "source",
	    new PlaceholderDirectory());

    m_OptionManager.add(
	    "regexp", "regExp",
	    new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
	    "max-files", "maxFiles",
	    -1, -1, null);

    m_OptionManager.add(
	    "sorting", "sorting",
	    Sorting.NO_SORTING);

    m_OptionManager.add(
	    "sort-descending", "sortDescending",
	    false);

    m_OptionManager.add(
	    "wait-list", "waitList",
	    0, 0, null);

    m_OptionManager.add(
	    "move-files", "moveFiles",
	    false);

    m_OptionManager.add(
	    "target", "target",
	    new PlaceholderDirectory());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Lister = new DirectoryLister();
    m_Lister.setListDirs(false);
    m_Lister.setListFiles(true);
    m_Lister.setRecursive(false);
    
    m_Files = new ArrayList<String>();
  }

  /**
   * Sets the incoming directory.
   *
   * @param value	the incoming directory
   */
  public void setSource(PlaceholderDirectory value) {
    m_Lister.setWatchDir(value);
    reset();
  }

  /**
   * Returns the incoming directory.
   *
   * @return		the incoming directory.
   */
  public PlaceholderDirectory getSource() {
    return m_Lister.getWatchDir();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sourceTipText() {
    return "The directory to watch for incoming files.";
  }

  /**
   * Sets the regular expression for the files.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_Lister.setRegExp(value);
    reset();
  }

  /**
   * Returns the regular expression for the files.
   *
   * @return		the regular expression
   */
  public BaseRegExp getRegExp() {
    return m_Lister.getRegExp();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression that the files must match.";
  }

  /**
   * Sets the maximum number of files to list.
   *
   * @param value	the maximum, -1 for unlimited
   */
  public void setMaxFiles(int value) {
    m_Lister.setMaxItems(value);
    reset();
  }

  /**
   * Returns the maximum number of files to list.
   *
   * @return		the maximum, -1 for unlimited
   */
  public int getMaxFiles() {
    return m_Lister.getMaxItems();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxFilesTipText() {
    return "The maximum number of files to list; -1 for unlimited.";
  }

  /**
   * Sets the sorting type.
   *
   * @param value 	the sorting
   */
  public void setSorting(Sorting value) {
    m_Lister.setSorting(value);
    reset();
  }

  /**
   * Returns the sorting type.
   *
   * @return 		the sorting
   */
  public Sorting getSorting() {
    return m_Lister.getSorting();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sortingTipText() {
    return "The type of sorting to perform.";
  }

  /**
   * Sets whether to sort in descending manner.
   *
   * @param value 	true if desending sort manner
   */
  public void setSortDescending(boolean value) {
    m_Lister.setSortDescending(value);
    reset();
  }

  /**
   * Returns whether to sort in descending manner.
   *
   * @return 		true if descending sort manner
   */
  public boolean getSortDescending() {
    return m_Lister.getSortDescending();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sortDescendingTipText() {
    return "If enabled, the sort direction is descending.";
  }

  /**
   * Sets the number of milli-seconds to wait after listing the files.
   *
   * @param value	the number of milli-seconds
   */
  public void setWaitList(int value) {
    if (value >= 0) {
      m_WaitList = value;
      reset();
    }
    else {
      getLogger().warning("Number of milli-seconds to wait must be >=0, provided: " + value);
    }
  }

  /**
   * Returns the number of milli-seconds to wait after listing the files.
   *
   * @return		the number of milli-seconds
   */
  public int getWaitList() {
    return m_WaitList;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String waitListTipText() {
    return "The number of milli-seconds to wait after listing the files.";
  }

  /**
   * Sets whether to move the files to the specified target directory
   * before transmitting them.
   *
   * @param value	true if to move files
   */
  public void setMoveFiles(boolean value) {
    m_MoveFiles = value;
    reset();
  }

  /**
   * Returns whether to move the files to the specified target directory
   * before transmitting them.
   *
   * @return		true if to move files
   */
  public boolean getMoveFiles() {
    return m_MoveFiles;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String moveFilesTipText() {
    return 
	"If enabled, the files get moved to the specified directory first "
	+ "before being transmitted (with their new filename).";
  }

  /**
   * Sets the move-to directory.
   *
   * @param value	the move-to directory
   */
  public void setTarget(PlaceholderDirectory value) {
    m_Target = value;
    reset();
  }

  /**
   * Returns the move-to directory.
   *
   * @return		the move-to directory.
   */
  public PlaceholderDirectory getTarget() {
    return m_Target;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String targetTipText() {
    return "The directory to move the files to before transmitting their names.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "source", getSource(), "source: ");
    result += QuickInfoHelper.toString(this, "regExp", getRegExp(), ", regexp: ");
    result += QuickInfoHelper.toString(this, "waitList", getWaitList(), ", wait-list: ");
    result += QuickInfoHelper.toString(this, "moveFiles", (getMoveFiles() ? "move" : "keep"), ", ");
    result += QuickInfoHelper.toString(this, "target", getTarget(), ", target: ");
    
    return result;
  }
  
  /**
   * Hook method for performing checks. Makes sure that directories exist.
   * 
   * @throws Exception	if checks fail
   */
  @Override
  public String check() {
    String	result;
    
    result = super.check();
    
    if ((result == null) && (!getSource().exists()))
      result = "Source directory does not exist: " + getSource();
    if ((result == null) && (!getSource().isDirectory()))
      result ="Source is not a directory: " + getSource();
    
    if (m_MoveFiles) {
      if ((result == null) && (!getTarget().exists()))
	result = "Target directory does not exist: " + getTarget();
      if ((result == null) && (!getTarget().isDirectory()))
	result ="Target is not a directory: " + getTarget();
    }
    
    return result;
  }

  /**
   * Returns the type of data this scheme generates.
   * 
   * @return		the type of data
   */
  @Override
  public Class generates() {
    return String.class;
  }

  /**
   * Checks whether any output can be collected.
   * 
   * @return		true if output available
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_Files.size() > 0);
  }

  /**
   * Returns the received data.
   * 
   * @return		the data
   */
  @Override
  public Object output() {
    return m_Files.remove(0);
  }

  /**
   * Performs the actual reception of data.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doReceive() {
    String		result;
    String[]		files;
    int			i;
    PlaceholderFile	file;
    
    result = null;
    
    files = m_Lister.list();
    doWait(m_WaitList);
    
    if (m_MoveFiles) {
      for (i = 0; i < files.length; i++) {
	file = new PlaceholderFile(files[i]);
	try {
	  if (!FileUtils.move(file, m_Target))
	    result = "Failed to move '" + file + "' to '" + m_Target + "'!";
	  else
	    files[i] = m_Target.getAbsolutePath() + File.separator + file.getName();
	}
	catch (Exception e) {
	  result = "Failed to move '" + file + "' to '" + m_Target + "': " + Utils.throwableToString(e);
	}
	if (result != null)
	  break;
      }
    }
    
    if (result == null)
      m_Files.addAll(Arrays.asList(files));
    
    return result;
  }
}