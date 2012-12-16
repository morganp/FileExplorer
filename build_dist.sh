#!/bin/sh

rev=$1

ant clean

rm ./FileExplorer.exe
rm ./dist/FileExplorer*.dmg
rm ./dist/*.tar.gz
rm ./dist/*.jar

ant

macname=`uname -a | grep -i darwin`
if [ -n "$macname" ];
then
   echo "Create dmg to Distribute Mac OS X .app"
   ln -s /Applications ./FileExplorer_dmg/
   
   hdiutil create "./dist/FileExplorer.$1.dmg" -srcfolder ./FileExplorer_dmg/ -volname "File Explorer" -ov
else 
   echo "Not a mac can not build dmg Mac distribution"
fi


echo "Create Named Jar for distribution"
mv ./jars/FileExplorer.jar "./dist/FileExplorer.$1.jar"

echo "Create Source tar.gz"
tar -czf "./dist/FileExplorer-src.$1.tar.gz" ./src/ ./lib/ ./resources/ ./resources_macosx/ ./windows_launcher/ ./build_dist.sh ./build.xml 

## Original Code for SourceForge Subversion
#echo "You should really Tag if your going to Release do:"
#echo "svn copy https://fileexplorer.svn.sourceforge.net/svnroot/fileexplorer/trunk/ \
# https://fileexplorer.svn.sourceforge.net/svnroot/fileexplorer/tags/Release/$1 \
# -m 'Tagging the $1 release' "
#
#scp ./dist/*$1* morgan_prior,fileexplorer@frs.sourceforge.net:/home/frs/project/f/fi/fileexplorer/fileexplorer

# Tag in git
git tag -a $rev -m "Tagging gem release $rev"

# Push tag to origin (github)
git push origin $rev


