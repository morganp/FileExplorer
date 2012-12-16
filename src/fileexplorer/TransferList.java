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
 
package fileexplorer;

import javax.swing.JFrame;

//Resources
import javax.swing.*;

import java.util.Locale;
import java.util.ResourceBundle;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;


public class TransferList extends JFrame {
   
   protected ResourceBundle resbundle;
	
   protected JPanel topPanel;
   protected JPanel bodyPanel;
   protected JScrollPane  scrollBodyPanel; //scrolling wrapper
	
	TransferList(){
		super();
		helperConstructor();
	}
	
	TransferList(String name){
		super(name);
		helperConstructor();
	}

	private void helperConstructor(){
		
		int initialWidth  = 300;
		int initialHeight = 400;
		int initialX      = 20;
		int initialY      = 40;
		
		setSize(initialWidth, initialHeight);
      setLocation(initialX, initialY);
		
		 
      
      this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
      //create custom close operation
      this.addWindowListener(new java.awt.event.WindowAdapter() {
        public void windowClosing(java.awt.event.WindowEvent e) {
           setVisible(false);
      }
      });
         
      int shortcutKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
      
      // The ResourceBundle below contains all of the strings used in this
      // application.  ResourceBundles are useful for localizing applications.
      // New localities can be added by adding additional properties files.
      resbundle = ResourceBundle.getBundle ("strings", Locale.getDefault());
      
      topPanel = new JPanel();
      topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
     
      
     
      bodyPanel = new JPanel();
      bodyPanel.setFocusable(true);
      bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
     
      //Create scrolling window for file list
      scrollBodyPanel = new JScrollPane(bodyPanel);

      //Stop Vertical Scroll bar disappearing
      scrollBodyPanel.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
      scrollBodyPanel.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
     
      //Set shortcut-j keyboard listener to toggle
      scrollBodyPanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_J, shortcutKeyMask),"show");
      scrollBodyPanel.getActionMap().put("show", new showTransfersActionClass());
     
     
     
     
     
     
     
      //Adding content
      //~ topPanel.add(new JLabel("Transfer List Top"));
      //~ topPanel.add(new JLabel("Transfer List Top"));
      //~ bodyPanel.add(new JLabel("Test Transfer List"));
      //~ bodyPanel.add(new JLabel("Test Transfer List"));
      //~ bodyPanel.add(new JLabel("Test Transfer List"));
      //~ bodyPanel.add(new JLabel("Test Transfer List"));
      //~ bodyPanel.add(new JLabel("Test Transfer List"));
      //~ bodyPanel.add(new JLabel("Test Transfer List"));
      //~ bodyPanel.add(new JLabel("Test Transfer List"));
      //~ bodyPanel.add(new JLabel("Test Transfer List"));
      //~ bodyPanel.add(new JLabel("Test Transfer List"));
      //~ bodyPanel.add(new JLabel("Test Transfer List"));
      //~ bodyPanel.add(new JLabel("Test Transfer List"));
      //~ bodyPanel.add(new JLabel("Test Transfer List"));
      //~ bodyPanel.add(new JLabel("Test Transfer List"));
      
      
      //~ this.add(topPanel);
      this.add(scrollBodyPanel);
      setVisible(false);
      
      //Make sure initial focus is correct
      scrollBodyPanel.grabFocus();
	}
	
   public class showTransfersActionClass extends AbstractAction {
      public showTransfersActionClass() {
         super();
      }
      public void actionPerformed(ActionEvent e) {
         toggleVisible();
      }
   }
	
	
	public void toggleVisible(){
		if (this.isVisible()){
			setVisible(false);
		} else {
         refresh();
			setVisible(true);
		}
	}

   
   public void add(){
      this.bodyPanel.add(new JLabel("Test Transfer List"));
      refresh();
   }
   
   public void add(JLabel label){
      this.bodyPanel.add(label);
      refresh();
   }
   
    public void add(String msg){
      this.bodyPanel.add(new JLabel(msg));
      refresh();
   }
   
   public void add(TransferListItem item){
      this.bodyPanel.add(item);
      refresh();
   }
   
   private void refresh() {
      topPanel.repaint();
		topPanel.validate();
      bodyPanel.repaint();
		bodyPanel.validate();
		scrollBodyPanel.validate();
   }
   
}
