#!/bin/bash
set -e
# Updates the version for all Uberfire modules

initializeWorkingDirAndScriptDir() {
    # Set working directory and remove all symbolic links
    workingDir=`pwd -P`

    # Go the script directory
    cd `dirname $0`
    # If the file itself is a symbolic link (ignoring parent directory links), then follow that link recursively
    # Note that scriptDir=`pwd -P` does not do that and cannot cope with a link directly to the file
    scriptFileBasename=`basename $0`
    while [ -L "$scriptFileBasename" ] ; do
        scriptFileBasename=`readlink $scriptFileBasename` # Follow the link
        cd `dirname $scriptFileBasename`
        scriptFileBasename=`basename $scriptFileBasename`
    done
    # Set script directory and remove other symbolic links (parent directory links)
    scriptDir=`pwd -P`
}

mvnVersionsSet() {
    newVersion=$1
    mvn -B -N -Dfull versions:set -DnewVersion=$newVersion -DallowSnapshots=true -DgenerateBackupPoms=false
}

initializeWorkingDirAndScriptDir
uberfireTopLevelDir="$scriptDir/../.."

if [ $# != 1 ] ; then
    echo
    echo "Usage:"
    echo "  $0 newVersion"
    echo "For example:"
    echo "  $0 0.9.0.Final"
    echo
    exit 1
fi

newVersion=$1

startDateTime=`date +%s`

cd $uberfireTopLevelDir
mvnVersionsSet $newVersion

endDateTime=`date +%s`
spentSeconds=`expr $endDateTime - $startDateTime`

echo
echo "Total time: ${spentSeconds}s"
