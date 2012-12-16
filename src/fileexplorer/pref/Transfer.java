//
//  View.java
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

import java.util.Locale;
import java.util.ResourceBundle;

import java.awt.*;
import java.awt.event.*;
import java.awt.Color;
import javax.swing.*;
import java.io.*;

public class Transfer extends JPanel {

	//FireFox
	//Show the downloads window when downloading
	//close window when downloads complete
	public final static String OPEN_TRANSFER   = new String("openTransfer");
	public final static String CLOSE_TRANSFER  = new String("closeTransfer");
	public final static boolean OPEN_TRANSFER_DEFAULT = true;
	public final static boolean CLOSE_TRANSFER_DEFAULT = false;
	
   private Preferences prefsTransfer;
   protected ResourceBundle resbundle;
   
   //Menu Items
   protected JCheckBox openTransferMenuItem; 
   protected JCheckBox closeTransferMenuItem;
   
   ActionListener setOpenTransferAction = new ActionListener () {
		public void actionPerformed(ActionEvent e) {
			prefsTransfer.putBoolean(OPEN_TRANSFER, openTransferMenuItem.isSelected());
		}
	};
   
   ActionListener setCloseTransferAction = new ActionListener () {
		public void actionPerformed(ActionEvent e) {
			prefsTransfer.putBoolean(CLOSE_TRANSFER, closeTransferMenuItem.isSelected());
		}
	};
	
   
	public Transfer(){
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
      // The ResourceBundle below contains all of the strings used in this
      // application.  ResourceBundles are useful for localizing applications.
      // New localities can be added by adding additional properties files.
      resbundle = ResourceBundle.getBundle ("strings", Locale.getDefault());
      
      prefsTransfer = Preferences.userRoot().node("net/amaras/fileexplorer/transfer"); 
      
		JPanel defLoc = new JPanel();
		defLoc.setLayout(new BoxLayout(defLoc, BoxLayout.Y_AXIS));
		

		openTransferMenuItem = new JCheckBox(resbundle.getString("openTransfer"));
		openTransferMenuItem.setSelected(prefsTransfer.getBoolean(OPEN_TRANSFER, OPEN_TRANSFER_DEFAULT));
		openTransferMenuItem.addActionListener(setOpenTransferAction);
		defLoc.add(openTransferMenuItem);
      
		closeTransferMenuItem = new JCheckBox(resbundle.getString("closeTransfer"));
		closeTransferMenuItem.setSelected(prefsTransfer.getBoolean(CLOSE_TRANSFER, CLOSE_TRANSFER_DEFAULT));
		closeTransferMenuItem.addActionListener(setCloseTransferAction);
		defLoc.add(closeTransferMenuItem);

      //TODO add radio list to select sort option
		int textHeight     = 23;
		
		
		this.add(defLoc);
      
	}
	
   //Prefs Tab Title/Name
	public String getName() {
		return new String("Transfer");
	}
   
   
}
