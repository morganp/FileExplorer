
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

//based on http://www.exampledepot.com/egs/java.lang/WorkQueue.html?l=rel

package amaras.runtime;

//import String;

import java.io.*;
import java.util.Vector;

//For getting Prefs
import java.util.prefs.*;

import fileexplorer.TransferList;
import fileexplorer.TransferListItem;

public class PasteQue {

	private static int debugOn        = 1;
	protected Runtime currentRuntime;
	private   Vector joblist;
	private   Vector completedlist;
   private   TransferList transferlist;
	
	public PasteQue() {
		joblist       = new Vector();
		completedlist = new Vector();
	};
   
   public PasteQue(TransferList transferlist) {
		this.transferlist = transferlist;
      joblist       = new Vector();
		completedlist = new Vector();
	};
   
   public synchronized CopyCut getJob(){
      while(joblist.size() == 0){
         try {
            wait();
         } catch (java.lang.InterruptedException e){
            System.err.println(e);
         }
      }
      CopyCut currentJob = (CopyCut) joblist.firstElement();
      return  currentJob;     
      
   }
   
   public boolean joblistComplete() {
      //Todo for the transfer list window will probably only have 1 list with
      //Status indicating qued active complete
      //This method will have to be updated to ignore complete list items.
      if (joblist.size() == 0) {
         
         Preferences prefsTransfer  = Preferences.userRoot().node("net/amaras/fileexplorer/transfer");
         // Get property from pref menu     
         boolean closeTransfer = prefsTransfer.getBoolean(fileexplorer.pref.Transfer.CLOSE_TRANSFER, 
                              fileexplorer.pref.Transfer.CLOSE_TRANSFER_DEFAULT);
         if (closeTransfer == true) {
            transferlist.setVisible(false);
         }
         return true;
      } else {
         return false;
      }
   }
   
   public void completedJob(CopyCut completedJob) {
	   
      completedlist.add(completedJob);
	   joblist.removeElement(completedJob); //Removes first Element
      if (transferlist != null) {
         String completemsg = CopyCut.statusString[completedJob.getStatus()] +
         					  " :"+ completedJob.getMode() + ": " +
                              completedJob.getSource() + " : " + 
                              completedJob.getDestination() ;
         
         //transferlist.add(completemsg);
         completedJob.getTransferlistitem().msgLabel.setText(completemsg);
      }
      //Trigger Closing the Transfer Window if required
      joblistComplete();
	  System.out.println("Complete Job - joblist Size:" + joblist.size());
   }
   
	
   public synchronized void add(int mode, String file, String dest){
		CopyCut newitem = new CopyCut(mode, file, dest);
      joblist.add(newitem);

      if (transferlist != null) {
         transferlist.add(newitem.getTransferlistitem().msgLabel);
      }
      
      //Notify thread that there is work to be done
      notify();
	};

   public void transferListSetVisible(boolean show){
      transferlist.setVisible(show);
   }

   
   public void transferListToggle(){
      transferlist.toggleVisible();
   }
}
