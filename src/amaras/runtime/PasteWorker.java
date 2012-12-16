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

import java.io.*;
import java.util.Vector;
import javax.swing.*;

public class PasteWorker extends Thread{
   private static int debugOn        = 1;
   PasteQue pasteque = new PasteQue();
   
   public PasteWorker(PasteQue pasteque) {
      this.pasteque = pasteque;
   }

   
   
   public void run() {
      System.out.println("Starting Running Paste Thread");
      while ( true) {
            System.out.println("Paste Thread Looping : checking wait() notify()");
            CopyCut currentJob = pasteque.getJob();
            
            int    mode   = currentJob.getMode(); 
            String source = currentJob.getSource();
            String dest   = currentJob.getDestination();
             currentJob.setActive();
         
            System.out.println("" + mode + " : " + source + " : " + dest);
            //Do the hard Work and copy the file
            int exitCode = pasteHardWork(mode, source, dest);
            System.out.println("PasteWorker.java -pasteHardWork- EXIT Code " + exitCode);
            
          //TODO Analyse completion status to display completed or cancelled
            if (exitCode == 0) {
               currentJob.setComplete();
            } else if (exitCode == -1)  {
             currentJob.setCanceled();
            } else {
            currentJob.setError();
            }
            
            pasteque.completedJob(currentJob);

      }
   }

	public void pasteHardWorkDebug(String msg) {
		if (false) {
			System.out.println("PasteWorker.java:pasteHardWork " + msg);
		}	
	}

   //###########################################################################
   public int pasteHardWork(int mode, String file, String dest) {
		pasteHardWorkDebug("Enter pasteHardWork");
         int exitCode = -1;
      //if in non-replace mode (default)
      //and if $file is a file
      //and already exists in dest
      //then append _1 (recursive search) before last .
      int lastFileSep = file.lastIndexOf(File.separator) + 1;
      int lastDot     = file.lastIndexOf('.') ;
      
      //Set to end of file when no file extension exists
      if (lastDot == -1) { lastDot = file.length(); }
      
      String pathfilename = file.substring(0, lastDot     );
      String filename = file.substring(lastFileSep, lastDot     );
      String fileExt  = file.substring(lastDot,     file.length() );
      
      
      //System.out.println("Debug filename " + filename + ":" + lastFileSep + "," + lastDot);
      //System.out.println("Debug fileExt  " + fileExt);
      
      pasteHardWorkDebug("MODE is " + mode);
      
      if ((mode==CopyCut.COPY)||(mode==CopyCut.CUT)) {
      	pasteHardWorkDebug("Entering COPY or CUT Mode");
      
         //TODO COPY and CUT need to feel the same across Folders,Files
         //   This can only be enabled once CopyCut Folders get unbundled 
         //   to file by file operation.
         //put _x before .ext
         if ((false == true)&&
            ((new File(file)).isDirectory() == false)&&
            ((new File(dest)).isDirectory() == true)) {
         
               //TODO add a pref mode for overwiting files.
               //Include a quick one off ovewrite mode
               String destNew = new String(dest + filename + fileExt);
               for (int i=1; ((new File(destNew)).exists() == true); i++) {
                  destNew = dest + filename + "_" + i + fileExt;
               }
               dest = destNew;
         }
      
         System.out.println("" + mode + " : " + file + " : " + dest);
         //joblist.add(new CopyCut(mode, file, dest));
         System.out.println("Testing Testing Testing");
      
         String[] command;
         //For now add rntime exec in here
         //OS Specific ways of launching file into default handler
         try {
            if ((System.getProperty("os.name").contains("Mac"))|
                (System.getProperty("os.name").contains("nix")) ) {
                  command = new String[4];
                  command[0] = "cp";
                  command[1] = "-R";
                  // //For Cut CP -R then a rm
                  // //To Handle folder merges properly 
                  command[2] = file;
                  command[3] = dest;
               } else if (System.getProperty("os.name").contains("Win")) {
            // if (mode == CopyCut.COPY){
                  if ((new File(file)).isDirectory() == true) {
                     command = new String[14];
                     command[0] = "cmd.exe";
                     command[1] = "/c";
                     command[2]  = "XCOPY";
                     command[5]  = "/S"; //Copy Folders & Subfolder
                     command[6]  = "/E"; //Include Empty Folders & Subfolder
                     command[7]  = "/H"; //Copy System & hidden
                     command[8]  = "/C"; //Continue if Error
                     command[9]  = "/I"; //Assume dest is Folder if selecting multiple and dest not exists
                     command[10] = "/K"; //Copy attributes
                     command[11] = "/X"; //Copy file audit settings
                     command[12] = "/F"; //Output files coppied
                     command[13] = "/Y"; //Forece Ovewrite if destination files exist
               
               		//Force windows to COPY to the correct path
               		if ((new File(dest)).isDirectory() == true) {
                      //filename = 
                      	if (dest.endsWith(File.separator) == true) {
                        	dest = dest + filename;
                      	} else {
                         	dest = dest + File.separator + filename;
                      	}
               		}
               		command[3] = file;
               		command[4] = dest;
               
            		} else {
               		//File based Copy
               		command = new String[6];
               		command[0] = "cmd.exe";
               		command[1] = "/c";
               		command[2] = "COPY";
               		command[3] = "/Y";  //Do not prompt
               		command[4] = file;
               		command[5] = dest;
            		}
         		} else {
						//Not MAC or Windows
            		//Assign default null values
            		command = new String[1];
            		command[0] = " ";
         		}
         
         
         		//Run r = new amaras.Run(command[0]);
         		exitCode = runcommand(command);
         		if (exitCode != 0) {
            		System.out.println("Copy Section of CUT exited uncleanly not safe to remove Origianl files");
            		JOptionPane.showMessageDialog(null, "COPY original File Failed" + file, "alert", JOptionPane.ERROR_MESSAGE); 
                	return -2;
         		}
         
         
         System.out.println(" Entering CUT Sequence");
         //TEMP fix for merged cut paste
         if (((System.getProperty("os.name").contains("Mac"))|
            (System.getProperty("os.name").contains("nix"))) &
              (mode == CopyCut.CUT) ){
               command = new String[3];
               //CUT is CP -R then a rm
               //To Handle folder merges properly
               command[0] = "rm";
               //command[0] = "echo";
               command[1] = "-rf";
               command[2] = file;
                 
               exitCode = runcommand(command);
                  
               // if (exitCode != 0) {
               //                   JOptionPane.showMessageDialog(null, "CUT failed to remove original File" + file, "alert", JOptionPane.ERROR_MESSAGE); 
               //                   return -2;
               //                }
            }
            
         if (System.getProperty("os.name").contains("Win") &
           (mode == CopyCut.CUT) ) {
              if ((new File(file)).isDirectory() == true) {
                  //DEL wil not delete current node
                  command = new String[6];
                  command[0] = "cmd.exe";
                  command[1] = "/c";
                  command[2] = "RD";
                  command[3] = "/S"; //Remove all including this node
                  command[4] = "/Q"; //Do Not prompt for confimation
                  command[5] = file;
              } else {
                  //RD fails on Files
                  command = new String[6];
                  command[0] = "cmd.exe";
                  command[1] = "/c";
                  command[2] = "DEL";
                  command[3] = "/F"; //Force Delete of Read only
                  command[4] = "/Q"; //Do Not prompt for confimation
                  command[5] = file;
              }
              
              exitCode = runcommand(command);
              
              // if (exitCode != 0) {
              //                  JOptionPane.showMessageDialog(null, "CUT failed to remove original File" + file, "alert", JOptionPane.ERROR_MESSAGE); 
              //                  return -2;
              //               }
            }
            
            //TODO check last exit code set
            return exitCode;

      } catch (IOException e) {
         System.out.println("PasteWorker.java:pasteHardWork - IOException");
         System.err.println(e.getMessage());
         return -2;
      } catch (Exception e) {
         System.out.println("PasteWorker.java:pasteHardWork - Exception");
         e.printStackTrace();
         return -2;
      }
      }
      if (mode == CopyCut.DMG) {
        String command [] = new String[6];
        // hdiutil create "./dist/FileExplorer.$1.dmg" -srcfolder ./FileExplorer_dmg/ -volname "File Explorer" -ov
        command[0] = "hdiutil";
        command[1] = "create";
        command[2] = pathfilename + ".dmg";
        command[3] = "-srcfolder";
        command[4] = file;
        command[5] = "-ov";
        
        try {
           exitCode = runcommand(command);
        } catch (IOException e) {
         System.out.println("PasteWorker.java:pasteHardWork - IOException - MODE DMG");
         System.err.println(e.getMessage());
         return -2;
        } catch (Exception e) {
           System.out.println("PasteWorker.java:pasteHardWork - Exception - MODE DMG");
           e.printStackTrace();
           return -2;
        }
        return exitCode ;
      }
      
      //All else fails get to this point
      pasteHardWorkDebug(" Defult return -2");
      return -2 ;
   }
   
   private int runcommand(String[] command) throws IOException, Exception {
         System.out.println();
         System.out.println();
         
         for (int i=0; i<command.length -1; i++){
            System.out.print(command[i] + " ");
         }
         System.out.println(command[command.length-1]);
         System.out.println();         

         Process p = Runtime.getRuntime().exec(command);   
          
         InputStream is = p.getInputStream();
         try {
            int b;
            while ((b = is.read()) != -1)
               System.out.write(b);
         } finally {
            is.close();
         }
         int exitCode = p.waitFor(); 
         debug("" + exitCode + ": PasteWorker exit code");
         return exitCode;
   }
   
   private void debug (String text) {
      if (debugOn == 1) {
         System.out.println(text);
      }
   }
   
}
