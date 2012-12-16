package fileexplorer;
//
//	File:	PrefPane.java
//
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
import javax.swing.*;

import fileexplorer.pref.*;

public class PrefPane extends JFrame {
    protected JButton okButton;
    protected JLabel prefsText;

    public PrefPane()
    {
		super();

//        this.getContentPane().setLayout(new BorderLayout(10, 10));
//        prefsText = new JLabel ("FileExplorer Preferences...");
//        JPanel textPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
//        textPanel.add(prefsText);
//        this.getContentPane().add (textPanel, BorderLayout.NORTH);
//		
//        okButton = new JButton("OK");
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
//        buttonPanel.add (okButton);
//        okButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent newEvent) {
//				setVisible(false);
//			}	
//		});
//        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
//        
        
		JTabbedPane topLevelMenu   = new JTabbedPane(JTabbedPane.TOP); 
        
        Location     locationTab    = new Location();
        View         viewTab        = new View();
        Colours      colorTab       = new Colours();
        Application  appTab         = new Application();
        Transfer     transferTab    = new Transfer();
        
		topLevelMenu.add(locationTab);
		topLevelMenu.add(viewTab);
		topLevelMenu.add(appTab);
//		topLevelMenu.add(colorTab);
		topLevelMenu.add(transferTab);
        
        
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.getContentPane().add(topLevelMenu);
		setSize(390, 300);
		setLocation(20, 40);
    }
}