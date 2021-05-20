#!/usr/bin/env bats

export KOGITO_HOME=/tmp/kogito
export HOME="${KOGITO_HOME}"
mkdir -p "${KOGITO_HOME}"/launch
cp $BATS_TEST_DIRNAME/../../../kogito-logging/added/logging.sh "${KOGITO_HOME}"/launch/

# imports
load $BATS_TEST_DIRNAME/../../added/launch/kogito-jobs-service-common.sh


teardown() {
    rm -rf "${KOGITO_HOME}"
}

@test "check if the event is correctly set on jobs service" {
    export ENABLE_EVENTS="true"
    configure_jobs_service_events

    result="${KOGITO_JOBS_PROPS}"
    expected=" -Dquarkus.profile=events-support"

    echo "Result is ${result} and expected is ${expected}"
    [ "${result}" = "${expected}" ]
}


