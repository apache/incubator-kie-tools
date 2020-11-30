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

@test "check if the persistence is correctly configured with auth" {
    export ENABLE_PERSISTENCE="true"
    configure_jobs_service

    result="${KOGITO_JOBS_PROPS}"
    expected=" -Dkogito.jobs-service.persistence=infinispan"

    echo "Result is ${result} and expected is ${expected}"
    [ "${result}" = "${expected}" ]
}

@test "check if the event is correctly set" {
    export ENABLE_EVENTS="true"
    configure_jobs_service

    result="${KOGITO_JOBS_PROPS}"
    expected=" -Dquarkus.profile=events-support"

    echo "Result is ${result} and expected is ${expected}"
    [ "${result}" = "${expected}" ]
}


