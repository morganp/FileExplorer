package fileexplorer;
// JLabelFile.java
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
//import java.swing.JLabel;
import java.awt.Dimension;
import javax.swing.*;
import java.awt.*;

import java.awt.event.*;
import javax.swing.JPopupMenu;


//Required for the File.seperator
import java.io.File;

public class JLabelFile extends JLabel { //implements ActionListener, ItemListener {
    private String text;
    private String fileLocation;
    private boolean selected;
    private int position;
    
    //TODO These should come from pref menu
    final static Color evenLine    = new Color(255,255,255);
    final static Color oddLine     = new Color(240,240,240);
    final static Color highlighted = new Color(160,160,160);
    final static int   labelHeight = 16;
    
    
	public JLabelFile () {
		super();
		this.selected = false;
		this.position = 0;
		
		//Create Right Click Menu
	    createPopupMenu();
	}
	
	public JLabelFile (String text) {
		super(text);
		this.text     = text;
		this.selected = false;
		this.position = 0;
		
		//Create Right Click Menu
	    createPopupMenu();
	}
	
	public JLabelFile (String text, int position) {
		super(text);
		this.text     = text;
		this.selected = false;
		this.position = position;
		reColor();
		
		//Create Right Click Menu
	    createPopupMenu();
	}
	
	public JLabelFile (String text, int position, String fileLocation) {
		//this.JLabelFile (text, position);
		super(text);
		this.text     = text;
		this.selected = false;
		this.position = position;
		reColor();
		this.setFileLocation(fileLocation);
		
		//Create Right Click Menu
	      createPopupMenu();
	}
   
   public JLabelFile (ImageIcon icon, String text, int position, String fileLocation) {
		//this.JLabelFile (text, position);
		super(text, icon, JLabel.LEFT);
		this.text     = text;
		this.selected = false;
		this.position = position;
		reColor();
		this.setFileLocation(fileLocation);
		
		//Create Right Click Menu
	      createPopupMenu();
	}
	
	public void select() {
		if (selected == false){
			selected = true;
			this.setOpaque(true);
			//label.setForeground(new Color(255, 255, 255));
			this.setBackground(highlighted);
         System.out.println("JLableFile Selected " + fileLocation);
		}
	}
	
   public boolean isSelected() {
      return selected;
   }
   
	public void deselect() {
		selected = false;
		reColor();
	}
	
	public void reColor() {
		this.setOpaque(true);
		if (selected == true) { 
			this.setBackground(highlighted);
		} else if ((this.position%2) == 0) {
			this.setBackground(evenLine);
		} else {
			this.setBackground(oddLine);
		}
	}
	
	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}
	
	public String getFileLocation(){
		return this.fileLocation;
	}
   
   public boolean isDirectory () {
      File folder = new File(fileLocation);
      return folder.isDirectory();
   }
	
   public boolean isFile () {
      File folder = new File(fileLocation);
      return folder.isFile();
   }
   
   public String getFullLocation(){
      String fullPath = new String();
      if (fileLocation.endsWith(text)) {
         //For some JRE that add filename to location
          return fileLocation;
      } else if (fileLocation.endsWith(File.separator)) {
         //Special case dont double slash
         fullPath = fileLocation + text;
      }  else {
         System.out.println("JLableFile adding extra File.separator");
         fullPath = fileLocation + File.separator + text ;
      }
      
      return fullPath;
   }
   
	public void setWidth(int width){
		setMinimumSize(  new Dimension(width, labelHeight));
		setMaximumSize(  new Dimension(width, labelHeight));
		setPreferredSize(new Dimension(width, labelHeight));
	}
	
	public int getIndex() {
		return position;
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		//setWidth(10);
		validate();
		//System.out.println("calling JLabelFile paint");
	}
	
	
   public void createPopupMenu() {
   }
   
}
