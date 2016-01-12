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
 * AnnotatorPanel.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.annotator;

import adams.core.*;
import adams.core.Properties;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.action.AbstractBaseAction;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.chooser.SpreadSheetFileChooser;
import adams.gui.core.*;
import adams.gui.dialog.EditBindingsDialog;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.visualization.video.vlcjplayer.VLCjPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.security.InvalidKeyException;
import java.util.*;
import java.util.List;

/**
 * Panel for viewing and annotating videos
 *
 * @author sjb90
 * @version $Revision$
 */
public class AnnotatorPanel extends BasePanel
  implements MenuBarProvider, CleanUpHandler {

  /**
   * the file to store the recent video files in.
   */
  public final static String VIDEO_SESSION_FILE = "AnnotatorVideoSession.props";


  public final static String BINDINGS_SESSION_FILE = "AnnotatorBindingSession.props";


  private static final long serialVersionUID = 6965340882268141821L;

  /** the list to store the bindings */
  private List<Binding> m_Bindings;

  /** a title generator */
  private TitleGenerator m_TitleGenerator;

  /** a video player panel */
  private VLCjPanel m_VideoPlayer;

  /** a panel for the annotation bindings */
  private BasePanel m_BindingPanel;

  /** dialog */
  private EditBindingsDialog m_BindingsDialog;

  /** menu bad */
  protected JMenuBar m_MenuBar;

  /** menu item for 'open */
  protected JMenuItem m_MenuItemFileOpen;

  /**
   * for handling recent files.
   */
  protected RecentFilesHandler<JMenu> m_RecentVideosHandler;
  protected JMenu m_MenuFileLoadRecentVideos;
  protected JMenuItem m_MenuItemFileClose;

  /**
   * Mute action
   */
  protected AbstractBaseAction m_ActionMute;

  /**
   * Play action
   */
  protected AbstractBaseAction m_ActionPlay;

  /**
   * Pause action
   */
  protected AbstractBaseAction m_ActionPause;

  /**
   * Stop action
   */
  protected AbstractBaseAction m_ActionStop;

  /** New Bindings action. */
  protected AbstractBaseAction m_ActionNewBindings;

  /**
   * Edit Bindings action
   */
  protected AbstractBaseAction m_ActionEditBindings;

  /**
   * New Trail action
   */
  protected AbstractBaseAction m_ActionNewTrail;

  /**
   * Export trail
   */
  protected AbstractBaseAction m_ActionExportTrail;

  /**
   * save bindings
   */
  protected AbstractBaseAction m_ActionSaveBindings;

  /**
   * open bindings
   */
  protected  AbstractBaseAction m_ActionLoadBindings;

  /**
   * Date formater for outputing timestamps
   */
  protected DateFormat m_dateFormatter;

  protected JMenuItem m_MenuItemVideoPlay;
  protected JMenuItem m_MenuItemVideoStop;

  /** the file chooser for exporting trails. */
  protected SpreadSheetFileChooser m_ExportFileChooser;

  /** the file chooser for saving bindings. */
  protected BaseFileChooser m_SavePropertiesFileChooser;

  /** the file chooser for saving bindings. */
  protected BaseFileChooser m_LoadPropertiesFileChooser;

  /** the queue that handles binding events */
  protected EventQueue m_EventQueue;

  /** handler for recent bindings */
  protected RecentFilesHandler<JMenu> m_RecentBindingsHandler;

  /** recent bindings menu */
  protected JMenu m_MenuFileLoadRecentBindings;


  @Override
  protected void initialize() {
    super.initialize();
    m_TitleGenerator 		= new TitleGenerator("Annotator", true);
    m_dateFormatter  		= new DateFormat("HH:mm:ss");
    m_Bindings 			= new ArrayList<>();
    m_ExportFileChooser 	= new SpreadSheetFileChooser();
    m_SavePropertiesFileChooser = new BaseFileChooser();
    m_SavePropertiesFileChooser.setAcceptAllFileFilterUsed(false);
    m_SavePropertiesFileChooser.setAutoAppendExtension(true);
    m_SavePropertiesFileChooser.addChoosableFileFilter(ExtensionFileFilter.getPropertiesFileFilter());
    m_SavePropertiesFileChooser.setDefaultExtension("props");
    m_LoadPropertiesFileChooser = new BaseFileChooser();
    m_LoadPropertiesFileChooser.setAcceptAllFileFilterUsed(false);
    m_LoadPropertiesFileChooser.setAutoAppendExtension(true);
    m_LoadPropertiesFileChooser.addChoosableFileFilter(ExtensionFileFilter.getPropertiesFileFilter());
    m_LoadPropertiesFileChooser.setDefaultExtension("props");
    m_EventQueue		= new EventQueue();
    initActions();
  }

  /**
   * for initializing actions
   */
  protected void initActions(){
    AbstractBaseAction action;

    // Mute action
    action = new AbstractBaseAction("Mute", "mute.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	m_VideoPlayer.mute();
      }
    };

    action.setMnemonic(KeyEvent.VK_M);
    action.setAccelerator("ctrl pressed M");
    m_ActionMute = action;

    // Play action
    action = new AbstractBaseAction("Play", "run.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	m_VideoPlayer.play();
      }
    };
    action.setMnemonic(KeyEvent.VK_P);
    action.setAccelerator("ctrl pressed P");
    m_ActionPlay = action;

    // Pause action
    action = new AbstractBaseAction("Pause", "pause.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	m_VideoPlayer.pause();
      }
    };
    action.setMnemonic(KeyEvent.VK_U);
    action.setAccelerator("ctrl pressed U");
    m_ActionPause = action;

    // Stop action
    action = new AbstractBaseAction("Stop", "stop_blue.gif" ) {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	m_VideoPlayer.stop();
      }
    };
    action.setMnemonic(KeyEvent.VK_S);
    action.setAccelerator("ctrl pressed S");
    m_ActionStop = action;

    // New Bindings
    action = new AbstractBaseAction("New", "new.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	newBindings();
      }
    };
    m_ActionNewBindings = action;

    // Bindings editor
    action = new AbstractBaseAction("Edit...", "properties.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	editBindings();
      }
    };
    m_ActionEditBindings = action;

    action = new AbstractBaseAction("Export...", "spreadsheet.png") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	export();
      }
    };
    m_ActionExportTrail = action;

    // Save Bindings
    action = new AbstractBaseAction("Save as...", "save.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	saveBindings();
      }
    };
    m_ActionSaveBindings = action;

    // open Bindings
    action = new AbstractBaseAction("Open...", "open.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	loadBindings();
      }
    };
    m_ActionLoadBindings = action;

    // New Trail
    action = new AbstractBaseAction("New", "new.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	m_EventQueue.resetTrail();
      }
    };
    m_ActionNewTrail = action;
  }

  @Override
  protected void initGUI() {
    super.initGUI();
    m_VideoPlayer    = new VLCjPanel();
    m_BindingPanel   = new BasePanel(new FlowLayout());
    add(m_VideoPlayer, BorderLayout.CENTER);
    add(m_BindingPanel, BorderLayout.SOUTH);
  }

  @Override
  protected void finishInit() {
    super.finishInit();
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return the menu bar
   */
  public JMenuBar getMenuBar() {
    JMenuBar  result;
    JMenu     menu;
    JMenu     submenu;
    JMenuItem menuitem;

    if (m_MenuBar == null) {
      result = new JMenuBar();

      // Video
      menu = new JMenu("Video");
      result.add(menu);
      menu.setMnemonic('V');
      menu.addChangeListener(e -> updateMenu());

      // Video/Open
      menuitem = new JMenuItem("Open...", GUIHelper.getIcon("open.gif"));
      menuitem.setMnemonic('O');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed O"));
      menuitem.addActionListener(e -> {
	if (m_VideoPlayer.open()) {
	  m_EventQueue.resetTrail();
	  if(m_RecentVideosHandler != null)
	    m_RecentVideosHandler.addRecentItem(m_VideoPlayer.getCurrentFile());
	}
      });
      menu.add(menuitem);
      m_MenuItemFileOpen = menuitem;

      // Video/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentVideosHandler = new RecentFilesHandler<>(VIDEO_SESSION_FILE, 5, submenu);
      m_RecentVideosHandler.addRecentItemListener(new RecentItemListener<JMenu, File>() {
	@Override
	public void recentItemAdded(RecentItemEvent<JMenu, File> e) {
	  // ignored
	}

	@Override
	public void recentItemSelected(RecentItemEvent<JMenu, File> e) {
	  m_VideoPlayer.open(e.getItem());
	}
      });
      m_MenuFileLoadRecentVideos = submenu;

      //menu.addSeparator();

//      // Video/Play
//      menuitem = new JMenuItem(m_ActionPlay);
//      menuitem.setEnabled(false);
//      m_MenuItemVideoPlay = menuitem;
//      menu.add(menuitem);
//
//      // Video/Stop
//      menuitem = new JMenuItem(m_ActionStop);
//      menuitem.setEnabled(false);
//      m_MenuItemVideoStop = menuitem;
//      menu.add(menuitem);
//
//      menu.addSeparator();

      // Video/Quit
      menuitem = new JMenuItem("Quit", GUIHelper.getIcon("exit.png"));
      menuitem.setMnemonic('Q');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.addActionListener(e -> close());
      m_MenuItemFileClose = menuitem;
      menu.addSeparator();
      menu.add(menuitem);

      menu = new JMenu("Annotations");
      result.add(menu);
      menu.setMnemonic('A');
      menu.addChangeListener(e -> updateMenu());

      // Annotations/New
      menuitem = new JMenuItem(m_ActionNewTrail);
      menu.add(menuitem);

      // Annotations/Export
      menuitem = new JMenuItem(m_ActionExportTrail);
      menu.add(menuitem);

      // Bindings
      menu = new JMenu("Bindings");
      result.add(menu);
      menu.setMnemonic('B');
      menu.addChangeListener(e -> updateMenu());

      // Bindings/New Bindings
      menuitem = new JMenuItem(m_ActionNewBindings);
      menu.add(menuitem);

      // Bindings/Open Bindings
      menuitem = new JMenuItem(m_ActionLoadBindings);
      menu.add(menuitem);

      // Bindings/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentBindingsHandler = new RecentFilesHandler<>(BINDINGS_SESSION_FILE, 5, submenu);
      m_RecentBindingsHandler.addRecentItemListener(new RecentItemListener<JMenu, File>() {
	@Override
	public void recentItemAdded(RecentItemEvent<JMenu, File> e) {
	  // ignored
	}

	@Override
	public void recentItemSelected(RecentItemEvent<JMenu, File> e) {
	  loadBindings(e.getItem().getAbsolutePath());
	}
      });
      m_MenuFileLoadRecentBindings = submenu;

      // Bindings/Edit Bindings
      menuitem = new JMenuItem(m_ActionEditBindings);
      menu.add(menuitem);

      // Bindings/Save Bindings
      menuitem = new JMenuItem(m_ActionSaveBindings);
      menu.add(menuitem);

      m_MenuBar = result;
    } else {
      result = m_MenuBar;
    }
    return result;
  }

  /**
    * Updates title and menu items.
  */
  protected void update() {
    updateTitle();
    updateMenu();
  }

  /**
   * Updates the title of the dialog.
   */
  protected void updateTitle() {
    Runnable run;

    if (!m_TitleGenerator.isEnabled())
      return;

    run = () -> {
      String title = m_TitleGenerator.generate(m_VideoPlayer.getCurrentFile());
      setParentTitle(title);
    };
    SwingUtilities.invokeLater(run);
  }

  /**
   * Updates the state of the menu items.
   */
  protected void updateMenu() {
    if (m_MenuBar == null)
      return;
  }

  @Override
  public void cleanUp() {
    if (m_BindingsDialog != null) {
      m_BindingsDialog.dispose();
      m_BindingsDialog = null;
    }
    if (m_VideoPlayer != null) {
      m_VideoPlayer.cleanUp();
    }
    List<Component> comps = new ArrayList<>(Arrays.asList(m_BindingPanel.getComponents()));
    comps.stream().filter(c -> c instanceof AnnotationPanel).forEach(c -> {
      ((AnnotationPanel) c).cleanUp();
    });

    if (m_EventQueue != null)
      m_EventQueue.cleanUp();
  }

  /**
   * Updates the binding bar to contain an indicator for every binding
   */
  private void updateBindingBar() {
    Runnable run = () -> {
      AnnotationPanel panel;
      m_BindingPanel.removeAll();
      for (Binding item : m_Bindings) {
	panel = new AnnotationPanel();
	panel.configureAnnotationPanel(item, m_VideoPlayer);
	m_BindingPanel.add(panel);
	m_BindingPanel.revalidate();
	if(m_EventQueue != null)
	  panel.addListener(m_EventQueue);
      }
      invalidate();
      revalidate();
    };
    SwingUtilities.invokeLater(run);
  }



  /**
   * Sets the base title to use for the title generator.
   *
   * @param value the title to use
   * @see #m_TitleGenerator
   */
  public void setTitle(String value) {
    m_TitleGenerator.setTitle(value);
    //update();
  }

  /**
   * Closes the dialog, if possible.
   */
  protected void close() {
    if (getParentDialog() != null)
      getParentDialog().setVisible(false);
    else if (getParentFrame() != null)
      getParentFrame().setVisible(false);
    cleanUp();
    closeParent();
  }

  /**
   * Resets the bindings.
   */
  public void newBindings() {
    m_Bindings = new ArrayList<>();
    updateBindingBar();
  }

  /**
   * Pops up dialog for editing bindings
   */
  public void editBindings() {
    if(getParentDialog() != null)
      m_BindingsDialog = new EditBindingsDialog(getParentDialog(), Dialog.ModalityType.DOCUMENT_MODAL);
    else
      m_BindingsDialog = new EditBindingsDialog(getParentFrame(), true);
    m_BindingsDialog.setBindings(m_Bindings);
    m_BindingsDialog.setLocationRelativeTo(this);
    m_BindingsDialog.setVisible(true);
    m_Bindings = m_BindingsDialog.getBindings();
    updateBindingBar();
  }

  /**
   * Exports the current trail to a spreadsheet file that the user selects.
   */
  public void export() {
    int retVal;
    SpreadSheet sheet;
    SpreadSheetWriter writer;

    if (m_EventQueue == null)
      return;

    retVal = m_ExportFileChooser.showSaveDialog(this);
    if (retVal != SpreadSheetFileChooser.APPROVE_OPTION)
      return;

    sheet = m_EventQueue.toSpreadSheet();
    writer = m_ExportFileChooser.getWriter();
    if (!writer.write(sheet, m_ExportFileChooser.getSelectedFile()))
      GUIHelper.showErrorMessage(this, "Failed to export data to: " + m_ExportFileChooser.getSelectedFile());
  }

  /**
   * Loads bindings from a file selected by the user
   */
  public void loadBindings() {
    int retVal;

    retVal = m_LoadPropertiesFileChooser.showOpenDialog(this);
    if(retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    String bindingPath = m_LoadPropertiesFileChooser.getSelectedFile().getAbsolutePath();
    loadBindings(bindingPath);
  }

  /**
   * Loads bindings from a file selected by the user
   */
  public void loadBindings(String bindingPath){
    Properties props = new Properties();
    props.load(bindingPath);

    // Clear the current bindings
    m_Bindings = new ArrayList<>();
    // Convert to bindings
    int count = props.getInteger("Count");
    Binding b;
    for( int i = 0; i < count; i++) {
      try {
	KeyStroke keyStroke = GUIHelper.getKeyStroke(props.getProperty(i + ".Binding"));
	b = new Binding(props.getProperty(i + ".Name"),
	  keyStroke, props.getBoolean(i + ".Toggleable"), props.getLong(i + ".Interval"), props.getBoolean(i + ".Inverted"));
	m_Bindings.add(b);
      }
      catch(InvalidKeyException e) {
	System.err.println(e.getMessage());
      }
    }
    updateBindingBar();

    if(m_RecentBindingsHandler != null)
      m_RecentBindingsHandler.addRecentItem(new File(bindingPath));
  }

  /**
   * Saves the current bindings to a file selected by the user
   */
  public void saveBindings() {
    int retVal;
    int i;
    Properties props = new Properties();

    retVal = m_SavePropertiesFileChooser.showSaveDialog(this);
    if(retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    for(i = 0; i < m_Bindings.size(); i++) {
      props.add(m_Bindings.get(i).toProperty(i));
    }
    props.setInteger("Count", i);
    props.save(m_SavePropertiesFileChooser.getSelectedFile().getAbsolutePath());
  }
}
