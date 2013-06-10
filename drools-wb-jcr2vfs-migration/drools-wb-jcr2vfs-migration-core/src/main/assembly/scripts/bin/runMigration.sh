#!/bin/sh

mainClass=org.drools.workbench.jcr2vfsmigration.Jcr2VfsMigrationApp

rem echo "Usage: ./runMigration.sh"
rem echo "For example: ./runMigration.sh"
rem echo "Some notes:"
rem echo "- Working dir should be the directory of this script."
rem echo "- Java is recommended to be JDK and java 6 for optimal performance"
rem echo "- The environment variable JAVA_HOME should be set to the JDK installation directory"
rem echo "  For example (linux): export JAVA_HOME=/usr/lib/jvm/java-6-sun"
rem echo "  For example (mac): export JAVA_HOME=/Library/Java/Home"
rem echo
rem echo "Starting migration app..."

# You can use -Xmx128m or less too, but it might be slower
if [ -f $JAVA_HOME/bin/java ]; then
    $JAVA_HOME/bin/java -Xms256m -Xmx1024m -server -cp ../libs/* ${mainClass} $*
else
    java -Xms256m -Xmx1024m -cp ../libs/* ${mainClass} $*
fi

if [ $? != 0 ] ; then
    echo
    echo "Error occurred. Check if \$JAVA_HOME ($JAVA_HOME) is correct."
    # Prevent the terminal window to disappear before the user has seen the error message
    sleep 20
fi
