#!/usr/bin/env bash

if [ "${SCRIPT_DEBUG}" = "true" ] ; then
    set -x
    SHOW_JVM_SETTINGS="-XshowSettings:properties"
    echo "Script debugging is enabled, allowing bash commands and their arguments to be printed as they are executed"
    echo "JVM settings debug is enabled."
fi


# Configuration scripts
# Any configuration script that needs to run on image startup must be added here.
CONFIGURE_SCRIPTS=(
  ${KOGITO_HOME}/launch/kogito-infinispan-properties.sh
  ${KOGITO_HOME}/launch/kogito-jobs-service.sh
)
source ${KOGITO_HOME}/launch/configure.sh
#############################################

exec java ${SHOW_JVM_SETTINGS} ${JAVA_OPTIONS} ${KOGITO_JOBS_PROPS} ${INFINISPAN_PROPERTIES} \
    -jar $KOGITO_HOME/bin/kogito-jobs-service-runner.jar

