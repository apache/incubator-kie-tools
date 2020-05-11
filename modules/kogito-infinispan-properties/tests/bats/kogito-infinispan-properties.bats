#!/usr/bin/env bats

export TEST=true
export KOGITO_HOME=/tmp/kogito
mkdir -p ${KOGITO_HOME}/launch
cp $BATS_TEST_DIRNAME/../../../kogito-logging/added/logging.sh ${KOGITO_HOME}/launch/

# import
load $BATS_TEST_DIRNAME/../../added/kogito-infinispan-properties.sh

function clear_vars() {
    unset INFINISPAN_USEAUTH
    unset INFINISPAN_USERNAME
    unset INFINISPAN_PASSWORD
    unset INFINISPAN_AUTHREALM
    unset INFINISPAN_SASLMECHANISM
}

@test "check if infinispan properties is blank" {
    clear_vars
    local expected=" -Dquarkus.infinispan-client.use-auth=false"
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
    # exit
    echo "Status: ${status}"
    [ "$status" -eq 1 ]
}

