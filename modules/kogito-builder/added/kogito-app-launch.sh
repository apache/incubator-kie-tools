#!/usr/bin/env bash
#
# S2I run script for the 'kogito-builder' image.
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

)

source ${S2I_MODULE_LOCATION}/s2i-core
source "${KOGITO_HOME}"/launch/configure.sh

runtime_type=$(get_runtime_type)

#############################################

case ${runtime_type} in 
    "quarkus")  
        if [ "${NATIVE^^}" == "TRUE" ]; then
            # shellcheck disable=SC2086
            exec "${KOGITO_HOME}"/bin/*-runner ${JAVA_OPTIONS} ${KOGITO_QUARKUS_S2I_PROPS} \
                -Dquarkus.http.host=0.0.0.0 -Djava.library.path="${KOGITO_HOME}"/ssl-libs \
                -Dquarkus.http.port=8080 \
                -Djavax.net.ssl.trustStore="${KOGITO_HOME}"/cacerts
        else
        # shellcheck disable=SC2086
            exec java ${JAVA_OPTIONS} ${KOGITO_QUARKUS_S2I_PROPS} -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080 -jar "${KOGITO_HOME}"/bin/*.jar
        fi
    ;;
    "springboot") # shellcheck disable=SC2086
        exec java ${JAVA_OPTIONS} ${KOGITO_SPRINGBOOT_S2I_PROPS} -Dserver.address=0.0.0.0 -Dserver.port=8080 -jar "${KOGITO_HOME}"/bin/*.jar
    ;;
    *)
        log_error "${runtime_type} is not supported."
        exit 1
esac