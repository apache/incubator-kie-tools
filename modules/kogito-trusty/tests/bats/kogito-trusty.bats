#!/usr/bin/env bats

export KOGITO_HOME=/tmp/kogito
export HOME=$KOGITO_HOME
mkdir -p ${KOGITO_HOME}/launch
cp $BATS_TEST_DIRNAME/../../../kogito-logging/added/logging.sh ${KOGITO_HOME}/launch/

# imports
load $BATS_TEST_DIRNAME/../../added/launch/kogito-trusty.sh

teardown() {
    rm -rf ${KOGITO_HOME}
}

@test "http port configuration default value" {
    configure_trusty_http_port
    expected=" -Dquarkus.http.port=8080"
    echo "Result is ${KOGITO_TRUSTY_PROPS} and expected is ${expected}"
    [ "${KOGITO_TRUSTY_PROPS}" = "${expected}" ]
}

@test "http port configuration custom value" {
    export HTTP_PORT="9090"
    configure_trusty_http_port
    expected=" -Dquarkus.http.port=9090"
    echo "Result is ${KOGITO_TRUSTY_PROPS} and expected is ${expected}"
    [ "${KOGITO_TRUSTY_PROPS}" = "${expected}" ]
}


@test "explainability is enabled by default" {
    enable_explainability
    expected=" -Dtrusty.explainability.enabled=true"
    echo "Result is ${KOGITO_TRUSTY_PROPS} and expected is ${expected}"
    [ "${KOGITO_TRUSTY_PROPS}" = "${expected}" ]
}

@test "disable explainability" {
    export EXPLAINABILITY_ENABLED="false" 
    enable_explainability
    expected=" -Dtrusty.explainability.enabled=false"
    echo "Result is ${KOGITO_TRUSTY_PROPS} and expected is ${expected}"
    [ "${KOGITO_TRUSTY_PROPS}" = "${expected}" ]
}

@test "explainability is enabled even if nonsense values are provided" {
    EXPLAINABILITY_ENABLED="nonsense"
    enable_explainability
    expected=" -Dtrusty.explainability.enabled=true"
    echo "result: ${KOGITO_TRUSTY_PROPS} \n expected: ${expected}"
    [ "${KOGITO_TRUSTY_PROPS}" = "${expected}" ]
}