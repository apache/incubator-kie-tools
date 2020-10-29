#!/usr/bin/env bats

export KOGITO_HOME=/tmp/kogito
export HOME=$KOGITO_HOME
mkdir -p ${KOGITO_HOME}/launch
cp $BATS_TEST_DIRNAME/../../../kogito-logging/added/logging.sh ${KOGITO_HOME}/launch/

# imports
load $BATS_TEST_DIRNAME/../../added/launch/kogito-trusty-ui.sh

teardown() {
    rm -rf ${KOGITO_HOME}
}

@test "test if the default value for trusty url will be set" {
    local expected=" -Dkogito.trusty.http.url=http://localhost:8180"
    configure_trusty_url
    echo "Result is [${KOGITO_TRUSTY_UI_PROPS}] and expected is [${expected}]" >&2
    [ "${expected}" = "${KOGITO_TRUSTY_UI_PROPS}" ]
}

@test "test if KOGITO_TRUSTY_URL will be correctly set " {
    export KOGITO_TRUSTY_ENDPOINT="http://10.10.10.10:8080"
    local expected=" -Dkogito.trusty.http.url=http://10.10.10.10:8080"
    configure_trusty_url
    echo "Result is [${KOGITO_TRUSTY_UI_PROPS}] and expected is [${expected}]" >&2
    [ "${expected}" = "${KOGITO_TRUSTY_UI_PROPS}" ]
}

@test "test if a invalid value for trusty url will return the expected exit code" {
    export KOGITO_TRUSTY_ENDPOINT="a.b.c"
    run configure_trusty_url
    [ "${status}" == "10" ]
}


