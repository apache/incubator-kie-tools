#!/usr/bin/env bash

function prepareEnv() {
    # keep it on alphabetical order
    unset ENABLE_PERSISTENCE
}

function configure() {
    configure_jobs_service
}


function configure_jobs_service() {
    if [ "${ENABLE_PERSISTENCE^^}" == "TRUE" ]; then
       KOGITO_JOBS_PROPS="${KOGITO_JOBS_PROPS} -Dkogito.jobs-service.persistence=infinispan"
    fi

    if [ "${ENABLE_EVENTS^^}" == "TRUE" ]; then
        KOGITO_JOBS_PROPS="${KOGITO_JOBS_PROPS} -Dquarkus.profile=events-support"
    fi
}
