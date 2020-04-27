#!/bin/bash
#
# Amforeas Startup Script for Linux
# Depends on pkill and bash 4+
#

if [ "${JAVA_HOME}" == "" ]; then
    echo "JAVA_HOME Not set. Install the JRE and set the JAVA_HOME in your initialization file"
    exit 1
fi

if [ ! -d "${JAVA_HOME}" ] ; then
    echo "Java ${JAVA_HOME} Directory doesn't exist."
    exit 1
else
    if [ ! -x "${JAVA_HOME}/bin/java" ] ; then
        echo "Java binary error: not found or not executable"
        exit 1
    fi
fi

do_start () {
    local path="lib/*:etc"
    local opts=""

    local exit_code=10
    while [ $exit_code -eq 10 ]; do
        "${JAVA_HOME}/bin/java" ${opts} -cp "${path}" amforeas.JongoJetty
        exit_code=$?
    done
}

do_demo () {
    local opts="-Denvironment=demo"
    local path="lib/*:etc"
    "${JAVA_HOME}/bin/java" ${opts} -cp "${path}" amforeas.JongoJetty
}

do_stop () {
    pkill -f "amforeas.AmforeasJetty"
    exit $?
}

do_status () {
    pkill -0 -f "amforeas.AmforeasJetty" > /dev/null 2>&1 && echo "Process is running" && exit 0
    echo "Amforeas is not running" && exit 0
}

case ${1} in
    start)
        do_start
    ;;

    demo)
        do_demo
    ;;

    stop)
        do_stop
    ;;

    status)
        do_status
    ;;

    nohup)
        nohup $0 start > /dev/null 2>&1 &
    ;;

    *)
        echo "Usage: $0 start|nohup|stop|status"
        exit 1
    ;;
esac

exit 0
