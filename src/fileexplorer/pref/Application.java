//
//  Location.java
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
package fileexplorer.pref;

//import BoxLayout;

import java.util.prefs.*;


import java.awt.*;
import java.awt.event.*;
import java.awt.Color;
import javax.swing.*;
import java.io.*;

public class Application extends JPanel {

   public final static String APP_LOCATION = new String("defaultLocation");
   public final static String APP_LOCATION_DEFAULT = new String(); 
   
   private JTextField labelLoc;
   private Preferences prefsApp;


   ActionListener browseAction = new ActionListener () {
      public void actionPerformed(ActionEvent e) {

         String location = prefsApp.get(APP_LOCATION, APP_LOCATION_DEFAULT);
         final JFileChooser fc = new JFileChooser(location);
         fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

         int returnVal = fc.showOpenDialog(Application.this);

         if (returnVal == JFileChooser.APPROVE_OPTION) {
            //Handle open button action.
            try {
               File file = fc.getSelectedFile();
               System.out.println("Setting Default Home to : " + file.getCanonicalPath() );
               prefsApp.put(APP_LOCATION, file.getCanonicalPath() );
               labelLoc.setText(file.getCanonicalPath());
            } catch (java.io.IOException except) {
               System.err.println("Error " + except);
            }
         } else {
            System.out.println("Open command cancelled by user." );
         }
      }
   };
   
   
   ActionListener setHomeAction = new ActionListener () {
      public void actionPerformed(ActionEvent e) {
         System.out.println("Pressed SetHome");
         String newAppLoc = getPresetDefault();
         labelLoc.setText(newAppLoc);
         prefsApp.put(APP_LOCATION, newAppLoc );
      }
   };
   
   
   ActionListener setNewLocAction = new ActionListener () {
      public void actionPerformed(ActionEvent e) {
         System.out.println("Setting new Default Loc");
         File newLocFile = new File(labelLoc.getText());
         if (newLocFile.exists() && newLocFile.isDirectory() ) {
            prefsApp.put(APP_LOCATION, labelLoc.getText() );
         }
      }
   };
   
   private String getPresetDefault() {
	   String appdefault = new String();
	      if (System.getProperty("os.name").contains("Mac")) {
	    	  appdefault = "/Applications";
	      } else if (System.getProperty("os.name").contains("Win")) {
	    	  appdefault = "C:\\Program\\ Files";
	      } else {
	    	  appdefault = System.getProperty("user.home");
	      }
	      return appdefault;
   }
   
   public Application(){
      super();
    //TODO find correct way for getting button height. this way will not scale with fonts
      int textHeight     = 27;
      
      this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      
      JPanel defLoc = new JPanel();
      defLoc.setLayout(new BoxLayout(defLoc, BoxLayout.X_AXIS));
      labelLoc = new JTextField();
      
      labelLoc.addActionListener(setNewLocAction);
      
      prefsApp = Preferences.userRoot().node("net/amaras/fileexplorer/application"); 
      
      JLabel message = new JLabel("The default path to for \"Open With\" \n");
      
      //BoxLayout sets all vertical components to have the same centre.
      //There fore message has to be same width as container to position correctly
      message.setMinimumSize  (new Dimension(600, textHeight));
      message.setPreferredSize(new Dimension(600, textHeight));
      message.setMaximumSize  (new Dimension(600, textHeight));
      this.add(message);
      
      labelLoc.setText( prefsApp.get(APP_LOCATION, getPresetDefault()));// 
      
      labelLoc.setMinimumSize  (new Dimension(150, textHeight));
      labelLoc.setPreferredSize(new Dimension(150, textHeight));
      labelLoc.setMaximumSize  (new Dimension(300, textHeight));
      
      defLoc.add(labelLoc);
      
      JButton browseButton = new JButton(new String("Browse"));
      browseButton.addActionListener(browseAction);
      defLoc.add(browseButton);
      
      JButton setHomeButton = new JButton(new String("Default"));
      setHomeButton.addActionListener(setHomeAction);
      defLoc.add(setHomeButton);
      
      this.add(defLoc);
      
   }
   
   //Prefs Tab Title/Name
   public String getName() {
      return new String("Applications");
   }
   
   
}
