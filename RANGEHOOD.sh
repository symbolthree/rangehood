#!/bin/sh
#############################################
# RANGEHOOD Command Line Shell Script
# $Header: /TOOL/RANGEHOOD/files/RANGEHOOD.sh 1     26/09/10 2:15p Christopher Ho $
#############################################

java -Dfile.encoding=UTF8 -cp \
lib/RANGEHOOD-1.1.jar:\
lib/jdom-jaxen-1.1.1.zip:\
lib/ojdbc6.jar:\
lib/SQLinForm.jar:\
lib/xstream-1.3.1.jar \
symplik.oracle.doc.RANGEHOOD
