#!/usr/bin/env bash

#import
source ${KOGITO_HOME}/launch/logging.sh

function prepareEnv() {
    # keep it on alphabetical order
    unset HTTP_PORT
}

function configure() {
    configure_data_index_http_port
}

function configure_data_index_http_port() {
    local httpPort=${HTTP_PORT:-8080}
    KOGITO_DATA_INDEX_PROPS="${KOGITO_DATA_INDEX_PROPS} -Dquarkus.http.port=${httpPort}"
}

