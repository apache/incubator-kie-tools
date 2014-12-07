#!/bin/sh

exporterMainClass=org.drools.workbench.jcr2vfsmigration.JcrExporterLauncher
importerMainClass=org.drools.workbench.jcr2vfsmigration.VfsImporterLauncher

# echo "Usage: ./runMigration.sh -i <path-to-jcr-repository> -o <vfs-repository>"
# echo "For example: ./runMigration.sh"
# echo "Some notes:"
# echo "- Working dir should be the directory of this script."
# echo "- Java is recommended to be JDK and java 6 for optimal performance"
# echo "- The environment variable JAVA_HOME should be set to the JDK installation directory"
# echo "  For example (linux): export JAVA_HOME=/usr/lib/jvm/java-6-sun"
# echo "  For example (mac): export JAVA_HOME=/Library/Java/Home"
# echo
# echo "Starting the jcr2vfs migraiton app..."

if [ ! -z "$JAVA_HOME" -a -f "$JAVA_HOME/bin/java" ]; then
   JAVA_BIN=$JAVA_HOME/bin/java
else
   JAVA_BIN=java
fi

$JAVA_BIN -Xms256m -Xmx1024m -cp "../jcr-exporter-libs/*" ${exporterMainClass}

$JAVA_BIN -Xms256m -Xmx1024m -cp "../vfs-importer-libs/*" ${importerMainClass}

if [ $? != 0 ] ; then
    echo
    echo "Error occurred. Check if \$JAVA_HOME ($JAVA_HOME) is correct."
    # Prevent the terminal window to disappear before the user has seen the error message
    sleep 20
    exit
fi
