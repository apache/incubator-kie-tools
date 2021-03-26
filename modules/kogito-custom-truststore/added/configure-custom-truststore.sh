#!/bin/sh
set -e

# imports
source "${KOGITO_HOME}"/launch/logging.sh

function prepareEnv() {
    # keep it on alphabetical order
    unset CUSTOM_TRUSTSTORE
    unset CUSTOM_TRUSTSTORE_PASSWORD
}

function configure() {
    configure_custom_truststore
}

# Exit codes
# 1 - General error
function configure_custom_truststore() {
    local defaultCustomTruststorePath="${KOGITO_HOME}/certs/custom-truststore"

    if [ ! -z "${CUSTOM_TRUSTSTORE}" ]; then
        CUSTOM_TRUSTSTORE_PATH="${defaultCustomTruststorePath}/${CUSTOM_TRUSTSTORE}"
        log_info "---> Configuring custom Java Truststore '${CUSTOM_TRUSTSTORE}' in the path ${defaultCustomTruststorePath}"
        if [ ! -f "${CUSTOM_TRUSTSTORE_PATH}" ]; then
            log_error "---> A custom truststore was specified ('${CUSTOM_TRUSTSTORE}'), but wasn't found in the path ${defaultCustomTruststorePath}. \
Make sure that the path is mounted and accessible in your container"
            exit 1
        fi
        CUSTOM_TRUSTSTORE_ARGS="-Djavax.net.ssl.trustStore=${CUSTOM_TRUSTSTORE_PATH}"
        if [ ! -z "${CUSTOM_TRUSTSTORE_PASSWORD}" ]; then
            CUSTOM_TRUSTSTORE_ARGS="${CUSTOM_TRUSTSTORE_ARGS} -Djavax.net.ssl.trustStorePassword=${CUSTOM_TRUSTSTORE_PASSWORD}"
        fi
    fi
}
