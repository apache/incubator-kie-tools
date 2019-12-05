#!/usr/bin/env bash
#
# S2I run script for the 'kogito-quarkus' images.
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

exec $KOGITO_HOME/bin/*-runner ${JAVA_OPTIONS} -Dquarkus.http.host=0.0.0.0 \
    -Dquarkus.http.port=8080 -Djava.library.path=$KOGITO_HOME/ssl-libs \
    -Djavax.net.ssl.trustStore=$KOGITO_HOME/cacerts
