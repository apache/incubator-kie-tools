#!/bin/sh

mainClass=org.drools.workbench.jcr2vfsmigration.Jcr2VfsMigrationApp

# echo "Usage: ./runMigration.sh"
# echo "For example: ./runMigration.sh"
# echo "Some notes:"
# echo "- Working dir should be the directory of this script."
# echo "- Java is recommended to be JDK and java 6 for optimal performance"
# echo "- The environment variable JAVA_HOME should be set to the JDK installation directory"
# echo "  For example (linux): export JAVA_HOME=/usr/lib/jvm/java-6-sun"
# echo "  For example (mac): export JAVA_HOME=/Library/Java/Home"
# echo
# echo "Starting migration app..."

# You can use -Xmx128m or less too, but it might be slower
# TODO: Change guvnor-jcr2vfs-migration-droolsjbpm-as-uberjar-5.5.1-20130606.153149-125-jars-as-uberjar.jar to a release version
if [ -f $JAVA_HOME/bin/java ]; then
    $JAVA_HOME/bin/java -Xms256m -Xmx1024m -server -cp ../libs/guvnor-jcr2vfs-migration-droolsjbpm-as-uberjar-5.5.1-20130606.153149-125-jars-as-uberjar.jar:../libs/* ${mainClass} $*
else
    java -Xms256m -Xmx1024m -cp ../libs/guvnor-jcr2vfs-migration-droolsjbpm-as-uberjar-5.5.1-20130606.153149-125-jars-as-uberjar.jar:../libs/* ${mainClass} $*
fi

if [ $? != 0 ] ; then
    echo
    echo "Error occurred. Check if \$JAVA_HOME ($JAVA_HOME) is correct."
    # Prevent the terminal window to disappear before the user has seen the error message
    sleep 20
fi
