#!/usr/bin/env bats

export KOGITO_HOME=/tmp/kogito
export HOME=$KOGITO_HOME
mkdir -p ${KOGITO_HOME}/launch
cp $BATS_TEST_DIRNAME/../../../kogito-logging/added/logging.sh ${KOGITO_HOME}/launch/

# imports
load $BATS_TEST_DIRNAME/../../added/launch/kogito-explainability.sh

teardown() {
    rm -rf ${KOGITO_HOME}
}


@test "http port configuration default value" {
    configure_explainability_http_port
    expected=" -Dquarkus.http.port=8080"
    echo "Result is ${KOGITO_EXPLAINABILITY_PROPS} and expected is ${expected}"
    [ "${KOGITO_EXPLAINABILITY_PROPS}" = "${expected}" ]
}

@test "http port configuration custom value" {
    export HTTP_PORT="9090"
    configure_explainability_http_port
    expected=" -Dquarkus.http.port=9090"
    echo "Result is ${KOGITO_EXPLAINABILITY_PROPS} and expected is ${expected}"
    [ "${KOGITO_EXPLAINABILITY_PROPS}" = "${expected}" ]
}

@test "test if the default explainability communication type is correctly set" {
    configure_explainability_jar
    expected="kogito-explainability-messaging-runner.jar"
    echo "result: ${EXPLAINABILITY_SERVICE_JAR} \n expected: ${expected}"
    [ "${EXPLAINABILITY_SERVICE_JAR}" = "${expected}" ]
}

@test "test if explainability communication service default value is correctly set if a nonsense type is set" {
    EXPLAINABILITY_COMMUNICATION="nonsense"
    configure_explainability_jar
    expected="kogito-explainability-messaging-runner.jar"
    echo "result: ${EXPLAINABILITY_SERVICE_JAR} \n expected: ${expected}"
    [ "${EXPLAINABILITY_SERVICE_JAR}" = "${expected}" ]
}

@test "test if explainability communication service default value s correctly set if set to rest" {
    EXPLAINABILITY_COMMUNICATION="rest"
    configure_explainability_jar
    expected="kogito-explainability-rest-runner.jar"
    echo "result: ${EXPLAINABILITY_SERVICE_JAR} \n expected: ${expected}"
    [ "${EXPLAINABILITY_SERVICE_JAR}" = "${expected}" ]
}