#!/usr/bin/env bats

export KOGITO_HOME=/tmp/kogito
export HOME="${KOGITO_HOME}"
mkdir -p "${KOGITO_HOME}"/launch
cp $BATS_TEST_DIRNAME/../../../kogito-logging/added/logging.sh "${KOGITO_HOME}"/launch/

# imports
load $BATS_TEST_DIRNAME/../../added/launch/kogito-jobs-service.sh


teardown() {
    rm -rf "${KOGITO_HOME}"
}

@test "check default jobs service" {
    configure_jobs_service

    result="${JOBS_SERVICE_JAR}"
    expected="jobs-service-common-runner.jar"

    echo "Result is ${result} and expected is ${expected}"
    [ "${result}" = "${expected}" ]
}

@test "check if the persistence is correctly configured on jobs service" {
    export ENABLE_PERSISTENCE="true"
    configure_jobs_service

    result="${JOBS_SERVICE_JAR}"
    expected="jobs-service-infinispan-runner.jar"

    echo "Result is ${result} and expected is ${expected}"
    [ "${result}" = "${expected}" ]
}

@test "check if the event is correctly set on jobs service" {
    export ENABLE_EVENTS="true"
    configure_jobs_service

    result="${KOGITO_JOBS_PROPS}"
    expected=" -Dquarkus.profile=events-support"

    echo "Result is ${result} and expected is ${expected}"
    [ "${result}" = "${expected}" ]
}


