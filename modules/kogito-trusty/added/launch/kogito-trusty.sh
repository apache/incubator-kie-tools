#!/usr/bin/env bash


function prepareEnv() {
    # keep it on alphabetical order
    unset HTTP_PORT
}

function configure() {
    configure_trusty_http_port
}

function configure_trusty_http_port {
    local httpPort=${HTTP_PORT:-8080}
    KOGITO_TRUSTY_PROPS="${KOGITO_TRUSTY_PROPS} -Dquarkus.http.port=${httpPort}"
}

