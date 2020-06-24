#!/usr/bin/env bash
#
# S2I run script for the 'kogito-quarkus-jvm' images.
# The run script executes the server that runs your application.
#
# For more information see the documentation:
#	https://github.com/openshift/source-to-image/blob/master/docs/builder_image.md
#
# Image help.
if [[ "$1" == "-h" ]]; then
    exec /usr/local/s2i/usage
    exit 0
fi

# Configuration scripts
# Any configuration script that needs to run on image startup must be added here.
CONFIGURE_SCRIPTS=(
  ${KOGITO_HOME}/launch/kogito-quarkus-jvm.sh
)
source ${KOGITO_HOME}/launch/configure.sh
#############################################

exec java ${JAVA_OPTIONS} ${KOGITO_QUARKUS_JVM_PROPS} -Dquarkus.http.host=0.0.0.0 -jar $KOGITO_HOME/bin/*runner.jar
