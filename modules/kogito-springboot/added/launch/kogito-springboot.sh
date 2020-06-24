#!/usr/bin/env bash


function prepareEnv() {
    # keep it on alphabetical order
    unset HTTP_PORT
}

function configure() {
    configure_springboot_http_port
}

function configure_springboot_http_port {
    local httpPort=${HTTP_PORT:-8080}
    KOGITO_SPRINGBOOT_PROPS="${KOGITO_SPRINGBOOT_PROPS} -Dserver.port=${httpPort}"
}