#!/bin/sh
chemin=$(pwd)
framework="$chemin/Framework/"

cd -- "$framework"
echo ${framework}
javac -d . *java
jar cf fw.jar .
mv fw.jar ../WEB-INF/lib/
cd ../WEB-INF/classes
javac -cp .:../lib/* -d . *.java
cd ../..
jar -cf Cluster.war .
cp Cluster.war /var/lib/tomcat9/webapps