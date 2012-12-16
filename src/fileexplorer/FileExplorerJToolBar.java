package fileexplorer;


import javax.swing.JToolBar;

import java.awt.event.*;
import javax.swing.JButton;

public class FileExplorerJToolBar extends JToolBar {

	FileExplorer fileexplorerParent;
	
	FileExplorerJToolBar(FileExplorer fileexplorerParent){
		super();
		
		this.fileexplorerParent = fileexplorerParent;
		
		JButton cutButton   = new JButton(new String("Cut"));
	    JButton copyButton  = new JButton(new String("Copy"));
	    JButton pasteButton = new JButton(new String("Paste"));
	  
	    cutButton.addActionListener(cutToolAction);
	    copyButton.addActionListener(copyToolAction);
	    pasteButton.addActionListener(pasteToolAction);
	  
	    this.add(cutButton);
	    this.add(copyButton);
	    this.add(pasteButton);
	}
	
	ActionListener cutToolAction = new ActionListener () {
	      public void actionPerformed(ActionEvent e) {
	  		
	         fileexplorerParent.getActiveBrowserWindow().cutAction();
	      }
	   };
	    
	   ActionListener copyToolAction = new ActionListener () {
	      public void actionPerformed(ActionEvent e) {
	    	  fileexplorerParent.getActiveBrowserWindow().copyAction();
	      }
	   };
	      
	   ActionListener pasteToolAction = new ActionListener () {
	      public void actionPerformed(ActionEvent e) {
	    	  fileexplorerParent.getActiveBrowserWindow().pasteAction();
	      }
	   };
	
}
