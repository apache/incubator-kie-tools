#!/usr/bin/env bash

function prepareEnv() {
    # keep it on alphabetical order
    unset ENABLE_PERSISTENCE
}

function configure() {
    configure_jobs_service
}


function configure_jobs_service() {
    local persistence='common'
    if [ "${ENABLE_PERSISTENCE^^}" == "TRUE" ]; then
        persistence='infinispan'
    fi

    if [ "${ENABLE_EVENTS^^}" == "TRUE" ]; then
        KOGITO_JOBS_PROPS="${KOGITO_JOBS_PROPS} -Dquarkus.profile=events-support"
    fi

    JOBS_SERVICE_JAR="jobs-service-${persistence}-runner.jar"
}
