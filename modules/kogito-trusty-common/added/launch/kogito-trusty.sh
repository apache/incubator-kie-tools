#!/usr/bin/env bash

#import
source "${KOGITO_HOME}"/launch/logging.sh

function prepareEnv() {
    # keep it on alphabetical order
    unset EXPLAINABILITY_ENABLED
}

function configure() {
    enable_explainability
}


function enable_explainability {
    local allowed_values=("TRUE" "FALSE")
    local explainability_enabled="true"
    # shellcheck disable=SC2153
    if [[ ! "${allowed_values[*]}" =~ ${EXPLAINABILITY_ENABLED^^} ]]; then
        log_warning "Explainability enabled type ${EXPLAINABILITY_ENABLED} is not allowed, the allowed types are [${allowed_values[*]}]. Defaulting to ${explainability_enabled}."
    elif [ "${EXPLAINABILITY_ENABLED^^}" == "FALSE" ]; then
        explainability_enabled="${EXPLAINABILITY_ENABLED^^}"
    fi
    log_info "Explainability is enabled: ${explainability_enabled}"
    KOGITO_TRUSTY_PROPS="${KOGITO_TRUSTY_PROPS} -Dtrusty.explainability.enabled=${explainability_enabled,,}"
}
