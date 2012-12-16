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
 * Graphic Icons loaded using Suns Java Graphics Repository
 * http://java.sun.com/developer/techDocs/hi/repository/
 *
 */
 
package amaras.icon;
 
public class SetupIcon {
   javax.swing.ImageIcon iconImage;
   
   public SetupIcon() {
   }
   
   public SetupIcon(String path, String description) {
      setImageIcon(path, description);
   }
   
   //http://java.sun.com/docs/books/tutorial/uiswing/components/icon.html
   /** Returns an ImageIcon, or null if the path was invalid. */
   public void setImageIcon(String path, String description) {
      //~ try {
         java.net.URL imgURL = getClass().getResource(path);
         //~ java.net.URL imgURL = new java.net.URL(path); 
         //~ java.net.URL imgURL = this.getRunDirectory();
         System.out.println(description + ": " + imgURL + getClass());
      
         if (imgURL != null) {
            iconImage = new javax.swing.ImageIcon(imgURL, description);
         } else {
            System.err.println("Couldn't find file: " + path);
            iconImage = null;
         }
      //~ } catch (java.net.MalformedURLException e){
         //~ System.err.println(e);
         //~ iconImage = null;
      //~ }
      
   }
   
   public javax.swing.ImageIcon getImageIcon(){
      return iconImage;
   }
   
}