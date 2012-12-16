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

public class View extends JPanel {

	public final static String SHOW_TOOLBAR = new String("showToolbar");
	public final static String SHOW_HIDDEN  = new String("showHidden");
	public final static String SORT_MODE    = new String("sortMode");
	public final static boolean SHOW_TOOLBAR_DEFAULT = true;
	public final static boolean SHOW_HIDDEN_DEFAULT = false;
	public final static int SORT_MODE_DEFAULT = 0;
   private Preferences prefsView;
   
   protected ResourceBundle resbundle;
   
   //View Menu Items
   protected JCheckBox showToolbarMenuItem;
    
   //Menu Items
   protected JCheckBox showHiddenMenuItem;
   protected JRadioButton[] m_sort;
   
   ActionListener setShowToolbarAction = new ActionListener () {
		public void actionPerformed(ActionEvent e) {
         prefsView.putBoolean(SHOW_TOOLBAR, showToolbarMenuItem.isSelected());
		}
	};
   
   ActionListener setShowHiddenAction = new ActionListener () {
		public void actionPerformed(ActionEvent e) {
         prefsView.putBoolean(SHOW_HIDDEN, showHiddenMenuItem.isSelected());
		}
	};
   
	ActionListener setSortAction = new ActionListener () {
		public void actionPerformed(ActionEvent e) {
			for (int i=0; i<amaras.filesort.DirectoryView.SORT_OPTIONS; i++) {
				if (m_sort[i].isSelected() == true) {
					prefsView.putInt(SORT_MODE, i);
				}
			}
		}
	};
	
   
	public View(){
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
      // The ResourceBundle below contains all of the strings used in this
      // application.  ResourceBundles are useful for localizing applications.
      // New localities can be added by adding additional properties files.
      resbundle = ResourceBundle.getBundle ("strings", Locale.getDefault());
      
      prefsView = Preferences.userRoot().node("net/amaras/fileexplorer/view"); 
      
		JPanel defLoc = new JPanel();
		defLoc.setLayout(new BoxLayout(defLoc, BoxLayout.Y_AXIS));
		

      showToolbarMenuItem = new JCheckBox(resbundle.getString("showToolbar"));
      showToolbarMenuItem.setSelected(prefsView.getBoolean(SHOW_TOOLBAR, SHOW_TOOLBAR_DEFAULT));
      showToolbarMenuItem.addActionListener(setShowToolbarAction);
		defLoc.add(showToolbarMenuItem);
      
      showHiddenMenuItem = new JCheckBox(resbundle.getString("showHidden"));
      showHiddenMenuItem.setSelected(prefsView.getBoolean(SHOW_HIDDEN, SHOW_HIDDEN_DEFAULT));
      showHiddenMenuItem.addActionListener(setShowHiddenAction);
		defLoc.add(showHiddenMenuItem);

      //TODO add radio list to select sort option
		int textHeight     = 23;
	      
		
		ButtonGroup sortGroup = new ButtonGroup();
	      m_sort = new JRadioButton[amaras.filesort.DirectoryView.SORT_OPTIONS];
	      int mode = prefsView.getInt(SORT_MODE, SORT_MODE_DEFAULT);
	      
	      for (int i=0; i<m_sort.length; i++) {
	         m_sort[i] = new JRadioButton(amaras.filesort.DirectoryView.text[i]);
	         m_sort[i].setSelected(i == mode);	         
	         m_sort[i].addActionListener(setSortAction);
	         sortGroup.add(m_sort[i]);
	         defLoc.add(m_sort[i]);
	      }
		
		
		
		
		
		
		this.add(defLoc);
      
	}
	
   //Prefs Tab Title/Name
	public String getName() {
		return new String("View");
	}
   
   
}
