#!/usr/bin/env bats

export KOGITO_HOME=/tmp/kogito
export HOME="${KOGITO_HOME}"
mkdir -p "${KOGITO_HOME}"/launch
cp $BATS_TEST_DIRNAME/../../../kogito-logging/added/logging.sh "${KOGITO_HOME}"/launch/

# imports
load $BATS_TEST_DIRNAME/../../added/launch/kogito-explainability.sh

teardown() {
    rm -rf "${KOGITO_HOME}"
}


@test "test if the default explainability communication type is correctly set" {
    configure_explainability_jar
    expected="messaging"
    echo "result: ${EXPLAINABILITY_SERVICE_JAR} \n expected: ${expected}"
    [ "${EXPLAINABILITY_SERVICE_COMMUNICATION}" = "${expected}" ]
}

@test "test if explainability communication service default value is correctly set if a nonsense type is set" {
    EXPLAINABILITY_COMMUNICATION="nonsense"
    configure_explainability_jar
    expected="messaging"
    echo "result: ${EXPLAINABILITY_SERVICE_JAR} \n expected: ${expected}"
    [ "${EXPLAINABILITY_SERVICE_COMMUNICATION}" = "${expected}" ]
}

@test "test if explainability communication service default value s correctly set if set to rest" {
    EXPLAINABILITY_COMMUNICATION="rest"
    configure_explainability_jar
    expected="rest"
    echo "result: ${EXPLAINABILITY_SERVICE_JAR} \n expected: ${expected}"
    [ "${EXPLAINABILITY_SERVICE_COMMUNICATION}" = "${expected}" ]
}