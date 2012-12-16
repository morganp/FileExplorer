package fileexplorer;
//
//  JLabelStatus.java
//  FileExplorer
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
import javax.swing.*;

public class JLabelStatus extends JLabel {
   
   String fullMessage;

   public void setTextO(String text){
      fullMessage = text;
      //System.out.println("Overridden setText");
      super.setText(text);
   }
   
   public String getTextO(){
      //System.out.println("Overridden getText");     
      return fullMessage;
   }
   
   public void refreshText(int x){
	   
	   //This is not required if just resizing the label when resize the window.
	   // ... wil be put in automagically
	   
      //System.out.println("Starting Refresh   " + x);
      super.setText(this.fullMessage);
      //if (this.getWidth() > (x-15)) {
      if ((fullMessage.length()*6.6) > (x-20)) {
      //reduce width ot message 
         //int charsAllowed = (panelWidth - 10)/6 ;
         for (int y=fullMessage.length(); y>0; y--){
            String temp = this.fullMessage.substring(0,y) + "..." ;        
            //System.out.println("trying   " + temp + this.getWidth());
            //if (this.getWidth() < (x-15)) {
            if ((temp.length()*6.6) < (x-15) ) {

               this.setText(temp); 
               y=0;
            }
         }
      }// if 
   }//public void refresh

}
