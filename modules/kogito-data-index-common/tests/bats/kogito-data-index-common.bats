#!/usr/bin/env bats

export KOGITO_HOME=/tmp/kogito
export HOME="${KOGITO_HOME}"
mkdir -p "${KOGITO_HOME}"/launch
cp $BATS_TEST_DIRNAME/../../../kogito-logging/added/logging.sh "${KOGITO_HOME}"/launch/

# imports
load $BATS_TEST_DIRNAME/../../added/launch/kogito-data-index-common.sh


teardown() {
    rm -rf "${KOGITO_HOME}"
}

@test "check if the default quarkus profile is correctly set on data index" {
    local expected=" -Dquarkus.profile=kafka-events-support"
    configure_data_index_quarkus_profile
    echo "Result is [${KOGITO_DATA_INDEX_PROPS}] and expected is [${expected}]"
    [ "${expected}" = "${KOGITO_DATA_INDEX_PROPS}" ]
}

@test "check if a provided data index quarkus profile is correctly set on data index" {
    export KOGITO_DATA_INDEX_QUARKUS_PROFILE="http-events-support"
    local expected=" -Dquarkus.profile=http-events-support"
    configure_data_index_quarkus_profile
    echo "Result is [${KOGITO_DATA_INDEX_PROPS}] and expected is [${expected}]"
    [ "${expected}" = "${KOGITO_DATA_INDEX_PROPS}" ]
}

