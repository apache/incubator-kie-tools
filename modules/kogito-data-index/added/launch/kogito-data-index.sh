#!/usr/bin/env bash


function prepareEnv() {
    # keep it on alphabetical order
    unset KOGITO_DATA_INDEX_HTTP_PORT
}

function configure() {
    configure_data_index_http_port
}

function configure_data_index_http_port {
    local httpPort=${KOGITO_DATA_INDEX_HTTP_PORT:-8080}
    KOGITO_DATA_INDEX_PROPS="${KOGITO_DATA_INDEX_PROPS} -Dquarkus.http.port=${httpPort}"
}

