#!/bin/bash -e

#import
source ${KOGITO_HOME}/launch/logging.sh

function prepareEnv() {
    # keep it on alphabetical order
    unset INFINISPAN_AUTHREALM
    unset INFINISPAN_PASSWORD
    unset INFINISPAN_SASLMECHANISM
    unset INFINISPAN_USEAUTH
    unset INFINISPAN_USERNAME
}

function configure() {
    configure_infinispan_props
}


# see https://quarkus.io/guides/infinispan-client-guide#quarkus-infinispan-client_configuration
function configure_infinispan_props() {
    local infinispan_props=""

    if [[ "${INFINISPAN_USEAUTH}" == "true" ]] && [[ -z "${INFINISPAN_USERNAME}"  || -z "${INFINISPAN_PASSWORD}" ]]; then
        log_error "Flag INFINISPAN_USEAUTH set to true, but no username or password informed. Please use INFINISPAN_USERNAME and INFINISPAN_PASSWORD variables to set the right credentials."
        exit 1
    fi

    # default to false if empty or any value different than true or false.
    if  [ -z "${INFINISPAN_USEAUTH}" ] || [[ ! ${INFINISPAN_USEAUTH^^} =~ FALSE$|TRUE$ ]]; then
        INFINISPAN_USEAUTH="false"
    fi

    if [ ! -z "${INFINISPAN_USERNAME}" ]; then infinispan_props=$(echo "${infinispan_props} -Dquarkus.infinispan-client.auth-username=${INFINISPAN_USERNAME}"); INFINISPAN_USEAUTH="true"; fi
    if [ ! -z "${INFINISPAN_PASSWORD}" ]; then infinispan_props=$(echo "${infinispan_props} -Dquarkus.infinispan-client.auth-password=${INFINISPAN_PASSWORD}"); fi
    if [ ! -z "${INFINISPAN_USEAUTH}" ]; then infinispan_props=$(echo "${infinispan_props} -Dquarkus.infinispan-client.use-auth=${INFINISPAN_USEAUTH}"); fi
    if [ ! -z "${INFINISPAN_AUTHREALM}" ]; then infinispan_props=$(echo "${infinispan_props} -Dquarkus.infinispan-client.auth-realm=${INFINISPAN_AUTHREALM}"); fi
    if [ ! -z "${INFINISPAN_SASLMECHANISM}" ]; then infinispan_props=$(echo "${infinispan_props} -Dquarkus.infinispan-client.sasl-mechanism=${INFINISPAN_SASLMECHANISM}"); fi

    INFINISPAN_PROPERTIES="${infinispan_props}"
}

