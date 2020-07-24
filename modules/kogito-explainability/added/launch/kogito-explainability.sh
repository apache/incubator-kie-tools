#!/usr/bin/env bash


function prepareEnv() {
    # keep it on alphabetical order
    unset HTTP_PORT
}

function configure() {
    configure_explainability_http_port
}

function configure_explainability_http_port {
    local httpPort=${HTTP_PORT:-8080}
    KOGITO_EXPLAINABILITY_PROPS="${KOGITO_EXPLAINABILITY_PROPS} -Dquarkus.http.port=${httpPort}"
}

