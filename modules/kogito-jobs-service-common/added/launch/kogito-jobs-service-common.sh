#!/usr/bin/env bash

source "${KOGITO_HOME}"/launch/logging.sh

function prepareEnv() {
    # keep it on alphabetical order
    unset ENABLE_EVENTS
}

function configure() {
    configure_jobs_service_events
}

function configure_jobs_service_events() {
    if [ "${ENABLE_EVENTS^^}" == "TRUE" ]; then
        KOGITO_JOBS_PROPS="${KOGITO_JOBS_PROPS} -Dquarkus.profile=events-support"
    fi
}