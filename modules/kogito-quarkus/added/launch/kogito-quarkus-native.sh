#!/usr/bin/env bash


function prepareEnv() {
    # keep it on alphabetical order
    unset HTTP_PORT
}

function configure() {
    configure_quarkus_native_http_port
}

function configure_quarkus_native_http_port {
    local httpPort=${HTTP_PORT:-8080}
    KOGITO_QUARKUS_NATIVE_PROPS="${KOGITO_QUARKUS_NATIVE_PROPS} -Dquarkus.http.port=${httpPort}"
}