#!/usr/bin/env bash

#import
source ${KOGITO_HOME}/launch/logging.sh

function prepareEnv() {
    # keep it on alphabetical order
    unset DATA_INDEX_PERSISTENCE
    unset HTTP_PORT
}

function configure() {
    configure_data_index_persistence
    configure_data_index_http_port
}

function configure_data_index_http_port() {
    local httpPort=${HTTP_PORT:-8080}
    KOGITO_DATA_INDEX_PROPS="${KOGITO_DATA_INDEX_PROPS} -Dquarkus.http.port=${httpPort}"
}

function configure_data_index_persistence() {
    local allowed_persistence_types=("INFINISPAN" "MONGODB")
    local persistence="INFINISPAN"
    if [[ ! "${allowed_persistence_types[@]}" =~ "${DATA_INDEX_PERSISTENCE^^}" ]]; then
        log_warning "Data index persistence type ${DATA_INDEX_PERSISTENCE} is not allowed, the allowed types are [${allowed_persistence_types[*]}]. Defaulting to ${persistence}."
    elif [ "${DATA_INDEX_PERSISTENCE^^}" == "MONGODB" ]; then
        persistence="${DATA_INDEX_PERSISTENCE^^}"
    fi
    log_info "Data index persistence is set to ${persistence}"
    KOGITO_DATA_INDEX_SERVICE_JAR="data-index-service-${persistence,,}.jar"
}

