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

//Resources
import javax.swing.*;

public class TransferListItem extends JPanel {

   private String msg;
   public JLabel msgLabel;
   
   public TransferListItem(){
      super();
      helperConstructor();
   }
   
   public TransferListItem(String msg){
      super();
      this.msg = msg;
      helperConstructor();
   }
   
   public void setMessage(String msg) {
      this.msg = msg;
      msgLabel.setText(msg);
   }
   
   private void helperConstructor(){
      if (this.msg == null) {
         msgLabel = new JLabel("Testing 123");
      } else {
         msgLabel = new JLabel(msg);
      }
      
      this.add(msgLabel);
   }
   
}