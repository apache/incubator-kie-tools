#!/usr/bin/env bats

# imports
source $BATS_TEST_DIRNAME/../../modules/graalvm/19.x/added/memory-limit.sh

@test "test a valid memory limit value" {
    export LIMIT_MEMORY="1073741824"
    local expected=" -Dnative-image.xmx=1073741824"
    configure
    echo "Expected: ${expected}"
    echo "Result: ${KOGITO_OPTS}"
    [ "${expected}" = "${KOGITO_OPTS}" ]
}


@test "test a small memory limit value" {
    export LIMIT_MEMORY="1073741600"
    local expected="Provided memory (1073741600) limit is too small, native build will use all available memory"
    local result=$(configure)
    echo "Expected: ${expected}"
    echo "Result: ${result}"
    [ "${expected}" = "${result}" ]
}


@test "test a invalid memory limit value" {
    export LIMIT_MEMORY="1024m"
    local expected="Provided memory (1024m) limit is not valid, native build will use all available memory"
    local result=$(configure)
    echo "Expected: ${expected}"
    echo "Result: ${result}"
    [ "${expected}" = "${result}" ]
}