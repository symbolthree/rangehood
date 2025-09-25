#!/bin/sh

export JAVA_OPTS="-Xms64m -Xmx1024m -XX:+UseParallelGC"
java -Dlog4j2.configurationFile=log4j2.xml \
-Dfile.encoding=UTF8 \
-cp rangehood-2.0.jar \
symbolthree.oracle.doc.RANGEHOOD $*

