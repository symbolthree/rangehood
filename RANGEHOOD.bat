@echo off
setlocal
del rangehood.log

D:\jdk11\bin\java.exe ^
-Xms64m -Xmx1024m -XX:+UseParallelGC ^
-Dfile.encoding=UTF8 -cp ^
target\classes;^
lib\xmlpull-1.1.3.4a.jar;^
lib\xstream-1.4.21.jar;^
lib\jdom-2.0.6.jar;^
lib\jaxen-2.0.0.jar;^
lib\xalan-2.7.3.jar;^
lib\serializer-2.7.3.jar;^
lib\ojdbc8.jar;^
lib\log4j-core-2.25.1.jar;^
lib\log4j-api-2.25.1.jar;^
lib\commons-cli-1.10.0.jar;^
lib\commons-io-2.20.0.jar;^
lib\commons-text-1.14.0.jar;^
lib\commons-lang3-3.18.0.jar;^
lib\dbtools-format-24.3.1.jar ^
symbolthree.oracle.doc.RANGEHOOD %*
endlocal