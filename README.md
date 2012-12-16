FileExplorer
==

*NB: Originally Released on [SourceForge][]*

Java based file explorer. Tested with Mac OS X 10.5 and Windows XP. 

Added features missing from Finder:

1) Editable Location bar.   
2) Cut and Paste.   
3) Paste merges folder contents.   
4) Queued file transfers.  
5) Open folder in new window.  
6) Right click create .DMG (Mac OS X only feature)

Taking the best features from Windows Explorer, Mac Finder, Firefox and adding some more. FileExplorer allows cursor based file browsing similar to Mac Finder, left cursor is up, with one folder selected right cursor will take you down in to that folder. Switching between showing/hiding hidden files is fast, a hassle on most other file browsers.

File sorting can be fully alphabetical (files and folders merged) Like Mac Finder, Files always on top or Folders always on top. Windows Explorer on the other hand will switch between file/folders on top depending on sort ascending or descending!

It allows you to cut and paste files on the Mac! The Cut/Copy & Paste is merged like windows explorer, not overwritten like Mac finder (Finder on OS X Lion has changed to this type of behavior

Compile
--

To run program from a comandline (with debug info to console):

    ant run-jar

To create release I have been running, for release version 0.1.1:

    build_dist.sh 0.1.1

If executed on OS X this will create the .app and package it as a .dmg for distribution. The tar.gz of source code and .jar will also be created. The process for creating the windows executable is to build the jar file then run [JSmooth][] with the template in the windows_launcher directory.


Development has now stopped
--

Since OS X Mountain Lion Java is now longer supported out the box. As this was intended to be a cross platform tool my motivation for carrying on development has gone.


[SourceForge]: http://sourceforge.net/projects/fileexplorer/
[JSmooth]: http://jsmooth.sourceforge.net/
