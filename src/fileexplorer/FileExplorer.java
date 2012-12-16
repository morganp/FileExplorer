package fileexplorer;


import java.util.Locale;
import java.util.ResourceBundle;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.*;

import com.apple.eawt.*;

//For getting Prefs
import java.util.prefs.*;

import java.awt.event.*;
import java.awt.Color;
import java.io.*;
import javax.swing.event.*;

import fileexplorer.*;
import amaras.runtime.*;
import amaras.icon.*;


public class FileExplorer extends JFrame implements WindowListener {

          String     startLocation;
   static boolean    isMac;
   static int        noMask;
   static int        shiftMask;
   static int        ctrlMask;
   static int        shiftCtrlMask;
   private   Font font = new Font("serif", Font.ITALIC+Font.BOLD, 36);
   protected ResourceBundle resbundle;
   protected AboutBox aboutBox;
   protected PrefPane prefs;
   protected Preferences prefsLoc, prefsView, prefsColour, prefsApp;
   
   private Application fApplication = Application.getApplication();
   protected Action newAction, openAction, closeAction, saveAction, saveAsAction,
   undoAction, cutAction, copyAction, pasteAction, clearAction, selectAllAction;
   
   
   //New Actions
   protected Action newWindowAction, newTabAction, showTransfersAction;
   protected Action showPrefsAction, exitAction;
   
   //Actions for viewMenu List
   protected Action showToolbarAction, showHiddenAction;
   protected Action showAboutAction;
   protected Action[] sortListAction;
  
   //Menu bar is not static, each window has its own menu bar.
   //   They just all look the same
   final JMenuBar mainMenuBar = new JMenuBar();   
   
   //Menus
   protected JMenu fileMenu, editMenu, viewMenu, helpMenu; 
   
   //View Menu Items
   protected JCheckBoxMenuItem showToolbarMenuItem;
    
   //Menu Items
   protected JCheckBoxMenuItem hiddenMenuItem;
   protected JMenuItem[]       m_sort;
   
   //Top Level Components
   protected FileExplorerJToolBar          toolbar;
   protected JPanel            masterPanel;
   //protected BrowserPane       browsePanel;
   protected JTabbedPane browserTabs;

          boolean showToolbar;
   static int     windowCount = -1;
          int     initialWidth;
          int     initialHeight;
          int     initialX;
          int     initialY;
   
   

   static TransferList transferlist;
   //Paste Buffer
   private static PasteQue    pasteque ;
   private static PasteWorker pasteworker;
   
    
   
   public FileExplorer(String startLocation) {
      super("");
      this.startLocation = startLocation;
      //Call parameter less constructor
      helperConstructor();
   }
   
   public FileExplorer() {
      super("");
      helperConstructor();
   }
   
   private void helperConstructor() {

     initialWidth  = 400;
     initialHeight = 600;
     initialX      = 20;
     initialY      = 40;
     if (windowCount == -1) {
        windowCount = 1;
     } else {
        windowCount++;
     }
     System.out.println();
     System.out.println("Window count " + windowCount);
     System.out.println();

     //Trying to setup Icon for mnimization 
     ImageIcon logo = new ImageIcon("./images/fe_logo.gif");
     setIconImage(logo.getImage());
     
     //Add window listener for Custom Close Frame
     addWindowListener(this);
     //Booleans never have a null value
     //this should speed up Mac operstion a tiny bit
     if (isMac == false) {
        if (System.getProperty("os.name").contains("Mac") ) {
           isMac = true;
        } else {
         isMac = false;
        }
      
         if ( isMac ) {
            noMask        = 0;
            shiftMask     = 1;
            ctrlMask      = 4;
            shiftCtrlMask = 5;
         } else {
            noMask        = 0;
            shiftMask     = 1;
            ctrlMask      = 2;
            shiftCtrlMask = 3;
         }
      }
      
      // The ResourceBundle below contains all of the strings used in this
      // application.  ResourceBundles are useful for localizing applications.
      // New localities can be added by adding additional properties files.
      resbundle = ResourceBundle.getBundle ("strings", Locale.getDefault());
      setTitle(resbundle.getString("frameConstructor"));
      //this.getContentPane().setLayout(null);
      
      
      if (transferlist == null) {
         transferlist = new TransferList(resbundle.getString("transferList"));
      }

      //Paste Thread setup
      if (pasteque == null) {
         pasteque    = new amaras.runtime.PasteQue(transferlist);
      }
      
      if (pasteworker == null) {
         pasteworker = new amaras.runtime.PasteWorker(pasteque);
         //Start the PasteBuffer thread
         pasteworker.start();
      }
      
      

      //Load prefs
      prefsView   = Preferences.userRoot().node("net/amaras/fileexplorer/view"); 
      prefsLoc    = Preferences.userRoot().node("net/amaras/fileexplorer/location"); 
      prefsApp    = Preferences.userRoot().node("net/amaras/fileexplorer/application"); 
      
      showToolbar = prefsView.getBoolean(fileexplorer.pref.View.SHOW_TOOLBAR, 
                    fileexplorer.pref.View.SHOW_TOOLBAR_DEFAULT);
      
      //Setup Tool Bar
      toolbar     = new FileExplorerJToolBar(this);

      //Setup Panels
      masterPanel = new JPanel();
      masterPanel.setLayout(new BoxLayout(masterPanel, BoxLayout.Y_AXIS));

      //Set Top left Icon
//      String logo_path = "../images/fe_logo1.gif";

//      amaras.icon.SetupIcon logo2 = new SetupIcon(logo_path, "logo2");
      
//      ImageIcon logo = createImageIconLocal(logo_path, "logo1");
//      setIconImage(logo.getImage());
      //~ setIconImage(logo2.getImageIcon().getImage());

      // Get property from pref menu   
      String location;
      if (startLocation == null) {
      location = prefsLoc.get(fileexplorer.pref.Location.START_LOCATION, 
                              fileexplorer.pref.Location.START_LOCATION_DEFAULT);
      } else {
         location = startLocation;
      }
      //TODO insert this in to a tabbed panel
      browserTabs = new JTabbedPane(JTabbedPane.TOP); 
      
      BrowserPane browsePanel = new BrowserPane(location, initialWidth, pasteque, this);
      browserTabs.add(browsePanel);

      toolbar.setVisible(showToolbar);
      masterPanel.add(toolbar);
      masterPanel.add(browserTabs);
      //masterPanel.add(browsePanel);
      this.getContentPane().add(masterPanel);


      //Add Menus
      createActions();
      addMenus();
      
      //Only do the wierd mac menu on macs
      if ( isMac == true) {
         fApplication.setEnabledPreferencesMenu(true);
         fApplication.addApplicationListener(new com.apple.eawt.ApplicationAdapter() {
            public void handleAbout(ApplicationEvent e) {
               if (aboutBox == null) {
                  aboutBox = new AboutBox();
               }
               about(e);
               e.setHandled(true);
            }
            public void handleOpenApplication(ApplicationEvent e) {
            }
            public void handleOpenFile(ApplicationEvent e) {
            }
            public void handlePreferences(ApplicationEvent e) {
               if (prefs == null) {
                  prefs = new PrefPane();
               }
               preferences(e);
            }
            public void handlePrintFile(ApplicationEvent e) {
            }
            public void handleQuit(ApplicationEvent e) {
               JFrame frame = new JFrame();
               JOptionPane.showMessageDialog(frame, "CMD-Q disabled: " +
                  "Use CMD-SHIFT-W to close windows will quit " +
                  "on last window closing");
               System.out.println("Command Q exit disabled " +
                                  "cmd-shift-w to close a window");

            }
         });
         
      }
      //Quit when last window exits 
      //Call custom close method so exit and close operations can be synced up
      this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

      //Spring loaded Bottom, never drops off screen
      //Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      //int maxHeightAllowed = (int) dim.getHeight();
      ////get screen position
      ////get app height
      //int panelBottom = masterPanel.getX() + masterPanel.getHeight(); 
      //if (panelBottom>maxHeightAllowed) {
      //   System.out.println("Bottom out of range");
      //} else {
      //   System.out.println("Bottom in of range");
      //}
      masterPanel.setVisible(true);
      
      setSize(initialWidth, initialHeight);
      setLocation(initialX, initialY);
      masterPanel.setVisible(true);
      setVisible(true);
      
      this.getActiveBrowserWindow().setBrowserFocus();
   }


   //Overiding 
   public void doLayout() {
      super.doLayout();
      //masterPanel.setVisible(false);   
   }
   
   public void about(ApplicationEvent e) {
      aboutBox.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      aboutBox.setResizable(false);
      aboutBox.setVisible(true);
   }

   public void about() {
      if (aboutBox == null) {
         aboutBox = new AboutBox();
      }
      aboutBox.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      aboutBox.setResizable(false);
      aboutBox.setVisible(true);
   }
   
   public void preferences(ApplicationEvent e) {
      prefs.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      prefs.setResizable(false);
      prefs.setVisible(true);
   }
   
   public void preferences() {
      if (prefs == null) {
         prefs = new PrefPane();
      }
      prefs.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      prefs.setResizable(false);
      prefs.setVisible(true);
   }

   public void newTab() {
      initialWidth  = 400;
      
      String location;
      if (startLocation == null) {
         location = prefsLoc.get(fileexplorer.pref.Location.START_LOCATION, 
                              fileexplorer.pref.Location.START_LOCATION_DEFAULT);
      } else {
         location = startLocation;
      }
      
      BrowserPane browsePanel2 = new BrowserPane(location, initialWidth, pasteque, this);
      browserTabs.add(browsePanel2);
   }
   
   public void newTab(String location) {
      initialWidth  = 400;
      
      BrowserPane browsePanel2 = new BrowserPane(location, initialWidth, pasteque, this);
      browserTabs.add(browsePanel2);
   }
   
   public void quit(ApplicationEvent e) {  
      System.out.println("quit(e)");
      quit();
   }

   public void quit() { 
      System.out.println("quit()");
      System.out.println("" + windowCount);
      int fixedWindowCount = windowCount;
      if (fixedWindowCount > 1) {
         --windowCount; 
         this.dispose();
      }
      if ((fixedWindowCount <= 1)&&(!pasteque.joblistComplete())) {
         JFrame frame = new JFrame();
         JOptionPane.showMessageDialog(frame, "Uncompleted Transfers, cant quit until they complete");

      }
      //Check all copy/paste jobs finished before exiting  
      if ((fixedWindowCount <= 1)&&(pasteque.joblistComplete())) {
         System.exit(0);
      }
   }

   
   //###########################################################################
   //##  Window Listener methods 
   //###########################################################################
   public void windowClosing(WindowEvent e) {
//      System.out.println("WindowListener method called: windowClosing.");
      quit();
   }
   
   public void windowClosed(WindowEvent e) {
//      System.out.println("WindowListener method called: windowClosed.");
   }

   public void windowOpened(WindowEvent e) {
//      System.out.println("WindowListener method called: windowOpened.");
   }

   public void windowIconified(WindowEvent e) {
//      System.out.println("WindowListener method called: windowIconified.");
   }

   public void windowDeiconified(WindowEvent e) {
//      System.out.println("WindowListener method called: windowDeiconified.");
   }

   public void windowActivated(WindowEvent e) {
//      System.out.println("WindowListener method called: windowActivated.");
   }

   public void windowDeactivated(WindowEvent e) {
//      System.out.println("WindowListener method called: windowDeactivated.");
   }

    
   public void createActions() {
      int shortcutKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

      //Create actions that can be used by menus, buttons, toolbars, etc.

      //File Menu
      newWindowAction = new newWindowActionClass( resbundle.getString("newWindow"), 
            KeyStroke.getKeyStroke(KeyEvent.VK_N, shortcutKeyMask) );
      
      newTabAction    = new newTabActionClass( resbundle.getString("newTab"), 
            KeyStroke.getKeyStroke(KeyEvent.VK_T, shortcutKeyMask) );
      
      showTransfersAction    = new showTransfersActionClass( resbundle.getString("transferList"), 
              KeyStroke.getKeyStroke(KeyEvent.VK_J, shortcutKeyMask) );
      
      if (isMac == false) {
         //non-Mac items
         showPrefsAction = new showPrefsActionClass( resbundle.getString("showPrefsWindow"));
      }
      
      exitAction = new exitActionClass (resbundle.getString("exitWindow"),
            KeyStroke.getKeyStroke(KeyEvent.VK_W, shiftCtrlMask) );
      
      //~ newAction = new newActionClass( resbundle.getString("newItem"),
            //~ KeyStroke.getKeyStroke(KeyEvent.VK_T, shortcutKeyMask) );
      //~ openAction = new openActionClass( resbundle.getString("openItem"),
            //~ KeyStroke.getKeyStroke(KeyEvent.VK_O, shortcutKeyMask) );
      //~ closeAction = new closeActionClass( resbundle.getString("closeItem"),
            //~ KeyStroke.getKeyStroke(KeyEvent.VK_W, shortcutKeyMask) );
      //~ saveAction = new saveActionClass( resbundle.getString("saveItem"),
            //~ KeyStroke.getKeyStroke(KeyEvent.VK_S, shortcutKeyMask) );
      //~ saveAsAction = new saveAsActionClass( resbundle.getString("saveAsItem") );
      
      
      
      //Edit Menu
      undoAction = new undoActionClass( resbundle.getString("undoItem"),
            KeyStroke.getKeyStroke(KeyEvent.VK_Z, shortcutKeyMask) );
      cutAction = new cutActionClass( resbundle.getString("cutItem"),
            KeyStroke.getKeyStroke(KeyEvent.VK_X, shortcutKeyMask) );
      copyAction = new copyActionClass( resbundle.getString("copyItem"),
            KeyStroke.getKeyStroke(KeyEvent.VK_C, shortcutKeyMask) );
      pasteAction = new pasteActionClass( resbundle.getString("pasteItem"),
            KeyStroke.getKeyStroke(KeyEvent.VK_V, shortcutKeyMask) );
      clearAction = new clearActionClass( resbundle.getString("clearItem") );
      selectAllAction = new selectAllActionClass( resbundle.getString("selectAllItem"),
            KeyStroke.getKeyStroke(KeyEvent.VK_A, shortcutKeyMask) );
      
      

      //ViewMenu Actions
//      transferList      = new transferListClass( resbundle.getString("transferList") );
      showToolbarAction = new showToolbarActionClass("Toolbar");
      showHiddenAction  = new showHiddenActionClass("Show Hidden");
      sortListAction    = new sortListActionClass[amaras.filesort.DirectoryView.SORT_OPTIONS];
      for (int i=0; i<amaras.filesort.DirectoryView.SORT_OPTIONS ; i++){
         sortListAction[i] = new sortListActionClass("Sort", i);
      }
      

      //HelpMenu
      if (isMac == false) {
         //non-Mac items
         showAboutAction = new showAboutActionClass( resbundle.getString("showAbout"));
      }
   }

   public void addMenus() {

      fileMenu = new JMenu(resbundle.getString("fileMenu"));
      //fileMenu.add(new JMenuItem(newAction));
      //fileMenu.add(new JMenuItem(openAction));
      //fileMenu.add(new JMenuItem(closeAction));
      //fileMenu.add(new JMenuItem(saveAction));
      //fileMenu.add(new JMenuItem(saveAsAction));
      fileMenu.add(new JMenuItem(newWindowAction));
      
      JMenuItem menuNewTab = new JMenuItem(newTabAction);
      menuNewTab.setEnabled (true); 
      fileMenu.add(menuNewTab);
      
      if (isMac == false) {
         //Mac these fuctions are part of main menu.
         fileMenu.addSeparator();
         fileMenu.add(new JMenuItem(showPrefsAction));
      }
      
      fileMenu.addSeparator();
      fileMenu.add(new JMenuItem(exitAction));
      
      mainMenuBar.add(fileMenu);
      
      editMenu = new JMenu(resbundle.getString("editMenu"));
      //editMenu.add(new JMenuItem(undoAction));
      //editMenu.addSeparator();
      editMenu.add(new JMenuItem(cutAction));
      editMenu.add(new JMenuItem(copyAction));
      editMenu.add(new JMenuItem(pasteAction));
      editMenu.add(new JMenuItem(clearAction));
      editMenu.addSeparator();
      editMenu.add(new JMenuItem(selectAllAction));
      mainMenuBar.add(editMenu);

      viewMenu = new JMenu(resbundle.getString("viewMenu"));
      viewMenu.add(new JMenuItem(showTransfersAction));
      viewMenu.addSeparator();
      
      showToolbarMenuItem = new JCheckBoxMenuItem(resbundle.getString("showToolbar"));
      showToolbarMenuItem.setSelected(showToolbar);
      showToolbarMenuItem.addActionListener(showToolbarAction);
      viewMenu.add(showToolbarMenuItem);
      viewMenu.addSeparator();
      
      hiddenMenuItem = new JCheckBoxMenuItem(resbundle.getString("showHidden"));
      hiddenMenuItem.setSelected(prefsView.getBoolean(
                  fileexplorer.pref.View.SHOW_HIDDEN, fileexplorer.pref.View.SHOW_HIDDEN_DEFAULT));
      hiddenMenuItem.addActionListener(showHiddenAction);
      viewMenu.add( hiddenMenuItem );
      
      viewMenu.addSeparator();
      
      //Todo prefs define default selection
      ButtonGroup sortGroup = new ButtonGroup();
      m_sort = new JMenuItem[amaras.filesort.DirectoryView.SORT_OPTIONS];
      int defMode = prefsView.getInt(fileexplorer.pref.View.SORT_MODE, fileexplorer.pref.View.SORT_MODE_DEFAULT);
      for (int i=0; i<m_sort.length; i++) {
         m_sort[i] = new JRadioButtonMenuItem(amaras.filesort.DirectoryView.text[i]);
         m_sort[i].setSelected(i == defMode);
         m_sort[i].addActionListener(sortListAction[i]);
         sortGroup.add(m_sort[i]);
         viewMenu.add(m_sort[i]);
      }
      
      mainMenuBar.add(viewMenu);

      helpMenu = new JMenu(resbundle.getString("helpMenu"));
      if (isMac == false) {
         helpMenu.add(new JMenuItem(showAboutAction));
      }
      mainMenuBar.add(helpMenu);
      setJMenuBar (mainMenuBar);
   }
   
   
   public void paint(Graphics g) {
      super.paint(g);
      g.setColor(Color.blue);
      g.setFont (font);
      //g.drawString(resbundle.getString("message"), 40, 80);

      //Spring loaded Bottom, never drops off screen
      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      int maxHeightAllowed = (int) dim.getHeight();
      //get screen position
      //get app height
      int panelBottom = masterPanel.getY() + masterPanel.getHeight(); 
      //if (panelBottom>maxHeightAllowed) {
      //   System.out.println("Bottom out of range");
      //} else {
      //   System.out.println("Bottom in of range");
      //}
   }


   public class newActionClass extends AbstractAction {
      public newActionClass(String text, KeyStroke shortcut) {
         super(text);
         putValue(ACCELERATOR_KEY, shortcut);
      }
      public void actionPerformed(ActionEvent e) {
         System.out.println("New...");
      }
   }

   public class openActionClass extends AbstractAction {
      public openActionClass(String text, KeyStroke shortcut) {
         super(text);
         putValue(ACCELERATOR_KEY, shortcut);
      }
      public void actionPerformed(ActionEvent e) {
         System.out.println("Open...");
      }
   }

   public class closeActionClass extends AbstractAction {
      public closeActionClass(String text, KeyStroke shortcut) {
         super(text);
         putValue(ACCELERATOR_KEY, shortcut);
      }
      public void actionPerformed(ActionEvent e) {
         System.out.println("Close...");
      }
   }

   public class saveActionClass extends AbstractAction {
      public saveActionClass(String text, KeyStroke shortcut) {
         super(text);
         putValue(ACCELERATOR_KEY, shortcut);
      }
      public void actionPerformed(ActionEvent e) {
         System.out.println("Save...");
      }
   }

   public class saveAsActionClass extends AbstractAction {
      public saveAsActionClass(String text) {
         super(text);
      }
      public void actionPerformed(ActionEvent e) {
         System.out.println("Save As...");
      }
   }

   public class undoActionClass extends AbstractAction {
      public undoActionClass(String text, KeyStroke shortcut) {
         super(text);
         putValue(ACCELERATOR_KEY, shortcut);
      }
      public void actionPerformed(ActionEvent e) {
         System.out.println("Undo...");
      }
   }

   public class cutActionClass extends AbstractAction {
      public cutActionClass(String text, KeyStroke shortcut) {
         super(text);
         putValue(ACCELERATOR_KEY, shortcut);
      }
      public void actionPerformed(ActionEvent e) {
         System.out.println("Cut...");
         getActiveBrowserWindow().cutAction();
      }
   }

   public class copyActionClass extends AbstractAction {
      public copyActionClass(String text, KeyStroke shortcut) {
         super(text);
         putValue(ACCELERATOR_KEY, shortcut);
      }
      public void actionPerformed(ActionEvent e) {
         System.out.println("Copy...");
            getActiveBrowserWindow().copyAction();
      }
   }

   public class pasteActionClass extends AbstractAction {
      public pasteActionClass(String text, KeyStroke shortcut) {
         super(text);
         putValue(ACCELERATOR_KEY, shortcut);
      }
      public void actionPerformed(ActionEvent e) {
         System.out.println("Paste...");
            getActiveBrowserWindow().pasteAction();
      }
   }

   public class clearActionClass extends AbstractAction {
      public clearActionClass(String text) {
         super(text);
      }
      public void actionPerformed(ActionEvent e) {
         System.out.println("Clear...");
         getActiveBrowserWindow().clearAction();
      }
   }

   public class selectAllActionClass extends AbstractAction {
      public selectAllActionClass(String text, KeyStroke shortcut) {
         super(text);
         putValue(ACCELERATOR_KEY, shortcut);
      }
      public void actionPerformed(ActionEvent e) {
         System.out.println("Select All...");
         getActiveBrowserWindow().selectAllAction();
      }
   }

   public class newWindowActionClass extends AbstractAction {
      public newWindowActionClass(String text, KeyStroke shortcut) {
         super(text);
         putValue(ACCELERATOR_KEY, shortcut);
      }
      public void actionPerformed(ActionEvent e) {
         System.out.println("New Window");
         new FileExplorer();
      }
   }
   
   public class newTabActionClass extends AbstractAction {
      public newTabActionClass(String text, KeyStroke shortcut) {
         super(text);
         putValue(ACCELERATOR_KEY, shortcut);
      }
      public void actionPerformed(ActionEvent e) {
         System.out.println("New Tab");
         newTab();
      }
   }
   
   public class showTransfersActionClass extends AbstractAction {
      public showTransfersActionClass(String text, KeyStroke shortcut) {
         super(text);
         putValue(ACCELERATOR_KEY, shortcut);
      }
      public void actionPerformed(ActionEvent e) {
         transferlist.toggleVisible();
      }
   }
   
   public class exitActionClass extends AbstractAction {
      public exitActionClass(String text) {
         super(text);
      }
      public exitActionClass(String text, KeyStroke shortcut) {
         super(text);
         putValue(ACCELERATOR_KEY, shortcut);
      }
      
      public void actionPerformed(ActionEvent e) {
         quit();
      }
   }

   public class sortListActionClass extends AbstractAction {
      int newMode;
      public sortListActionClass(String text, int i) {
         super(text);
         this.newMode = i;
      }
      public void actionPerformed(ActionEvent e) {
         System.out.println("Changing File Sort Option " + newMode);
         getActiveBrowserWindow().sortAction(newMode);
      }
   }
   
   public class showToolbarActionClass extends AbstractAction {
      public showToolbarActionClass(String text) {
         super(text);
      }
      public void actionPerformed(ActionEvent e) {
         toolbar.setVisible(showToolbarMenuItem.isSelected());
      }
   }
   
   public class showHiddenActionClass extends AbstractAction {
      public showHiddenActionClass(String text) {
         super(text);
      }
      public void actionPerformed(ActionEvent e) {
         getActiveBrowserWindow().showHidden(hiddenMenuItem.isSelected());
      }
   }
   
   public class showPrefsActionClass extends AbstractAction {
      public showPrefsActionClass(String text) {
         super(text);
      }
      public void actionPerformed(ActionEvent e) {
         preferences();
      }
   }
   
   public class showAboutActionClass extends AbstractAction {
      public showAboutActionClass(String text) {
         super(text);
      }
      public void actionPerformed(ActionEvent e) {
         about();
      }
   }
   
   //http://java.sun.com/docs/books/tutorial/uiswing/components/icon.html
   // Returns an ImageIcon, or null if the path was invalid.
   protected ImageIcon createImageIconLocal(String path,
                                           String description) {
    java.net.URL imgURL = getClass().getResource(path);
    if (imgURL != null) {
        return new ImageIcon(imgURL, description);
    } else {
        System.err.println("FileExplorer.java:createImageIconLocal Couldn't find file: " + path);
        return null;
    }
}
   
   public void setCurrentTabName(){
      int activePos = browserTabs.getSelectedIndex() ;
		if (activePos == -1) {
			activePos = 0;
		}
		try {
			String name = getActiveBrowserWindow().getTabName();
      	browserTabs.setTitleAt(activePos, name);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Caught ArrayIndexOutOfBoundsException");
			System.out.println("   FileExplorer.java:setCurrentTabName Tab:" + activePos);
		} catch (NullPointerException e) {
			System.out.println("Caught NullPointerException");
			System.out.println("   FileExplorer.java:setCurrentTabName Tab:" + activePos);
		}
		System.out.println("Trying to Rename Tab: " + activePos);
   }
   
   public BrowserPane getActiveBrowserWindow() {
      try {
         
         return (BrowserPane) browserTabs.getSelectedComponent(); //browserPane.getActivePane();
      } catch (java.lang.ArrayIndexOutOfBoundsException e){
         System.out.println("FileExplorer.java:getActiveBrowserWindow Caught OutOfBounds exception");
         System.err.println(e);
      } catch (java.lang.NullPointerException e) {
         System.out.println("FileExplorer.java:getActiveBrowserWindow Caught Null Pointer exception");
         System.err.println(e);
      }
      //Always return something stops the method calls failing
      return new BrowserPane("",0,pasteque,this);
   }

   public static void main(String args[]) {
      System.out.println("FileExplorer  Copyright (C) 2009  Morgan Prior");
      System.out.println("This program comes with ABSOLUTELY NO WARRANTY; ");
      System.out.println("   This is free software, and you are welcome to redistribute it");
      System.out.println("   under certain conditions;");
      FileExplorer window = new FileExplorer();
   }

}
