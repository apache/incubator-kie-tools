#!/usr/bin/env bash

# keep it here for now
# TODO refactor data-index image to reuse it.
source ${KOGITO_HOME}/launch/kogito-infinispan-properties.sh

function prepareEnv() {
    # keep it on alphabetical order
    unset BACKOFF_RETRY
    unset ENABLE_PERSISTENCE
    unset INFINISPAN_CLIENT_SERVER_LIST
    unset MAX_INTERVAL_LIMIT_RETRY
}

function configure() {
    configure_jobs_service
}


function configure_jobs_service() {
    if [ "${ENABLE_PERSISTENCE^^}" == "TRUE" ]; then

        if [ "${INFINISPAN_CLIENT_SERVER_LIST}x" = "x" ]; then
            echo "INFINISPAN_CLIENT_SERVER_LIST env not found, please set it."
            exit 1
        else
            infinispanProps=$(set_infinispan_props)
            KOGITO_JOBS_PROPS="${KOGITO_JOBS_PROPS} -Dkogito.jobs-service.persistence=infinispan ${infinispanProps}"
            KOGITO_JOBS_PROPS="${KOGITO_JOBS_PROPS} -Dquarkus.infinispan-client.server-list=${INFINISPAN_CLIENT_SERVER_LIST}"
        fi
    fi

    if [ "${BACKOFF_RETRY}x" != "x" ]; then
        KOGITO_JOBS_PROPS="${KOGITO_JOBS_PROPS} -Dkogito.jobs-service.backoffRetryMillis=${BACKOFF_RETRY}"
    fi

    if [ "${MAX_INTERVAL_LIMIT_RETRY}x" != "x" ]; then
        KOGITO_JOBS_PROPS="${KOGITO_JOBS_PROPS} -Dkogito.jobs-service.maxIntervalLimitToRetryMillis=${MAX_INTERVAL_LIMIT_RETRY}"
    fi

}

