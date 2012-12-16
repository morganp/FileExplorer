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

//import String;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;

public class Run {
	private static final char SPACE = ' ';
	private static final char TAB = '\t';
	private static final char DOUBLEQUOTE = '\"';
	private static final char BACKSLASH = '\\';
	
	
	private static int debugOn        = 1;
	
	public static final int DIRECT = 0;
	
	protected Runtime currentRuntime;
	
	public Run(int mode, Vector launchFile) {
		try {
			
			if (mode == DIRECT){
				int launchFileSize = launchFile.size();
				
				
				if (System.getProperty("os.name").contains("Mac")){
					String [] command = new String[launchFileSize+2];
					command[0] = "open";
					command[1] = "-a";
					for (int i=0; i<(launchFileSize); i++) {
						command[i+2] = (String) launchFile.get(i);
					}
					Process p = Runtime.getRuntime().exec(command);
				} else if (System.getProperty("os.name").contains("Win")){
					String [] command = new String[launchFileSize];
					//command[0] = "cmd.exe";
					//command[1] = "/c";
					for (int i=0; i<(launchFileSize); i++) {
						command[i] = (String) launchFile.get(i);
					}
					Process p = Runtime.getRuntime().exec(command);
				}
				
				
				
			}
			
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
		      e.printStackTrace();
		}
	}
	
	public Run(Vector launchFile) {
		debug("Testing openFile");

		String[] command = new String[3];

		Vector commandVec = new Vector();
		//TODO if java >= 1.6 launch in getDesktop() style
		
		
		try {
			//Generic file launcher for java 1.6+
			//if (java.awt.Desktop.isDesktopSupported()) {
			if (true == false) {
			   ;
			   //java.awt.Desktop.getDesktop().open(new File(launchFile));
			} else {
				
				//TODO this needs to be a vector for iterating over multiple files
				
				//OS Specific ways of launching file into default handler
				if (System.getProperty("os.name").contains("Mac")){
					commandVec.add("open");
				}
				if (System.getProperty("os.name").contains("nix")){
					commandVec.add("echo");
					commandVec.add("File Launching Not supported");
				}
            
            if ((System.getProperty("os.name").contains("Mac"))||
               (System.getProperty("os.name").contains("nix"))){
               int commandVecSize = commandVec.size();
               int launchFileSize = launchFile.size();
               String[] commandString = new String[commandVecSize+launchFileSize];
               
               for (int i = 0 ; i < commandVecSize; i++){
                  commandString[i] =  (String) commandVec.get(i);
                  System.out.println(commandString[i]);
               }
               for (int i = 0 ; i < launchFileSize; i++){
                  commandString[i+commandVecSize] =  (String) launchFile.get(i);
                  System.out.println(commandString[i]);
               }
               
               Process p = Runtime.getRuntime().exec(commandString); 
            }
            
				if (System.getProperty("os.name").contains("Win")) {
					commandVec.add("cmd.exe");
					commandVec.add("/c");
               
               int commandVecSize = commandVec.size();
               int launchFileSize = launchFile.size();
               
               String[] commandString = new String[commandVecSize+1];
               
               for (int i = 0 ; i < commandVecSize; i++){
                  commandString[i] =  (String) commandVec.get(i);
                  System.out.println(commandString[i]);
               }
               for (int i = 0 ; i < launchFileSize; i++){
                  commandString[commandVecSize] =  (String) launchFile.get(i);
                  Process p = Runtime.getRuntime().exec(commandString);
               }
				}
				//commandVec.add(launchFile);
				

				
				
				  
				
            //This blocks the process When you just want to spawn new process
				//~ InputStream is = p.getInputStream();
				//~ try {
					//~ int b;
					//~ while ((b = is.read()) != -1)
						//~ System.out.write(b);
				//~ } finally {
					//~ is.close();
				//~ }
				//~ int exitCode = p.waitFor(); 
				//~ debug("" + exitCode);	
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
		      e.printStackTrace();
		}

	}
	 
	private void debug (String text) {
		if (debugOn == 1) {
			System.out.println(text);
		}
	}
	
}
