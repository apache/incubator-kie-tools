#!/usr/bin/env bash

source "${KOGITO_HOME}"/launch/logging.sh

function prepareEnv() {
    # keep it on alphabetical order
    unset KOGITO_DATA_INDEX_QUARKUS_PROFILE
}
function configure() {
    configure_data_index_quarkus_profile
}

function configure_data_index_quarkus_profile() {
    local quarkusProfile=${KOGITO_DATA_INDEX_QUARKUS_PROFILE}
    KOGITO_DATA_INDEX_PROPS="${KOGITO_DATA_INDEX_PROPS} -Dquarkus.profile=${quarkusProfile}"
}