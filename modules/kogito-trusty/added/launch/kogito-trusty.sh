#!/usr/bin/env bash

#import
source ${KOGITO_HOME}/launch/logging.sh

function prepareEnv() {
    # keep it on alphabetical order
    unset EXPLAINABILITY_ENABLED
    unset HTTP_PORT
}

function configure() {
    configure_trusty_http_port
    enable_explainability
}

function configure_trusty_http_port {
    local httpPort=${HTTP_PORT:-8080}
    KOGITO_TRUSTY_PROPS="${KOGITO_TRUSTY_PROPS} -Dquarkus.http.port=${httpPort}"
}

function enable_explainability {
    local allowed_values=("TRUE" "FALSE")
    local explainability_enabled="true"
    if [[ ! "${allowed_values[@]}" =~ "${EXPLAINABILITY_ENABLED^^}" ]]; then
        log_warning "Explainability enabled type ${EXPLAINABILITY_ENABLED} is not allowed, the allowed types are [${allowed_values[*]}]. Defaulting to ${explainability_enabled}."
    elif [ "${EXPLAINABILITY_ENABLED^^}" == "FALSE" ]; then
        explainability_enabled="${EXPLAINABILITY_ENABLED^^}"
    fi
    log_info "Explainability is enabled: ${explainability_enabled}"
    KOGITO_TRUSTY_PROPS="${KOGITO_TRUSTY_PROPS} -Dtrusty.explainability.enabled=${explainability_enabled,,}"
}
