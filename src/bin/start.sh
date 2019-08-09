#!/bin/bash

cd `dirname $0`/..

WORKING_HOME=`pwd`

MY_CLASSPATH="conf"

MY_CLASSPATH=${MY_CLASSPATH}":"`ls lib/ | awk '{print "lib/"$1}' | tr '\n' ':'`

java -classpath ${MY_CLASSPATH} -Dfile.encoding=UTF-8 -DWORKING_HOME=${WORKING_HOME} youli/open/filesync/client/jface/FileSyncSWTClient