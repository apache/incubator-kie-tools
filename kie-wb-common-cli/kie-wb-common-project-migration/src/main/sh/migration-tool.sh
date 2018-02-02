#!/bin/sh

SCRIPTDIR="`dirname $0`"
export CLASSPATH="`find "$SCRIPTDIR/../lib" -name '*.jar' | tr '\n' ':' | sed -E 's/:$//'`"

java ${mainClass} $@

