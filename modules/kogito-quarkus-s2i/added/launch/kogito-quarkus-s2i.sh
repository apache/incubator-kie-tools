#!/usr/bin/env bash


function prepareEnv() {
    # keep it on alphabetical order
    unset HTTP_PORT
}

function configure() {
    configure_quarkus_s2i_http_port
}

function configure_quarkus_s2i_http_port {
    local httpPort=${HTTP_PORT:-8080}
    KOGITO_QUARKUS_S2I_PROPS="${KOGITO_QUARKUS_S2I_PROPS} -Dquarkus.http.port=${httpPort}"
}