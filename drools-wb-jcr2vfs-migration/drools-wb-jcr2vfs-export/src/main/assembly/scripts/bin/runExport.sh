#!/bin/sh

mainClass=org.drools.workbench.jcr2vfsmigration.JcrExporterLauncher

# echo "Usage: ./runExport.sh"
# echo "For example: ./runExport.sh"
# echo "Some notes:"
# echo "- Working dir should be the directory of this script."
# echo "- Java is recommended to be JDK and java 6 for optimal performance"
# echo "- The environment variable JAVA_HOME should be set to the JDK installation directory"
# echo "  For example (linux): export JAVA_HOME=/usr/lib/jvm/java-6-sun"
# echo "  For example (mac): export JAVA_HOME=/Library/Java/Home"
# echo
# echo "Starting export app..."

# following jar has to be first on classpath because of https://bugzilla.redhat.com/show_bug.cgi?id=987920
DROOLSJBPM5_UBERJAR=`find ../libs -name "guvnor-jcr2vfs-migration-droolsjbpm-as-uberjar-5*"`

# You can use -Xmx128m or less too, but it might be slower
if [ ! -z "$JAVA_HOME" -a -f "$JAVA_HOME/bin/java" ]; then
    $JAVA_HOME/bin/java -Xms256m -Xmx1024m -server -cp $DROOLSJBPM5_UBERJAR:../libs/* ${mainClass} $*
else
    java -Xms256m -Xmx1024m -cp $DROOLSJBPM5_UBERJAR:../libs/* ${mainClass} $*
fi

if [ $? != 0 ] ; then
    echo
    echo "Error occurred. Check if \$JAVA_HOME ($JAVA_HOME) is correct."
    # Prevent the terminal window to disappear before the user has seen the error message
    sleep 20
fi
