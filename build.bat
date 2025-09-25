set JAVA_HOME=D:\JDK11
set PATH=%JAVA_HOME%\bin;D:\WORK\apache-ant-1.10.7\bin;%PATH%
call ant -buildfile build.xml
copy log4j2.xml target\classes\log4j2.xml
copy build.properties target\classes\build.properties