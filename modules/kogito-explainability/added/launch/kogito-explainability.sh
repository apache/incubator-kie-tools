#!/usr/bin/env bash

#import
source "${KOGITO_HOME}"/launch/logging.sh

function prepareEnv() {
    # keep it on alphabetical order
    unset EXPLAINABILITY_COMMUNICATION
}

function configure() {
    configure_explainability_jar
}

function configure_explainability_jar {
    local allowed_communication_types=("REST" "MESSAGING")
    local communication="MESSAGING"
    if [[ ! "${allowed_communication_types[*]}" =~ ${EXPLAINABILITY_COMMUNICATION^^} ]]; then
        log_warning "Explainability communication type ${EXPLAINABILITY_COMMUNICATION} is not allowed, the allowed types are [${allowed_communication_types[*]}]. Defaulting to ${communication}."
    elif [ "${EXPLAINABILITY_COMMUNICATION^^}" == "REST" ]; then
        communication="${EXPLAINABILITY_COMMUNICATION^^}"
    fi

    log_info "Explainability communication is set to ${communication}"
    EXPLAINABILITY_SERVICE_JAR="kogito-explainability-${communication,,}-runner.jar"
}
