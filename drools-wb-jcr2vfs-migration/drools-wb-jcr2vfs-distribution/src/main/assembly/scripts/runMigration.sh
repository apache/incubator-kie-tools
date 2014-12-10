#!/bin/sh
# Copyright 2014 JBoss Inc
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# Script used to execute the jcr2vfs migration tool.
#
# Execution consists of two phases:
#   1) Exporting the content of JCR repository into XML
#   2) Importing the XML (generated in the previous step) into newly created VFS Git repository

function print_help() {
    echo "Usage: ./runMigration.sh [options]"
    echo "Description: Migrates Guvnor 5.x JCR content into UberFire VFS repositoriy."
    echo
    echo "Where [options]:"
    echo "    -h         Prints this help"
    echo "    -i <dir>   Directory with the JCR repository configuration"
    echo "    -o <dir>   Directory to store the migrated VFS repository in. Optional, defaults to './outputVfs'"
    echo "    -r <name>  VFS repository name. Optional, defaults to 'guvnor-jcr2vfs-migration'"
    echo "    -f         Force overwriting the resulting VFS repository. Optional"
    echo
    echo "Notes:"
    echo "  - Working dir needs to be the directory of this script!"
    echo "  - Java is recommended to be JDK and version 6 or later"
    echo "  - The environment variable JAVA_HOME should be set to the JDK installation directory"
    echo "       For example (linux): export JAVA_HOME=/usr/lib/jvm/java-6-sun"
    echo "       For example (mac): export JAVA_HOME=/Library/Java/Home"
}

# Change directory to the directory of the script
cd `dirname $0`

OPTIND=1 # reset in case getopts has been used previously in the shell

EXPORT_ARGS=""
IMPORT_ARGS=""
TMP_DIR="`pwd`/tmp-jcr2vfs"

# flags to determine if the JCR input / VFS output parameters have been specified
JCR_REPO_DIR_SET="false"
VFS_REPO_DIR_SET="false"

while getopts "hi:o:r:ft:" opt; do
    case "$opt" in
    h|\?)
        print_help
        exit 0
        ;;
    i)  EXPORT_ARGS="$EXPORT_ARGS -i $OPTARG"
        JCR_REPO_DIR_SET="true"
        ;;
    o)  IMPORT_ARGS="$IMPORT_ARGS -o $OPTARG"
        VFS_REPO_DIR_SET="true"
        ;;
    r)  IMPORT_ARGS="$IMPORT_ARGS -r $OPTARG"
        ;;
    f)  IMPORT_ARGS="$IMPORT_ARGS -f"
        ;;
    t) TMP_DIR=${OPTARG}
        ;;
    esac
done

if [ ${JCR_REPO_DIR_SET} == "false" ]
then
    echo "JCR respository location needs to be specified using the -i <dir>!"
    print_help
    exit 0
fi

# add default VFS output dir if none specified
if [ ${VFS_REPO_DIR_SET} == "false" ]
then
    IMPORT_ARGS="$IMPORT_ARGS -o ./outputVfs"
fi

EXPORT_ARGS="$EXPORT_ARGS -o $TMP_DIR"

IMPORT_ARGS="$IMPORT_ARGS -i $TMP_DIR"

if [ ! -z "${JAVA_HOME}" -a -f "${JAVA_HOME}/bin/java" ]; then
   JAVA_BIN=${JAVA_HOME}/bin/java
else
   JAVA_BIN=java
fi

EXPORTER_MAIN_CLASS=org.drools.workbench.jcr2vfsmigration.JcrExporterLauncher
IMPORTER_MAIN_CLASS=org.drools.workbench.jcr2vfsmigration.VfsImporterLauncher

${JAVA_BIN} -Xms256m -Xmx1024m -cp "../jcr-exporter-libs/*" ${EXPORTER_MAIN_CLASS} ${EXPORT_ARGS}

${JAVA_BIN} -Xms256m -Xmx1024m -cp "../vfs-importer-libs/*" ${IMPORTER_MAIN_CLASS} ${IMPORT_ARGS}

if [ $? != 0 ] ; then
    echo
    echo "ERROR: Check if Java is installed and environment variable JAVA_HOME ($JAVA_HOME) is correct."
    # Prevent the terminal window to disappear before the user has seen the error message
    read -p "Press [Enter] key to close this window." dummyVar
fi
