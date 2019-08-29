#!/bin/bash

cd `dirname $0`/..

WORKING_HOME=`pwd`

LOG_HOME=${WORKING_HOME}"/logs"

if [[ ! -d ${LOG_HOME} ]]; then
    mkdir ${LOG_HOME}
fi

STDOUT_FILE=${LOG_HOME}"/start.log"

MY_CLASSPATH="conf"

MY_CLASSPATH=${MY_CLASSPATH}":"`ls lib/ | awk '{print "lib/"$1}' | tr '\n' ':'`

java -classpath ${MY_CLASSPATH} -Dfile.encoding=UTF-8 -DWORKING_HOME=${WORKING_HOME} youli/open/filesync/client/swing/FileSyncSwingClient > $STDOUT_FILE 2>&1 &