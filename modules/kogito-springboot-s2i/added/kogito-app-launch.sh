#!/usr/bin/env bash
#
# S2I run script for the 'kogito-springboot-ubi8-s2i' image.
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
  ${KOGITO_HOME}/launch/kogito-springboot-s2i.sh
)
source ${KOGITO_HOME}/launch/configure.sh
#############################################

exec java ${JAVA_OPTIONS} ${KOGITO_SPRINGBOOT_S2I_PROPS} -jar $KOGITO_HOME/bin/*.jar

