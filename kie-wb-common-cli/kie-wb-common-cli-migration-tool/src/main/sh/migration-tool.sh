#!/bin/sh
#SCRIPTDIR="cd $(dirname $0) && pwd"

saveddir=`pwd`
SCRIPT_HOME=`dirname "$0"`/..

# make it fully qualified
SCRIPTDIR=`cd "$SCRIPT_HOME" && pwd`
cd "$saveddir"

CLASSPATH="$SCRIPTDIR/lib/*"

if [ -z "$JAVACMD" ] ; then
  if [ -n "$JAVA_HOME"  ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
      # IBM's JDK on AIX uses strange locations for the executables
      JAVACMD="$JAVA_HOME/jre/sh/java"
    else
      JAVACMD="$JAVA_HOME/bin/java"
    fi
  else
    JAVACMD="`which java`"
  fi
fi

if [ ! -x "$JAVACMD" ] ; then
  echo "Error: JAVA_HOME is not defined correctly."
  echo "  We cannot execute $JAVACMD"
  exit 1
fi
JAVAVER=`"$JAVACMD" -version 2>&1`

case $JAVAVER in
*1.[8-9]*) ;;
*1.[1-7]*)
	echo " Error: a Java 1.8 or higher JRE is required to run Migration tool; found [$JAVACMD -version == $JAVAVER]."
	exit 1
 ;;
esac

exec "$JAVACMD" -cp "$CLASSPATH" ${mainClass} $@

