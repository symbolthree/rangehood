@echo off
setlocal
REM ############################################
REM # RANGEHOOD Command Line Batch File
REM #############################################

where java.exe >nul 2>&1
if %errorlevel% equ 0 (
java.exe ^
-Xms64m -Xmx1024m -XX:+UseParallelGC ^
-Dlog4j2.configurationFile=log4j2.xml ^
-Dfile.encoding=UTF8 ^
-cp rangehood-2.0.jar ^
symbolthree.oracle.doc.RANGEHOOD %*
) else (
  echo java.exe not found in PATH.
)

endlocal