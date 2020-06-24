#!/usr/bin/env bats

# imports
load $BATS_TEST_DIRNAME/../../added/launch/kogito-quarkus-s2i.sh

@test "check if custom http port is correctly set" {
  export HTTP_PORT="9090"

  configure_quarkus_s2i_http_port

  result="${KOGITO_QUARKUS_S2I_PROPS}"
  expected=" -Dquarkus.http.port=9090"

  echo "Result is ${result} and expected is ${expected}"
    [ "${result}" = "${expected}" ]
}