#!/usr/bin/env bats

export KOGITO_HOME=/tmp/kogito
export HOME=$KOGITO_HOME
mkdir -p ${KOGITO_HOME}/launch
cp $BATS_TEST_DIRNAME/../../../kogito-infinispan-properties/added/kogito-infinispan-properties.sh ${KOGITO_HOME}/launch/

load ${KOGITO_HOME}/launch/kogito-infinispan-properties.sh

teardown() {
    rm -rf ${KOGITO_HOME}
}

function clear_vars() {
    unset INFINISPAN_USEAUTH
    unset INFINISPAN_USERNAME
    unset INFINISPAN_PASSWORD
    unset INFINISPAN_AUTHREALM
    unset INFINISPAN_SASLMECHANISM
}

@test "check if infinispan properties is blank" {
    clear_vars
    local expected=""
    configure_infinispan_props
    echo "Result is ${INFINISPAN_PROPERTIES} and expected is ${expected}" >&2
    [ "${expected}" = "${INFINISPAN_PROPERTIES}" ]
}


@test "check if infinispan auth is false" {
    clear_vars
    export INFINISPAN_USEAUTH="false"
    local expected=" -Dquarkus.infinispan-client.use-auth=false"
    configure_infinispan_props
    echo "Result is ${INFINISPAN_PROPERTIES} and expected is ${expected}" >&2
    [ "${expected}" = "${INFINISPAN_PROPERTIES}" ]
}

@test "check if infinispan has auth props" {
    clear_vars
    export INFINISPAN_USERNAME="developer"
    export INFINISPAN_USEAUTH="true"
    export INFINISPAN_PASSWORD="developer"
    export INFINISPAN_AUTHREALM="default"
    export INFINISPAN_SASLMECHANISM="PLAIN"

    local expected=" -Dquarkus.infinispan-client.auth-username=developer -Dquarkus.infinispan-client.auth-password=developer -Dquarkus.infinispan-client.use-auth=true -Dquarkus.infinispan-client.auth-realm=default -Dquarkus.infinispan-client.sasl-mechanism=PLAIN"
    configure_infinispan_props

    echo "Result is ${INFINISPAN_PROPERTIES} and expected is ${expected}" >&2
    [ "${expected}" = "${INFINISPAN_PROPERTIES}" ]
}

@test "setting username, useauth is true" {
    clear_vars
    export INFINISPAN_USERNAME="developer"
    export INFINISPAN_USEAUTH="false"
    local expected=" -Dquarkus.infinispan-client.auth-username=developer -Dquarkus.infinispan-client.use-auth=true"

    configure_infinispan_props

    echo "Result is ${INFINISPAN_PROPERTIES} and expected is ${expected}" >&2
    [ "${expected}" = "${INFINISPAN_PROPERTIES}" ]
}

@test "when use auth is set to nonsense and no credentials" {
    clear_vars
    export INFINISPAN_USEAUTH="dsadsadasdsa"
    local expected=" -Dquarkus.infinispan-client.use-auth=false"

    configure_infinispan_props

    echo "Result is ${INFINISPAN_PROPERTIES} and expected is ${expected}" >&2
    [ "${expected}" = "${INFINISPAN_PROPERTIES}" ]
}

@test "when use auth is set to nonsense and has credentials" {
    clear_vars
    export INFINISPAN_USEAUTH="dsadsadasdsa"
    export INFINISPAN_USERNAME="developer"
    local expected=" -Dquarkus.infinispan-client.auth-username=developer -Dquarkus.infinispan-client.use-auth=true"

    configure_infinispan_props

    echo "Result is ${INFINISPAN_PROPERTIES} and expected is ${expected}" >&2
    [ "${expected}" = "${INFINISPAN_PROPERTIES}" ]
}

@test "when use auth is set to true and no credentials" {
    clear_vars
    export INFINISPAN_USEAUTH="true"

    run configure_infinispan_props

    expected="[ERROR] Flag INFINISPAN_USEAUTH set to true, but no username or password informed. Please use INFINISPAN_USERNAME and INFINISPAN_PASSWORD variables to set the right credentials."
    echo "Result is ${output} and expected is ${expected}"
    echo "Expected status is 1, outcome status is ${status}"
    [ "$status" -eq 1 ]
    [ "${output}" = "${expected}" ]
}