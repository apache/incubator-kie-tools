#!/usr/bin/env bash

function prepareEnv() {
    # keep it on alphabetical order
    unset BACKOFF_RETRY
    unset ENABLE_PERSISTENCE
    unset HTTP_PORT
    unset MAX_INTERVAL_LIMIT_RETRY
}

function configure() {
    configure_jobs_service
    configure_jobs_service_http_port
}


function configure_jobs_service() {
    if [ "${ENABLE_PERSISTENCE^^}" == "TRUE" ]; then
       KOGITO_JOBS_PROPS="${KOGITO_JOBS_PROPS} -Dkogito.jobs-service.persistence=infinispan"
    fi

    if [ "${BACKOFF_RETRY}x" != "x" ]; then
        KOGITO_JOBS_PROPS="${KOGITO_JOBS_PROPS} -Dkogito.jobs-service.backoffRetryMillis=${BACKOFF_RETRY}"
    fi

    if [ "${MAX_INTERVAL_LIMIT_RETRY}x" != "x" ]; then
        KOGITO_JOBS_PROPS="${KOGITO_JOBS_PROPS} -Dkogito.jobs-service.maxIntervalLimitToRetryMillis=${MAX_INTERVAL_LIMIT_RETRY}"
    fi

    if [ "${ENABLE_EVENTS^^}" == "TRUE" ]; then
        KOGITO_JOBS_PROPS="${KOGITO_JOBS_PROPS} -Dquarkus.profile=events-support"
    fi
}

function configure_jobs_service_http_port {
    local httpPort=${HTTP_PORT:-8080}
    KOGITO_JOBS_PROPS="${KOGITO_JOBS_PROPS} -Dquarkus.http.port=${httpPort}"
}

