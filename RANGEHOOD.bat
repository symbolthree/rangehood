@echo off
setlocal
REM ############################################
REM # RANGEHOOD Command Line Batch File
REM # $Header: /TOOL/RANGEHOOD/files/RANGEHOOD.bat 1     26/09/10 2:15p Christopher Ho $
REM #############################################

java.exe -Dfile.encoding=UTF8 -cp ^
lib\RANGEHOOD-1.1.jar;^
lib\jdom-jaxen-1.1.1.zip;^
lib\ojdbc8.jar;^
lib\SQLinForm.jar;^
lib\xstream-1.3.1.jar ^
symplik.oracle.doc.RANGEHOOD
endlocal