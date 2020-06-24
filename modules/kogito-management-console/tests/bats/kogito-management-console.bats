#!/usr/bin/env bats

export KOGITO_HOME=/tmp/kogito
export HOME=$KOGITO_HOME
mkdir -p ${KOGITO_HOME}/launch
cp $BATS_TEST_DIRNAME/../../../kogito-logging/added/logging.sh ${KOGITO_HOME}/launch/

# imports
load $BATS_TEST_DIRNAME/../../added/launch/kogito-management-console.sh

teardown() {
    rm -rf ${KOGITO_HOME}
}

@test "test if the default value for data-index url will be set" {
    local expected=" -Dkogito.dataindex.http.url=http://localhost:8180"
    configure_data_index_url
    echo "Result is [${KOGITO_MANAGEMENT_CONSOLE_PROPS}] and expected is [${expected}]" >&2
    [ "${expected}" = "${KOGITO_MANAGEMENT_CONSOLE_PROPS}" ]
}

@test "test if KOGITO_DATA_INDEX_URL will be correctly set " {
    export KOGITO_DATAINDEX_HTTP_URL="http://10.10.10.10:8080"
    local expected=" -Dkogito.dataindex.http.url=http://10.10.10.10:8080"
    configure_data_index_url
    echo "Result is [${KOGITO_MANAGEMENT_CONSOLE_PROPS}] and expected is [${expected}]" >&2
    [ "${expected}" = "${KOGITO_MANAGEMENT_CONSOLE_PROPS}" ]
}

@test "test if a invalid value for data-index url will return the expected exit code" {
    export KOGITO_DATAINDEX_HTTP_URL="a.b.c"
    run configure_data_index_url
    [ "${status}" == "10" ]
}

@test "check if default http port is correctly set" {

  configure_mgmt_console_http_port

  result="${KOGITO_MANAGEMENT_CONSOLE_PROPS}"
  expected=" -Dquarkus.http.port=8080"

  echo "Result is ${result} and expected is ${expected}"
    [ "${result}" = "${expected}" ]
}

@test "check if custom http port is correctly set" {
  export HTTP_PORT="9090"

  configure_mgmt_console_http_port

  result="${KOGITO_MANAGEMENT_CONSOLE_PROPS}"
  expected=" -Dquarkus.http.port=9090"

  echo "Result is ${result} and expected is ${expected}"
    [ "${result}" = "${expected}" ]
}

