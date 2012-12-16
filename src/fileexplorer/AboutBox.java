package fileexplorer;
//
// File: AboutBox.java
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
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.*;
import amaras.jLabelExt.*;

public class AboutBox extends JFrame implements ActionListener {
    protected JLabel titleLabel, aboutLabel[];
    protected static int labelCount  = 8;
    protected static int aboutWidth  = 400;
    protected static int aboutHeight = 410;
    protected static int aboutTop    = 200;
    protected static int aboutLeft   = 350;
    protected Font titleFont, bodyFont;
    protected ResourceBundle resbundle;

    public AboutBox() {
        super("");
        this.setResizable(true);
        resbundle = ResourceBundle.getBundle ("strings", Locale.getDefault());
        SymWindow aSymWindow = new SymWindow();
        this.addWindowListener(aSymWindow);  
      
        // Initialize useful fonts
        titleFont = new Font("Lucida Grande", Font.BOLD, 14);
        if (titleFont == null) {
            titleFont = new Font("SansSerif", Font.BOLD, 14);
        }
        bodyFont  = new Font("Lucida Grande", Font.PLAIN, 10);
        if (bodyFont == null) {
            bodyFont = new Font("SansSerif", Font.PLAIN, 10);
        }
      
        this.getContentPane().setLayout(new BorderLayout(5, 5));
   
        aboutLabel = new JLabel[labelCount];
        aboutLabel[0] = new JLabel("");
        aboutLabel[1] = new JLabel(resbundle.getString("frameConstructor"));
        aboutLabel[1].setFont(titleFont);
        aboutLabel[2] = new JLabel(resbundle.getString("appVersion"));
        aboutLabel[2].setFont(bodyFont);
        aboutLabel[3] = new JLabel("");
        aboutLabel[4] = new JLabel("");
        aboutLabel[5] = new JLabel("JDK " + System.getProperty("java.version"));
        aboutLabel[5].setFont(bodyFont);
        aboutLabel[6] = new JLabel(resbundle.getString("copyright"));
        aboutLabel[6].setFont(bodyFont);
		  
        aboutLabel[7] = new JLabel("");      
      
        //Dimension currentDim = aboutLabel[6].getPreferredSize();
        //Double ex = (Double) currentDim.getWidth() ;
        //Double wy = (Double) currentDim.getHeight();
        //System.out.println(ex + ":" + wy);

         //Dimension newDim = new Dimension(ex.intValue(), wy.intValue()*10);
        
         //aboutLabel[6].setMinimumSize(newDim);
         //aboutLabel[6].setPreferredSize(newDim);
         //aboutLabel[6].setMaximumSize(newDim);
         aboutLabel[6].setUI( new MultiLineLabelUI() );
        // 
        // 		  //Print ou tthe
        // 		  currentDim = aboutLabel[6].getPreferredSize();
        //         ex = (Double) currentDim.getWidth() ;
        //         wy = (Double) currentDim.getHeight();
        //         System.out.println(ex + ":" + wy);
        
      
        Panel textPanel2 = new Panel(new GridLayout(labelCount, 1));
        for (int i = 0; i<labelCount; i++) {
            aboutLabel[i].setHorizontalAlignment(JLabel.CENTER);
            textPanel2.add(aboutLabel[i]);
        }
        this.getContentPane().add (textPanel2, BorderLayout.CENTER);
        this.pack();
        this.setLocation(aboutLeft, aboutTop);
        this.setSize(aboutWidth, aboutHeight);
    }

    class SymWindow extends java.awt.event.WindowAdapter {
       public void windowClosing(java.awt.event.WindowEvent event) {
          setVisible(false);
       }
    }
    
    public void actionPerformed(ActionEvent newEvent) {
        setVisible(false);
    }    
}