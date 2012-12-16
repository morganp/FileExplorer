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

package amaras.runtime;

import fileexplorer.TransferListItem;

public class CopyCut {

	 public static final int COPY           = 1;
    public static final int CUT            = 2;
    public static final int DMG            = 4;
	
    public static int QUE            = 0;
    public static int ACTIVE         = 1;
    public static int COMPLETE       = 2;
    public static int CANCELED       = 3;
    public static int ERROR          = 4;
   
    public static String [] statusString = {"Queued", "Active", "Completed", "Canceled", "Error"};
    
    
	private int    mode;
	private String source;
	private String destination;
    private int    status;
    private TransferListItem transferlistitem;
	
   public CopyCut (int mode, String source, String destination){
		this.mode        = mode;
		this.source      = source;
		this.destination = destination;
        this.status      = QUE;
        transferlistitem = new TransferListItem("Queued   :" + msgEnd());
	}

	
	public int getMode() {
		return mode;
	}
	
	public String getSource(){
		return source;
	}
	
	public String getDestination(){
		return destination;
	}
   
   public int getStatus(){
		return status;
	}
   
   public TransferListItem getTransferlistitem(){
      return transferlistitem;
   }
   
   public void setQued(){
      status = QUE;
      transferlistitem.setMessage("Queued   :"+ msgEnd());
   }
   
   public void setActive(){
      status = ACTIVE;
      transferlistitem.setMessage("Active   :"+ msgEnd());
   }
   
   public void setComplete(){
      status = COMPLETE;
      transferlistitem.setMessage("Complete :"+ msgEnd());
   }
   
   public void setError(){
	  status = ERROR;
	  transferlistitem.setMessage("Error :"+ msgEnd());
   }
   
   public void setCanceled(){
      status = CANCELED;
      transferlistitem.setMessage("Canceled :"+ msgEnd());
   }
   
   private String msgEnd() {
      return mode +" : "+ source +" : "+ destination ;
   }
}
