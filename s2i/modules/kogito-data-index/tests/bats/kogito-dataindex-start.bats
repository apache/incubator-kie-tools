#!/usr/bin/env bats

export TEST=true

# import
load $BATS_TEST_DIRNAME/../../added/start

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
    run set_infinispan_props

    echo "Result is ${output} and expected is ${expected}" >&2
    [ "${expected}" = "${output}" ]
}


@test "check if infinispan auth is false" {
    clear_vars
    export INFINISPAN_USEAUTH="false"
    local expected="-Dquarkus.infinispan-client.use-auth=false"
    run set_infinispan_props

    echo "Result is ${output} and expected is ${expected}" >&2
    [ "${expected}" = "${output}" ]
}

@test "check if infinispan has auth props" {
    clear_vars
    export INFINISPAN_USERNAME="developer"
    export INFINISPAN_USEAUTH="true"
    export INFINISPAN_PASSWORD="developer"
    export INFINISPAN_AUTHREALM="default"
    export INFINISPAN_SASLMECHANISM="PLAIN"

    local expected="-Dquarkus.infinispan-client.auth-username=developer -Dquarkus.infinispan-client.auth-password=developer -Dquarkus.infinispan-client.use-auth=true -Dquarkus.infinispan-client.auth-realm=default -Dquarkus.infinispan-client.sasl-mechanism=PLAIN"
    run set_infinispan_props

    echo "Result is ${output} and expected is ${expected}" >&2
    [ "${expected}" = "${output}" ]
}

@test "setting username, useauth is true" {
    clear_vars
    export INFINISPAN_USERNAME="developer"
    export INFINISPAN_USEAUTH="false"
    local expected="-Dquarkus.infinispan-client.auth-username=developer -Dquarkus.infinispan-client.use-auth=true"

    run set_infinispan_props

    echo "Result is ${output} and expected is ${expected}" >&2
    [ "${expected}" = "${output}" ]
}

@test "when use auth is set to nonsense and no credentials" {
    clear_vars
    export INFINISPAN_USEAUTH="dsadsadasdsa"
    local expected="-Dquarkus.infinispan-client.use-auth=false"

    run set_infinispan_props

    echo "Result is ${output} and expected is ${expected}" >&2
    [ "${expected}" = "${output}" ]
}

@test "when use auth is set to nonsense and has credentials" {
    clear_vars
    export INFINISPAN_USEAUTH="dsadsadasdsa"
    export INFINISPAN_USERNAME="developer"
    local expected="-Dquarkus.infinispan-client.auth-username=developer -Dquarkus.infinispan-client.use-auth=true"

    run set_infinispan_props

    echo "Result is ${output} and expected is ${expected}" >&2
    [ "${expected}" = "${output}" ]
}

@test "when use auth is set to true and no credentials" {
    clear_vars
    export INFINISPAN_USEAUTH="true"
    local expected="-Dquarkus.infinispan-client.auth-username=developer -Dquarkus.infinispan-client.use-auth=true"

    run set_infinispan_props
    # exit(1)
    [ "$status" -eq 1 ]
}