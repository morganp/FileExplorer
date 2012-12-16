package fileexplorer;
//
//  BrowserPane.java
//  FileExplorer
/*
 * FileExplorer, Java based file browser.
 * Copyright (C) 2009  Morgan Prior
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact morgan.prior@gmail.com
 *
 */
import java.awt.*;
import java.awt.event.*;
import java.awt.Color;
import javax.swing.*;
import java.io.*;
//import javax.swing.filechooser.*;

//For the textbox listner
import javax.swing.event.*;


import java.util.Vector;

//For getting Prefs
import java.util.prefs.*;

import amaras.runtime.*;
//import javax.swing.text.*;
import java.net.URL;


//implement only required for right click menu
public class BrowserPane extends JPanel implements ItemListener {
   //public static final long serialVersionUID ;
   public  static int textHeight     = 29;
   public  static int labelHeight    = 16;
   public  static int scrollBarWidth = 19;
   private static int debugOn        = 1;

   private FileExplorer parentFileExplorer ;

   //File Sorting Modes
   private   int          sortMode ;
   private   boolean      showHidden;
   
   public    int          initialWidth;
   protected JPanel       topPanel;
   protected JTextField   labelLocation;
    protected String       currentLocation;
   protected JPanel       bodyPanel;
   protected JScrollPane  scrollBodyPanel; //scrolling wrapper
   protected JLabelFile[] listOfFilesLabels;

   protected JLabelStatus message = new JLabelStatus();
   //For multi selects
   protected int          lastPositionSelected = -1;

   //Static PasteBuffer means copy paste works between windows
   //   Only last Cut or Copied files in buffer 
   protected PasteQue pasteBuffer ;
   //Path History
   //Vector history
   
   //Selected files Memory for Ct paste and redraw highlights
   //This must be final so that all windows no what operation you are wanting to perform
   static int pasteMode = 0;
   //Vector Selection
   protected static Vector copycutSelection = new Vector();

   //Icons for Tool bar and controlBar
   protected ImageIcon homeIcon;
   protected ImageIcon upIcon;
   protected ImageIcon folderIcon;
   protected ImageIcon fileIcon;

   boolean           isMac            = false;
   boolean           isWin            = false;
   boolean[]         preShiftSelection ;
   protected boolean browserShiftMode  = false;
   protected int     browserShiftPos   = -1;
   protected boolean browserCtrlMode   = false;
   private   int     noMask            = 0;
   private   int     shiftMask         = 1;
   private   int     ctrlMask          = 0;
   private   int     shiftCtrlMask     = 0;
   
   
   
   
   
   MouseAdapter[] newLocationAction;

   ActionListener locationAction = new ActionListener () {
      public void actionPerformed(ActionEvent e) {
         buildBodyPanel(labelLocation.getText());
      }
   };

   ActionListener homeAction = new ActionListener () {
      public void actionPerformed(ActionEvent e) {
         Preferences prefsLoc  = Preferences.userRoot().node("net/amaras/fileexplorer/location");
         // Get property from pref menu     
         String location = prefsLoc.get(fileexplorer.pref.Location.START_LOCATION, 
                              fileexplorer.pref.Location.START_LOCATION_DEFAULT);   
         buildBodyPanel(location);
      }
   };
   
   ActionListener goAction = new ActionListener () {
      public void actionPerformed(ActionEvent e) {
         buildBodyPanel(labelLocation.getText());
      }
   };

   ActionListener upAction = new ActionListener () {
      public void actionPerformed(ActionEvent e) {
          goUp();
      }
   };

   //TODO return sensible tab names index:last foldername in path!
	//Tab Title/Name
	public String getName() {
		//currentLocation
		return getTabName();
	}
	
	public String getTabName() {
		int startPos   = currentLocation.lastIndexOf(File.separator) + 1;
		int endPos     = currentLocation.length();
		return new String(currentLocation.substring(startPos, endPos));
	}
	
   private void goUp() {
      String temp1 = currentLocation;
            String temp2;
            int pos   = temp1.lastIndexOf(File.separator);
             int first = temp1.indexOf(File.separator);
      
            if ((pos < 0) ){
               //If no/ found limit
               pos = temp1.length();
            } else if ((!isWin)&&(pos == 0)) {
               //If only root / found keep it
               pos = 1;
            } else if ((isWin)&&(pos == 2)) {
               //if Windows
               pos = 3;
                }
            if ((temp1.length() > pos)) {
               temp2 = temp1.substring(0, pos);
               buildBodyPanel(temp2);
            } else {
               File[] roots = File.listRoots();
               Vector root_vec = new Vector();
               root_vec.addAll(java.util.Arrays.asList(roots));
               
                 
               drawBodyPanel(root_vec);
              setStatusMsg("At Root Level");
               //setStatusMsg("No folders above \"" + temp1 + "\"");
            }
   }
   
   MouseListener mouseDownAction = new MouseAdapter() {  
      public void mousePressed(MouseEvent me){ 
         //First restore Focus to correct pane
         setBrowserFocus();
         //Reset Cursor Selection modes
         if (me.getButton() == java.awt.event.MouseEvent.BUTTON1) {
            JLabelFile label = (JLabelFile) me.getComponent();  
            int index   = label.getIndex();
            browserShiftPos = index;
            //Highlight Selection
            
               if (me.isShiftDown() == true) {
                if (browserShiftMode==false) {
   //                  debug("Store Pre Selection");
                   storePreSelection(true);
                }
             clearAction();
             //Restore selction when ctrl is pressed
             if ( ((!isMac)&&(me.isControlDown() == true)) | 
                  (( isMac)&&(me.isMetaDown()    == true)) ){
                resetPreSelection();
             }
             
             
               if (index > lastPositionSelected) {
                  for (int i = index; i>=lastPositionSelected ; i--) {
                     listOfFilesLabels[i].select();
                  }
               } else {
                   for (int i = index; i<=lastPositionSelected ; i++) {
                     listOfFilesLabels[i].select();
                  }
               }
               //Set the Shift mode from cursor based control
               browserShiftMode = true;
               browserShiftPos  = index;
            } 
            if (me.isShiftDown() == false) {
   //            printShiftStatus();
   //            if ((me.isControlDown() == true)&&(browserShiftMode==true)) {
   //               storePreSelection(false);
   //            }
   //            printShiftStatus();
             clearCursorModes();
             lastPositionSelected = browserShiftPos;
                for (int i=0; i<listOfFilesLabels.length ; i++) {
                   if (i == index) {
                      lastPositionSelected = i;
                      if (((!isMac)&&(me.isControlDown() == true) && (listOfFilesLabels[i].isSelected()))||
                       ((isMac) &&(me.isMetaDown()    == true) && (listOfFilesLabels[i].isSelected())) ) {
                         listOfFilesLabels[i].deselect();
                      } else {
                        listOfFilesLabels[i].select();
                      }
                   } else if ( ((!isMac)&&(me.isControlDown() == false)) |
                            (( isMac)&&(me.isMetaDown() == false)) ) {
                     //unless ctrl held down
                     listOfFilesLabels[i].deselect();
                   }
               }
               browserShiftMode     = false;
               lastPositionSelected = index;
            }
            
            if ( ((!isMac)&&(me.isControlDown() == true)) | 
                  (( isMac)&&(me.isMetaDown()    == true)) ){
             browserCtrlMode = true;
             } else {
               browserCtrlMode  = false;
             }

           
         }
      }// if (me.getButton == BUTTON1) {
   };

   MouseListener mouseAction = new MouseAdapter() {  
      public void mouseClicked(MouseEvent me){  
         
         //First restore Focus to correct pane
         setBrowserFocus();
         
         JLabelFile label = (JLabelFile) me.getComponent();  
         String text = label.getText(); 

         //TASK add file to selected Vector for redrawing Selection
         //When Displayed contents are from a search nad not jsut a folder 
         //  Need a special jlabel with full path property.
         
         if (me.getClickCount() == 2) {
            String newLocation ;

            //Also dont add path seperator if locations ends with path seperator
            //~ if (label.getFileLocation().endsWith(File.separator)) {
               //~ //Special case dont double slash root
               //~ newLocation = label.getFileLocation() ;//+ text ;
               
            //~ }  else {
               //~ newLocation = label.getFileLocation() ;//+ File.separator + text ;
            //~ }
                newLocation = label.getFileLocation();
            System.out.println(newLocation);
            
            
            File folder = new File(newLocation);
            //try catch or test for exists, may be add hint pane later
            if (folder.isDirectory()) {
               setStatusMsg("Requesting Folder \"" + newLocation + "\"");
               buildBodyPanel(newLocation);
            } 
            if (folder.isFile()) {
               setStatusMsg("Loading File \"" + newLocation + "\"");
               Vector newLocVec = new Vector();
               newLocVec.add(newLocation);
               Run runCommand = new amaras.runtime.Run(newLocVec);
            }
         }  
      }

   };
    
   public void clearAction(){
      System.out.println("clearAction()");
      //Deselect all files in current window
      for (int i=0;  i<listOfFilesLabels.length; i++){
         listOfFilesLabels[i].deselect();
      }
   }
   
   public void selectAllAction(){
      //Select all files in current window
      for (int i=0;  i<listOfFilesLabels.length; i++){
         listOfFilesLabels[i].select();
      }
   }
   
   public void cutAction() {
      pasteMode = amaras.runtime.CopyCut.CUT;
      copycutSelection.clear();
      //Vector Selection
       for (int i=0;  i<listOfFilesLabels.length; i++){
         if (listOfFilesLabels[i].isSelected()) {
            System.out.println("BroswerPane Cut " + listOfFilesLabels[i].getFullLocation());
            copycutSelection.add(listOfFilesLabels[i].getFullLocation());
         }
       }
      
   }
   
   public void copyAction() {
	
	
	////This seems to be failing to set the pastemode correctly
	
      pasteMode = amaras.runtime.CopyCut.COPY;

		System.out.println("The static variable COPY=" + amaras.runtime.CopyCut.COPY);
		System.out.println("The current Copy mode   =" + pasteMode);
		
      copycutSelection.clear();
      //Vector Selection
       for (int i=0;  i<listOfFilesLabels.length; i++){
         if (listOfFilesLabels[i].isSelected()) {
            System.out.println("BroswerPane Copy " + listOfFilesLabels[i].getFullLocation());
            copycutSelection.add(listOfFilesLabels[i].getFullLocation());
         }
       }
   }
   
   public void createDMGAction() {
	      pasteMode = amaras.runtime.CopyCut.DMG;
	      copycutSelection.clear();
	      //Vector Selection
	       for (int i=0;  i<listOfFilesLabels.length; i++){
	         if (listOfFilesLabels[i].isSelected()) {
	            System.out.println("BroswerPane Copy " + listOfFilesLabels[i].getFullLocation());
	            copycutSelection.add(listOfFilesLabels[i].getFullLocation());
	         }
	       }
	       String destination = getLocationFormatted();
	       for(int i=0; i< copycutSelection.size(); i++) {
	            //TODO Get handler for cut paste from static toplevel
	            pasteBuffer.add(pasteMode, (String) copycutSelection.get(i), destination);
	         }
	       
	   }
   
   private String getLocationFormatted() {
	   String destination = new String();
	      if (labelLocation.getText().endsWith(File.separator)) {
	         //Special case dont double slash root
	         destination = labelLocation.getText() ;
	      }  else {
	         destination = labelLocation.getText() + File.separator;
	      }
	   
	      return destination;
   }
   
   public void pasteAction() {
	  	String destination = getLocationFormatted();
      File folder = new File(destination);
      
		System.out.println("BrowserPane.java:pasteAction() Mode=" + pasteMode + ", Destination=" + destination);
		//try catch or test for exists, may be add hint pane later
      if (folder.isDirectory()) {
         for(int i=0; i< copycutSelection.size(); i++) {
            //TODO Get handler for cut paste from static toplevel
            pasteBuffer.add(pasteMode, (String) copycutSelection.get(i), destination);
         }
      }
      
      prefsTransferPopup();
      
      
      
   }
   
   private void prefsTransferPopup() {
	   Preferences prefsTransfer  = Preferences.userRoot().node("net/amaras/fileexplorer/transfer");
       // Get property from pref menu     
       boolean openTransfer = prefsTransfer.getBoolean(fileexplorer.pref.Transfer.OPEN_TRANSFER, 
                            fileexplorer.pref.Transfer.OPEN_TRANSFER_DEFAULT);
    
       if (openTransfer == true) {
    	   pasteBuffer.transferListSetVisible(true);
       }
   }
   
   public void sortAction(int newMode) {
      System.out.println("Called sortAction" + newMode);
      this.sortMode = newMode;
      reloadBodyPanel();
   }
   
   public void showHidden(boolean set) {
      this.showHidden = set;
      reloadBodyPanel();
   }
   
   //If no selecion then start at bottom
    
  
   public class cursorActionClass extends AbstractAction {
      //0 UP
      //1 Left
      //2 Down
      //3 Right
      
      int     mode;
      boolean ctrl;
      boolean shift;
      
      public cursorActionClass(String text, int mode , boolean shift, boolean ctrl) {
         super(text);
         this.mode  = mode;
         this.shift = shift;
         this.ctrl  = ctrl;
      }
      public void actionPerformed(ActionEvent e) {
         if (mode == 0) {
            cursorSelect(1, shift, ctrl);
         } else if (mode == 1) {
            //System.out.println("Left");
            goUp();
         } else if (mode == 2) {
            cursorSelect(-1, shift,ctrl);
         } else if (mode == 3) {
            //System.out.println("Right");
            goIntoFolder();
         } else {
            System.out.println("Broken");
         }
      }
   }
   
   private void goIntoFolder (){
     
      int selection_count =0;
      for (int i=0; i<listOfFilesLabels.length ; i++){
         if (listOfFilesLabels[i].isSelected()) {
            selection_count++;
         }
      }         
      
      int position = -1;
      if (selection_count == 1) {
         for (int i=0; i<listOfFilesLabels.length ; i++){
            if (listOfFilesLabels[i].isSelected()) {
               position = i;
            }
         }     
         //if only one selected 
         String newLocation = listOfFilesLabels[position].getFileLocation();

         File folder = new File(newLocation);
         //try catch or test for exists, may be add hint pane later
         if (folder.isDirectory()) {
            setStatusMsg("Requesting Folder \"" + newLocation + "\"");
            buildBodyPanel(newLocation);
         } 
      } else {
         setStatusMsg("Right Cursor to move into Folder only works with 1 Selection");
      }
   }
   
   private void cursorSelect(int i, boolean shift, boolean ctrl) {
       //TODO CTRL held and scrolling over pre selected files they are then 
      //nselected rather than keeping there pre cursor move value
//      printShiftStatus();
      
      if((browserShiftMode==true)&&(shift==false)){
         //Complete the last shift selection
//         System.out.println("Setting Shift selected values");
         tempSelectForShift();
         lastPositionSelected = browserShiftPos; 
//         printShiftStatus();
      }
      
//      System.out.print("LastPosition Selected: " + lastPositionSelected);
//      System.out.print(" browserShiftMode: " + browserShiftMode);
//      System.out.print(" browserShiftPos: " + browserShiftPos);
//      System.out.println(" browserCtrlMode: " + browserCtrlMode);
      
      if ( ((ctrl==false)&&( (browserShiftMode==false)||(shift==false)) ) ||
         ((ctrl==true )&&(  browserCtrlMode ==false))||
         ((ctrl==true )&&(shift == false)&&(browserShiftMode == true)) ) {
         System.out.println("Storing pre selected values");
//         printShiftStatus();
         storePreSelection(shift);
//         printShiftStatus();
      }
    
      if (ctrl == true) {
         browserCtrlMode  = true;
      } else {
         browserCtrlMode  = false;
      }
      if (shift == true) {
          if (browserShiftMode==false){
            browserShiftPos = lastPositionSelected;
            browserShiftMode = true;
          }
      } 
      
      // Always clear contents
      clearAction();
      
      //If ctrl down reselect all previous selection
      if (ctrl) {
//          System.out.println("Ctrl down ReSelect values");
//          printShiftStatus(); 
          resetPreSelection();
//          printShiftStatus();
          if ((shift == false)&&(browserShiftMode == false)&&(preShiftSelection[lastPositionSelected]==false)) {
//             System.out.println("Deselecting Last value");
             listOfFilesLabels[lastPositionSelected].deselect();   
          }
//          printShiftStatus();
       }
      
      
      if (i > 0){ 
            setShiftModeTest(shift, browserShiftPos-1);
            if (shift == false) {
               if(lastPositionSelected == -1){
                    //Select bottom location
                     lastPositionSelected = listOfFilesLabels.length-1;
                  } else if (lastPositionSelected > 0) {
                     lastPositionSelected--;
                  } else {
                    lastPositionSelected = 0;
                     setStatusMsg("No More Files Up");
                  }
                listOfFilesLabels[lastPositionSelected].select();
            }
      } 
      if (i <  0) {
            setShiftModeTest(shift, browserShiftPos+1);
            if (shift == false) {
                if (lastPositionSelected == -1){
                   //Select Top location
                   lastPositionSelected = 0;
               } else if (lastPositionSelected < listOfFilesLabels.length-1){
                   lastPositionSelected++;
               } else {
                  lastPositionSelected =listOfFilesLabels.length-1;
                   setStatusMsg("No More Files Down");
               }
                listOfFilesLabels[lastPositionSelected].select();
            }
      }
      
      if (shift) {
         tempSelectForShift();
      } else {
//         debug("Leaving Shift Mode");
          browserShiftMode = false;
      }

//      printShiftStatus();
//      System.out.println();
//      System.out.println();
      
   }
   
   private void storePreSelection(boolean shift){
//      debug("storePreSelection()");
      preShiftSelection = new boolean[listOfFilesLabels.length];
      for (int j=0;  j<listOfFilesLabels.length; j++){
         if ((j!=lastPositionSelected) |
            ((shift==false)&&(browserShiftMode==true)) ){
            preShiftSelection[j] = listOfFilesLabels[j].isSelected();
         }
      }
   }
   
   private void resetPreSelection() {
//      debug("resetPreSelection()");
      for (int j=0;  j<listOfFilesLabels.length; j++){
         if(preShiftSelection[j]) {
            listOfFilesLabels[j].select();         
         }
      }
   }
   
   private void printShiftStatus(){
//      System.out.print("LastPosition Selected: " + lastPositionSelected);
//      System.out.print(" browserShiftMode: " + browserShiftMode);
//      System.out.println(" browserShiftPos: " + browserShiftPos);
      
      System.out.print("Labels: ");
      for(int i=0 ; i<listOfFilesLabels.length; i++){
         if (listOfFilesLabels[i].isSelected()) {
             System.out.print("1");
         } else {
            System.out.print("0");
         }
      }
      System.out.println();
      
      try {
      System.out.print("Select: ");
      for(int i=0 ; i<listOfFilesLabels.length; i++){
         if (preShiftSelection[i]) {
             System.out.print("1");
         } else {
            System.out.print("0");
         }
      }
      System.out.println();
      } catch (NullPointerException e) {
         
      }
      
   }
   
   private void clearCursorModes(){
//      debug("Reset Cursor Mode");
      browserShiftMode= false;
      browserCtrlMode = false;
   }
   
   private void tempSelectForShift(){
//      System.out.println("tempSelectForShift()");
//      printShiftStatus();
      if (lastPositionSelected>browserShiftPos){
            for (int x=lastPositionSelected; x>=browserShiftPos; x-- ) {
                  listOfFilesLabels[x].select();
               }
         } else {
              for (int x=lastPositionSelected; x<=browserShiftPos; x++ ) {
                  listOfFilesLabels[x].select();
               }
         }
   }
   
   private void setShiftModeTest(boolean shift, int value){
//      debug("setShiftModeTest(" +shift +","+value+")");
      if (shift){
         browserShiftPos = value;
      }
   }

   
   public BrowserPane(String location, int initialWidth, PasteQue pasteBuffer, FileExplorer parentFileExplorer) {
      super();
      debug("BrowserPane Constructor");
      this.parentFileExplorer = parentFileExplorer;
      if ( (System.getProperty("os.name").contains("Mac") )) {
         isMac = true;
      } else {
         isMac = false;
      }
      
      if ( (System.getProperty("os.name").contains("Win") )) {
         isWin = true;
      } else {
         isWin = false;
      }
      
      Preferences prefsView = Preferences.userRoot().node("net/amaras/fileexplorer/view"); 
      
      this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      this.initialWidth = initialWidth; 
      this.sortMode = prefsView.getInt(fileexplorer.pref.View.SORT_MODE, fileexplorer.pref.View.SORT_MODE_DEFAULT);
      this.showHidden = prefsView.getBoolean(fileexplorer.pref.View.SHOW_HIDDEN, fileexplorer.pref.View.SHOW_HIDDEN_DEFAULT);
      
      
      this.pasteBuffer = pasteBuffer;
      
      
      
      //Title Pane
      topPanel = new JPanel();
      topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));

      //File list pane
      //Done all inside 

      //TODO move this to a seperate function loading all the used icons
      //http://java.sun.com/developer/techDocs/hi/repository/
      //~ String imgLocation = "toolbarButtonGraphics/navigation/Home24.gif";
      
      String homeIconLocation   = "toolbarButtonGraphics/navigation/Home16.gif";
      String upIconLocation     = "toolbarButtonGraphics/navigation/Up16.gif";
      String folderIconLocation = "toolbarButtonGraphics/general/Open16.gif";
      String fileIconLocation   = "toolbarButtonGraphics/general/Edit16.gif";
      
      
      homeIcon     = createImageIcon(homeIconLocation,   "Home");
      upIcon       = createImageIcon(upIconLocation,     "Up");
      folderIcon   = createImageIcon(folderIconLocation, "Folder");
      fileIcon     = createImageIcon(fileIconLocation,   "File");

      

      
      //Maybe have prefs for icon size and icon or text or both but for now 
      //Hard wired big Icon
      
      //~ JButton homeButton = new JButton(new String("Home"), homeIcon);
      JButton homeButton = new JButton(homeIcon);
      homeButton.addActionListener(homeAction);
      
      labelLocation = new JTextField(location);
      labelLocation.setMinimumSize(new Dimension(50,textHeight));
      //labelLocation.setPreferredSize(new Dimension(200,29));
      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      int maxWidthAllowed = (int) dim.getWidth();
      labelLocation.setMaximumSize(new Dimension(maxWidthAllowed-150,textHeight));
      labelLocation.addActionListener(locationAction);

      
      JButton goButton = new JButton(new String("Go"));
      goButton.addActionListener(goAction);
      JButton upButton = new JButton("Up", upIcon);
      upButton.addActionListener(upAction);

      topPanel.add(homeButton);
      topPanel.add(labelLocation);
      topPanel.add(goButton);
      topPanel.add(upButton);

      bodyPanel = new JPanel();
      bodyPanel.setFocusable(true);
      //Create scrolling window for file list
      scrollBodyPanel = new JScrollPane(bodyPanel);
      //bodyPanel = new JScrollPane();
      //Stop Vertical Scroll bar disappearing
      scrollBodyPanel.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
      scrollBodyPanel.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);



      JPanel status = new JPanel();
      setStatusMsg("Status");
      //Set status to correct size and the ... will apear auto when required
      status.setMinimumSize(   new Dimension(10,textHeight));
      status.setPreferredSize(new Dimension(20,textHeight));
      status.setMaximumSize(   new Dimension(maxWidthAllowed,textHeight));
      status.setLayout(new BoxLayout(status, BoxLayout.X_AXIS));
      status.add(message);      

      this.add(topPanel);
      this.add(scrollBodyPanel);
      this.add(status);

      
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

     //########################################################################
      //Add Cursor Up Action
      scrollBodyPanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, noMask),"up_0_0");
      cursorActionClass  UP_0_0 = new cursorActionClass("UP", 0, false,false);
      scrollBodyPanel.getActionMap().put("up_0_0", UP_0_0);
      
      //UP Shift
      scrollBodyPanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, shiftMask),"up_1_0");
      scrollBodyPanel.getActionMap().put("up_1_0", new cursorActionClass("UP", 0, true,false));
      
      //Up CTRL
      scrollBodyPanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, ctrlMask),"up_0_1");
      scrollBodyPanel.getActionMap().put("up_0_1", new cursorActionClass("UP", 0, false,true));
     
      //Up SHIFT & CTRL
      scrollBodyPanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, shiftCtrlMask),"up_1_1");
      scrollBodyPanel.getActionMap().put("up_1_1", new cursorActionClass("UP", 0, true,true));
      //########################################################################
      
      scrollBodyPanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, noMask),"left");
      scrollBodyPanel.getActionMap().put("left", new cursorActionClass("Left", 1, false,false)); 
      
      
      
      
      
      
      
      //########################################################################
      //Cursor Down Action
      scrollBodyPanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, noMask),"down_0_0");
      scrollBodyPanel.getActionMap().put("down_0_0", new cursorActionClass("Down", 2, false,false));
      
      //Down Shift
      scrollBodyPanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, shiftMask),"down_1_0");
      scrollBodyPanel.getActionMap().put("down_1_0", new cursorActionClass("Down", 2, true,false)); 
      
      //Down Ctrl(Option on Mac)
      scrollBodyPanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, ctrlMask),"down_0_1");
      scrollBodyPanel.getActionMap().put("down_0_1", new cursorActionClass("Down", 2, false,true)); 
      
      //Down Shift & Ctrl
      scrollBodyPanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, shiftCtrlMask),"down_1_1");
      scrollBodyPanel.getActionMap().put("down_1_1", new cursorActionClass("Down", 2, true,true)); 
      
      //########################################################################
      scrollBodyPanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0),"right");
      scrollBodyPanel.getActionMap().put("right", new cursorActionClass("Right", 3, false,false));
      
      //~ scrollBodyPanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT, true),"shift");
      //~ scrollBodyPanel.getActionMap().put("shift", new cursorActionClass("Shift", 4,  true,false));
      
      //~ scrollBodyPanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_CONTROL , true),"ctrl");
      //~ scrollBodyPanel.getActionMap().put("ctrl", new cursorActionClass("Ctrl", 4,  false,true));
      
      scrollBodyPanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0),"space");
      scrollBodyPanel.getActionMap().put("space", new cursorActionClass("Space", 5, false,false));
      
      scrollBodyPanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"enter");
      scrollBodyPanel.getActionMap().put("enter", new cursorActionClass("Enter", 6, false,false));
      
      //Draw after Adding, so that getWidth Methods Work
      buildBodyPanel(location);
      
      
      
      //Make sure initial focus is correct
      scrollBodyPanel.grabFocus();
   }

   public void setBrowserFocus() {
      scrollBodyPanel.grabFocus();
   }
   public void paint(Graphics g) {
      super.paint(g);
      //debug("paint");
      //refreshBodyPanel();
      //resizeAllLabels(); 
      //refreshBodyPanel();      
      //drawBodyPanel(labelLocation.getText());
      resizeAllLabels(50);
      resizeAllLabels();
      message.refreshText(topPanel.getWidth());

   }

   public BrowserPane getActivePane() {
      return this;
   }
   
   public void setStatusMsg(String msg) {
      message.setTextO(msg);
      message.refreshText(topPanel.getWidth());
      message.repaint();
      message.validate();
   }

   public String getStatus() {
      return message.getTextO();
   }
   
   public int reloadBodyPanel(){
      buildBodyPanel(labelLocation.getText());
      return 0;
   }

   public int buildBodyPanel(String location) {
      debug("buildBodyPanel(" + location + ")");
      setStatusMsg("Loading");
      
      //Clear cursor Selection Modes
      clearCursorModes();
      
      //Draw file list
      File folder = new File(location);
      //try catch or test for exists, may be add hint pane later

      if ( folder.isDirectory() ) {
         //Set new Location in address bar
         labelLocation.setText(location);
            currentLocation = location;
         //Clear multi selects history
         lastPositionSelected = -1;

         //File[] listOfFiles = folder.listFiles();
         //Vector vectorOfFiles = new Vector();
         //for (int i=0; i< listOfFiles.length; i++){
         //   vectorOfFiles.add(listOfFiles[i]); //.getName())
         //}
         amaras.filesort.DirectoryView dirView = new amaras.filesort.DirectoryView(folder, showHidden, sortMode);
         Vector vectorOfFiles = dirView.getDirectoryList();
         
         drawBodyPanel(vectorOfFiles);
			
			//Update TabName
			//setCurrentTabName is current tab
			//getTabName is this Tab
			parentFileExplorer.setCurrentTabName();

         setStatusMsg("Loaded " + location);
         return 0;
      } else {
         clearBodyPanel();
         setStatusMsg("ERROR - Requested a non-folder");
         return -1;
      }
   } 
   
   public int drawBodyPanel (Vector vectorOfFile){
      
	//	((FileExplorer) this.getParent() ).setCurrentTabName(getTabName());
		
      listOfFilesLabels = new JLabelFile[vectorOfFile.size()];
      MouseAdapter[] newLocationAction = new MouseAdapter[vectorOfFile.size()];

      //clear current pannel
      clearBodyPanel();
       try {
           for (int i=0; i<vectorOfFile.size(); i++){
              File nextFile = (File) vectorOfFile.get(i);
              String curName = new String();
              if (nextFile.getName().equals("")) {
                 curName = nextFile.toString();
              } else {
                 curName = nextFile.getName();
              }
              
              
              if (nextFile.isDirectory() == true) {
                 listOfFilesLabels[i] = 
                     new JLabelFile(folderIcon, curName, i, nextFile.getCanonicalPath()  );          
              } else {
                 listOfFilesLabels[i] = 
                     new JLabelFile(fileIcon,   curName, i, nextFile.getCanonicalPath()  );          
              }
              listOfFilesLabels[i].addMouseListener(mouseAction);
              listOfFilesLabels[i].addMouseListener(mouseDownAction);
              bodyPanel.add(listOfFilesLabels[i]);
            
              //System.out.println(nextFile.getCanonicalPath());
           }
        } catch (java.io.IOException e) {
           System.err.println(e);
        }
        
        //Add Right Click Menu
        createPopupMenu();
        
         resizeAllLabels(); 
         refreshBodyPanel();
         scrollBodyPanel.grabFocus();
         return 0;      
   }


   
   public void clearBodyPanel () {
      //debug("clearBodyPanel()");
      bodyPanel.removeAll();
      //bodyPanel.repaint();
      //bodyPanel.validate();
      bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
   }
   
   public void refreshBodyPanel (){
      //debug("refreshBodyPanel()");
      //Trigger Layout manager to remove old & show new components
      bodyPanel.repaint();
      bodyPanel.validate();
      scrollBodyPanel.validate();
   }

   public void resizeAllLabels() {
      //debug("resizeAllLabels()");
      int panelWidth = (int) topPanel.getWidth();
      if (panelWidth == 0) {
         panelWidth = initialWidth;
      } 
      //debug("topPanel=" + panelWidth + " Label=" + listOfFilesLabels[0].getWidth());
      resizeAllLabels(panelWidth);
      //debug("Label Size is " + listOfFilesLabels[0].getPreferredSize() + " " + listOfFilesLabels[0].getWidth());
   }


   public void resizeAllLabels(int panelWidth) {
      //BUG
      // This Works when panel is enlarged (not when resize fast)
      // When resized smaller the Jlabel determins panel size
      
      int newPanelWidth = panelWidth - scrollBarWidth;
      //debug("resizeAllLabels (" + newPanelWidth + ")");
      for (int i = 0; i < listOfFilesLabels.length; i++) {
         listOfFilesLabels[i].setWidth(newPanelWidth);
      }
   }

   //http://java.sun.com/docs/books/tutorial/uiswing/components/icon.html
   /** Returns an ImageIcon, or null if the path was invalid. */
   protected ImageIcon createImageIcon(String path,
                                           String description) {
    java.net.URL imgURL = getClass().getClassLoader().getResource(path);
    if (imgURL != null) {
        return new ImageIcon(imgURL, description);
    } else {
        System.err.println("Couldn't find file: " + imgURL.toString());
        return null;
    }
}

   
   private void debug (String text) {
      if (debugOn == 1) {
         System.out.println(text);
      }
   }
   
   //###########################################################################
   
   //Rightclick menu
   public void createPopupMenu() {
       //Create the popup menu.
      JPopupMenu popup = new javax.swing.JPopupMenu();
      
      JMenuItem menuOpen      = new JMenuItem("Open");
      JMenuItem menuOpenWith  = new JMenuItem("Open With");
      JMenuItem menuNewWindow = new JMenuItem("Open in New Window");
      JMenuItem menuNewTab    = new JMenuItem("Open in New Tab");
      JMenuItem menuNewFolder = new JMenuItem("Create New Folder");
      JMenuItem menuNewFile   = new JMenuItem("Create New File");
      JMenuItem menuNewDMG    = new JMenuItem("Create DMG");
      JMenuItem menuCopy      = new JMenuItem("Copy");
      JMenuItem menuCut       = new JMenuItem("Cut");
      JMenuItem menuPaste     = new JMenuItem("Paste");
      JMenuItem menuDelete    = new JMenuItem("Delete");
      JMenuItem menuRename    = new JMenuItem("Rename");
      JMenuItem menuProps     = new JMenuItem("Properties");
      
      //Turn Off unimplemeted fetures
      menuOpen.setEnabled     (true);     
      menuOpenWith.setEnabled (true);    
      menuNewWindow.setEnabled(true);    
      menuNewTab.setEnabled   (true);    
      menuNewFolder.setEnabled(true);    
      menuNewFile.setEnabled  (true);    
      menuNewDMG.setEnabled   (true);    
      menuCopy.setEnabled     (true);    
      menuCut.setEnabled      (true);    
      menuPaste.setEnabled    (true);    
      menuDelete.setEnabled   (true);    
      menuRename.setEnabled   (true);    
      menuProps.setEnabled    (false);    
      
      menuOpen.addActionListener     (actionOpen);
      menuOpenWith.addActionListener (actionOpenWith);
      menuNewWindow.addActionListener(actionNewWindow);
      menuNewTab.addActionListener   (actionNewTab);
      menuNewFolder.addActionListener(actionNewFolder);
      menuNewFile.addActionListener  (actionNewFile);
      menuNewDMG.addActionListener   (actionNewDMG);
      menuCopy.addActionListener     (actionMenuCopy);
      menuCut.addActionListener      (actionMenuCut);
      menuPaste.addActionListener    (actionMenuPaste);
      menuDelete.addActionListener   (actionMenuDelete);
      menuRename.addActionListener   (actionMenuRename);
      menuProps.addActionListener    (actionMenuProps);

      popup.add(menuOpen);
      popup.add(menuOpenWith);
      popup.addSeparator();
      popup.add(menuNewWindow);
      popup.add(menuNewTab);
      popup.addSeparator();
      popup.add(menuNewFolder);
      popup.add(menuNewFile);
      if (isMac) {
    	popup.addSeparator();
      	popup.add(menuNewDMG);
      }
      popup.addSeparator();
      popup.add(menuCopy);
      popup.add(menuCut);
      popup.add(menuPaste);
      popup.addSeparator();
      popup.add(menuDelete);
      popup.add(menuRename);
      popup.addSeparator();
      popup.add(menuProps);
      
      //Add listener to the text area so the popup menu can come up.
      MouseListener popupListener = new PopupListener(popup);
      for (int i=0; i< listOfFilesLabels.length; i++) {
         listOfFilesLabels[i].addMouseListener(popupListener);
      }
      bodyPanel.addMouseListener(popupListener);
      
   }
   
   ActionListener actionOpen = new ActionListener () {
      public void actionPerformed(ActionEvent e) {
          //JMenuItem source = (JMenuItem)(e.getSource());
         int count = 0;
         int lastSelected = -1;
         //TODO make vector of files to open
         Vector fileList = new Vector();
         
         for (int i=0; i< listOfFilesLabels.length; i++) {
            if ( listOfFilesLabels[i].isSelected() ) { 
               System.out.println( listOfFilesLabels[i].getFileLocation() );
             //TODO but filter here for .app on macs
               if (listOfFilesLabels[i].isFile()) {
            	   //fileList = fileList + listOfFilesLabels[i].getFileLocation() + " ";
            	   fileList.add(listOfFilesLabels[i].getFileLocation() );
               }
               lastSelected = i;
               count = count + 1;
            }
            
         }
          //~ System.out.println("ActionListener Open");
          //~ newLocation = label.getFileLocation();
          //~ System.out.println(newLocation);
            
         //TODO there should be a loop for opening file which will open all selected
         
         if (fileList.size() > 0 ){
        	 System.out.println("Opening bunch of files");
        	 Run runCommand = new amaras.runtime.Run(fileList);
         }
         
         
         //if ((count == 1)&&(listOfFilesLabels[lastSelected].isFile())) {
         //   String newLocation = listOfFilesLabels[lastSelected].getFileLocation();
         //   Run runCommand = new amaras.runtime.Run(newLocation);
         //}
         if ((count == 1)&&(listOfFilesLabels[lastSelected].isDirectory())) {
            String newLocation = listOfFilesLabels[lastSelected].getFileLocation();
            setStatusMsg("Requesting Folder \"" + newLocation + "\"");
            buildBodyPanel(newLocation);
         }
      }
   };
   
   ActionListener actionOpenWith = new ActionListener () {
      public void actionPerformed(ActionEvent e) {
    	  System.out.println("ActionListener OpenWith");
    	  //Request application to open files with.
    	  Preferences prefsApp  = Preferences.userRoot().node("net/amaras/fileexplorer/application");
          // Get property from pref menu     
          String location = prefsApp.get(fileexplorer.pref.Application.APP_LOCATION, 
                               fileexplorer.pref.Application.APP_LOCATION_DEFAULT);  
    	  JFileChooser _fileChooser = new JFileChooser(location);
    	  
    	  int retval = _fileChooser.showOpenDialog(null);
          if (retval == JFileChooser.APPROVE_OPTION) {
              //... The user selected a file, get it, use it.
              File file = _fileChooser.getSelectedFile();
        	  //then open selected files with it.
              
              System.out.println(file.getAbsolutePath() );
              Vector fileList = new Vector();
              fileList.add(file.getAbsolutePath());
              for (int i=0; i< listOfFilesLabels.length; i++) {
                  if ( listOfFilesLabels[i].isSelected()  &&
                       listOfFilesLabels[i].isFile()    ) {
                  	   fileList.add(listOfFilesLabels[i].getFileLocation() );
                  }
               }
              
              Run runCommand = new amaras.runtime.Run(amaras.runtime.Run.DIRECT, fileList);
          } else {
        	  System.out.println("User Canceled OpenWith");
          }
          
    	  
          
      }
   };
   
   ActionListener actionNewTab = new ActionListener () {
      public void actionPerformed(ActionEvent e) {
         System.out.println("ActionListener OpenNewTab");
			for (int i=0; i< listOfFilesLabels.length; i++) {
            if (( listOfFilesLabels[i].isSelected() )&&
                ( listOfFilesLabels[i].isDirectory())) { 
						 parentFileExplorer.newTab(listOfFilesLabels[i].getFullLocation());
                   //new FileExplorer(listOfFilesLabels[i].getFullLocation());
            }
         }
      }
   };
   

   ActionListener actionNewWindow = new ActionListener () {
      public void actionPerformed(ActionEvent e) {
         System.out.println("ActionListener OpenNewWindow");
         for (int i=0; i< listOfFilesLabels.length; i++) {
            if (( listOfFilesLabels[i].isSelected() )&&
                ( listOfFilesLabels[i].isDirectory())) { 
                   new FileExplorer(listOfFilesLabels[i].getFullLocation());
            }
         }    
      }
   };

   ActionListener actionNewFolder = new ActionListener () {
      public void actionPerformed(ActionEvent e) {
          System.out.println("ActionListener Create New Folder");
          String newFilename = (String) JOptionPane.showInputDialog(new JFrame(),"Input File Name to be created");

          if ((newFilename != null)&&(newFilename.length() > 0)) {
             //User enterd text and clicked ok
             File create = new File(currentLocation + File.separator + newFilename); 
             checkFileExists (create, 1);
             
          }
      }
   };
   
   ActionListener actionNewFile = new ActionListener () {
      public void actionPerformed(ActionEvent e) {
         System.out.println("ActionListener Create New File");
         String newFilename = (String) JOptionPane.showInputDialog(new JFrame(),"Input File Name to be created");

         if ((newFilename != null)&&(newFilename.length() > 0)) {
            //User enterd text and clicked ok
            //System.out.println(currentLocation + File.separator + newFilename);
            File create = new File(currentLocation + File.separator + newFilename); 
            checkFileExists (create, 0);
            
         }
      }
   };
   
   private void checkFileExists (File newFilename, int mode) {
	   try {
		   System.out.println("checkFileExists(File "+newFilename+")");
		   if ( newFilename.exists() ) {
			    Object queryMSG = "File "+newFilename+" already exists Choose another";

			    String newFilenameString = (String) JOptionPane.showInputDialog(queryMSG, newFilename.getName());
		   		
		   		if ((newFilenameString != null)&&(newFilenameString.length() > 0)) {
		   			System.out.println("checkFileExists recursing");
		   			newFilename = new File(currentLocation + File.separator + newFilenameString);
		   			checkFileExists(newFilename, mode);
		   		} else {
		   			System.out.println("User cancelled File Creation: " + newFilename);
		   			setStatusMsg("User Cancelled New File");
		   		}
		   } else {
			  if (mode == 0) { 
                 newFilename.createNewFile();
			  } else {
				 newFilename.mkdirs();
			  }
              reloadBodyPanel();
		   }
	   } catch (java.io.IOException filecreatione) {
           System.out.println("File Creation Failed");
           System.err.println(filecreatione);
        }
	   
   }
   
   ActionListener actionMenuCopy = new ActionListener () {
	      public void actionPerformed(ActionEvent e) {
	          System.out.println("ActionListener Copy");
	          copyAction();
	      }
	   };
   
   ActionListener actionNewDMG = new ActionListener () {
      public void actionPerformed(ActionEvent e) {
          System.out.println("ActionListener NewDMG");
          createDMGAction();
      }
   };
   
   ActionListener actionMenuCut = new ActionListener () {
      public void actionPerformed(ActionEvent e) {
          System.out.println("ActionListener Cut");
          cutAction();
      }
   };
   
   ActionListener actionMenuPaste = new ActionListener () {
      public void actionPerformed(ActionEvent e) {
          System.out.println("ActionListener Paste");
          pasteAction();
      }
   };
   
   ActionListener actionMenuDelete = new ActionListener () {
      public void actionPerformed(ActionEvent e) {
          System.out.println("ActionListener Delete");
          for (int i=0; i< listOfFilesLabels.length; i++) {
              if ( listOfFilesLabels[i].isSelected() ){ 
                     System.out.println("Delete: " + listOfFilesLabels[i].getFullLocation());
                     Object[] options = { "CANCEL", "DELETE" };
                     int option = JOptionPane.showOptionDialog(null,"Delete "+listOfFilesLabels[i].getFullLocation(), "Warning",
                             JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                             null, options, options[0]); // QUESTION_MESSAGE OK_CANCEL_OPTION
                             
                     if (option == 1) {
                    	 File FileForDelete = new File(listOfFilesLabels[i].getFullLocation());
                    	 //FileForDelete.delete();
                    	 recDelete(FileForDelete);
                     } else {
                    	 System.out.println("User Cancelled Delete");
                     }
              }
           }
          //Reload after all files have been deleted
          reloadBodyPanel();
      }
   };
   
   public static void recDelete(File folder) {
      if(folder.exists()) {
    	  if(folder.isDirectory()) {
    		  File[] files = folder.listFiles();
    	      for(int i=0; i < files.length; i++) {
    	         File curFile = files[i];
    	         // call to delete the current file/folder
    	         recDelete(curFile);
    	       }    
    	  }
    	  //If a folder Deleted all contents
    	  //If a File Jump straight here
          folder.delete();
      }
   }
   
   ActionListener actionMenuRename = new ActionListener () {
      public void actionPerformed(ActionEvent e) {
    	  String lastItem = listOfFilesLabels[lastPositionSelected].getFileLocation();
    	  //System.out.println(lastItem);
    	  //listOfFilesLabels[lastPositionSelected] =  new JLabelFile("Rename");
    	  System.out.println("ActionListener Rename");
    	  File filename = new File(lastItem);
    	  
    	  
    	  renameFile (filename, filename, 0);
    	  
    	  
      }
   };
   
   void renameFile (File filename, File suggestion, int run) {
	   //try {
	   		String queryMSG;
	   		if (run == 0) {
	   			queryMSG = new String("Choose New Name for:" + filename.getName());
		    } else {
		    	queryMSG = new String("Choose New Name for:" + filename.getName() + 
		    			"\n" + suggestion.getName() + " Already taken");
		    }
	   		  
	    	  
		      String newFilenameString = (String) JOptionPane.showInputDialog(queryMSG, suggestion.getName());
	    	  File newfilename  = new File (filename.getParent() + File.separator  + newFilenameString);
	    	  System.out.println(newfilename);
	    	  
	    	  if (newfilename.exists() == false) {
	    		  filename.renameTo(newfilename);
	    		  //Reload after all files have been deleted
	              reloadBodyPanel();
	    	  } else {
	    		  //Iterrate
	    		  renameFile(filename, newfilename, 1);  
	    	  }
// 	  } catch (java.io.IOException eRename){ 
// 	     System.err.println("Call Dave" + eRename);
// 	  }
	   
   }
   
   ActionListener actionMenuProps = new ActionListener () {
	      public void actionPerformed(ActionEvent e) {
	          System.out.println("ActionListener Props");
	      }
	   };

   public void itemStateChanged(ItemEvent e) {
       JMenuItem source = (JMenuItem)(e.getSource());
       System.out.println("Right Click Menu statechange");
   }
   
   
   //Right click menu Functionality
   class PopupListener extends MouseAdapter {
       JPopupMenu popup;

       PopupListener(JPopupMenu popupMenu) {
           popup = popupMenu;
       }

       public void mousePressed(MouseEvent e) {
           maybeShowPopup(e);
       }

       public void mouseReleased(MouseEvent e) {
           maybeShowPopup(e);
       }

       private void maybeShowPopup(MouseEvent e) {
           if (e.isPopupTrigger()) {
               popup.show(e.getComponent(),
                          e.getX(), e.getY());
           }
       }
   }
    //###########################################################################

}

