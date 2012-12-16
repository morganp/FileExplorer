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

package amaras.filesort;

import java.io.File;
import java.lang.String;
import java.util.Vector;

public class DirectoryView {
	public final static int SORT_OPTIONS = 3;
	//File Sorting Modes
	public final static int MERGED  	   = 0;
	public final static int FOLDERS_FILES  = 1;
	public final static int FILES_FOLDERS  = 2;
	public final static String[] text = {"Merged", "Folders & Files", "Files & Folders"};
	
	//Contents List
	private Vector dirView;

	
	public DirectoryView (File dir, boolean showHidden, int mode){
		dirView = new Vector();
		
//		System.out.println("DirectoryView new call " + mode);
		
		if (dir.isDirectory()) {
			File[] listOfFiles = dir.listFiles();
			for (int i=0; i< listOfFiles.length; i++){
				
				if (showHidden == true) {
					dirView.add(listOfFiles[i]); //.getName())
				} else {
					if (listOfFiles[i].isHidden() == false) {
						dirView.add(listOfFiles[i]);
					}
				}
			}
//			System.out.println("DirectoryView about to enter Switch " + mode);
			//switch (mode) {
			//case MAC_SHOW_HIDDEN : java.util.Collections.sort(dirView); //dirView = sortfoldersSeperate(dirView);
			//case MAC_HIDE_HIDDEN : dirView; //dirView = sortfoldersSeperate(dirView);
			if (mode == FOLDERS_FILES ) {
//				System.out.println("Folders Files " + mode);
				dirView = sortfoldersfilesSeperate(FOLDERS_FILES, dirView);
				}
			if (mode == FILES_FOLDERS) {
//				System.out.println("Files Folders " + mode);
				dirView = sortfoldersfilesSeperate(FILES_FOLDERS, dirView);
				}
			//}
			
		}
		
		
	}
	
	public Vector getDirectoryList(){
	   return dirView;
	}
	
	private Vector sortfoldersfilesSeperate(int mode, Vector unsorted){
	   Vector folders = new Vector();
	   Vector files   = new Vector();
	   Vector merged  = new Vector();
	   
	   for (int i=0; i<unsorted.size(); i++){
		   File test = (File) unsorted.get(i);
		   if (test.isDirectory() == true) {
			   folders.add(test);
		   } else {
			   files.add(test);
		   }
	   }
	   java.util.Collections.sort(folders);
	   java.util.Collections.sort(files);
//	   System.out.println("About to choose sorting order");
	   if (mode == FOLDERS_FILES) {
//		   System.out.println("Folders Files " + mode);
		   merged.addAll(folders);
		   merged.addAll(files);
	   } else if (mode == FILES_FOLDERS) {
//		   System.out.println("Files Folders " + mode);
		   merged.addAll(files);
		   merged.addAll(folders);
	   }// else {
		//   merged.addAll(folders);
		//   merged.addAll(files);
	   //}
	   return merged;
	}
	
}
