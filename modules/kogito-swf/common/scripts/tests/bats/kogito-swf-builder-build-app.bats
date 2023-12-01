#!/usr/bin/env bats

setup() {
    export KOGITO_HOME=/tmp/kogito
    export HOME="${KOGITO_HOME}"
    mkdir -p "${KOGITO_HOME}"/launch
    mkdir -p "${KOGITO_HOME}"/serverless-workflow-project/src/main/resources/
    cp $BATS_TEST_DIRNAME/../../../../../kogito-logging/added/logging.sh "${KOGITO_HOME}"/launch/
    cp $BATS_TEST_DIRNAME/../../added/jvm-settings.sh "${KOGITO_HOME}"/launch/
    cp $BATS_TEST_DIRNAME/../../added/build-app.sh "${KOGITO_HOME}"/launch/
}

teardown() {
    rm -rf "${KOGITO_HOME}"
    rm -rf /tmp/resources
}

@test "verify copy resources is working" {
    TEMPD=$(mktemp -d)
    cp -r $BATS_TEST_DIRNAME/../../../../../../tests/shell/kogito-swf-builder/resources/greet-with-inputschema/* ${TEMPD}

    # We don't care about the errors to try to execute and build the program, just the copy matters
    source ${KOGITO_HOME}/launch/build-app.sh ${TEMPD} || true
    
    [[ -f "${KOGITO_HOME}"/serverless-workflow-project/src/main/resources/greet.sw.json ]]
    [[ -f "${KOGITO_HOME}"/serverless-workflow-project/src/main/resources/schemas/input.json ]]
}
