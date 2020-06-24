#!/usr/bin/env bats

export KOGITO_HOME=/tmp/kogito
export HOME=$KOGITO_HOME
mkdir -p ${KOGITO_HOME}/launch
cp $BATS_TEST_DIRNAME/../../../kogito-infinispan-properties/added/kogito-infinispan-properties.sh ${KOGITO_HOME}/launch/
cp $BATS_TEST_DIRNAME/../../../kogito-logging/added/logging.sh ${KOGITO_HOME}/launch/

# imports
load ${KOGITO_HOME}/launch/kogito-infinispan-properties.sh
load $BATS_TEST_DIRNAME/../../added/launch/kogito-data-index.sh

teardown() {
    rm -rf ${KOGITO_HOME}
}


@test "http port configuration default value" {
    configure_data_index_http_port
    expected=" -Dquarkus.http.port=8080"
    echo "Result is ${KOGITO_DATA_INDEX_PROPS} and expected is ${expected}"
    [ "${KOGITO_DATA_INDEX_PROPS}" = "${expected}" ]
}

@test "http port configuration custom value" {
    export HTTP_PORT="9090"
    configure_data_index_http_port
    expected=" -Dquarkus.http.port=9090"
    echo "Result is ${KOGITO_DATA_INDEX_PROPS} and expected is ${expected}"
    [ "${KOGITO_DATA_INDEX_PROPS}" = "${expected}" ]
}

