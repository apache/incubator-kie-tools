#!/usr/bin/env bash

function prepareEnv() {
    # keep it on alphabetical order
    unset ENABLE_PERSISTENCE
    unset HTTP_PORT
}

function configure() {
    configure_jobs_service
    configure_jobs_service_http_port
}


function configure_jobs_service() {
    if [ "${ENABLE_PERSISTENCE^^}" == "TRUE" ]; then
       KOGITO_JOBS_PROPS="${KOGITO_JOBS_PROPS} -Dkogito.jobs-service.persistence=infinispan"
    fi

    if [ "${ENABLE_EVENTS^^}" == "TRUE" ]; then
        KOGITO_JOBS_PROPS="${KOGITO_JOBS_PROPS} -Dquarkus.profile=events-support"
    fi
}

function configure_jobs_service_http_port {
    local httpPort=${HTTP_PORT:-8080}
    KOGITO_JOBS_PROPS="${KOGITO_JOBS_PROPS} -Dquarkus.http.port=${httpPort}"
}
