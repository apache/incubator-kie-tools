#!/usr/bin/env bats
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#


export KOGITO_HOME=/tmp/kogito
export HOME="${KOGITO_HOME}"
mkdir -p "${KOGITO_HOME}"/launch
cp $BATS_TEST_DIRNAME/../../../kogito-logging/added/logging.sh "${KOGITO_HOME}"/launch/

load $BATS_TEST_DIRNAME/../../added/configure-custom-truststore.sh

teardown() {
    rm -rf "${KOGITO_HOME}"
}

@test "fail case when the custom certificate is not present in the expected path" {
    prepareEnv

    local expected=1
    export CUSTOM_TRUSTSTORE=my-cert.jks

    run configure

    echo "Result is [$status] and expected is [${expected}]" >&2
    [ "$status" = "${expected}" ]
    echo "Output is: ${lines[@]}"
    [[ "${lines[1]}" == *"ERROR ---> A custom truststore was specified"* ]]
}

@test "success case when the custom certificate is present in the expected path" {
    prepareEnv

    local expected=0
    local pathExpected="${KOGITO_HOME}/certs/custom-truststore/my-cert.jks"

    mkdir -p ${KOGITO_HOME}/certs/custom-truststore
    touch ${KOGITO_HOME}/certs/custom-truststore/my-cert.jks
    CUSTOM_TRUSTSTORE=my-cert.jks

    run configure

    echo "Result is [$status] and expected is [${expected}]" >&2
    [ "$status" = "${expected}" ]
    echo "Output is: ${lines[@]}"
    [ "${lines[0]}" = "INFO ---> Configuring custom Java Truststore 'my-cert.jks' in the path /tmp/kogito/certs/custom-truststore" ]
}

@test "success case when no custom certificate is given" {
    local expected=0

    prepareEnv
    run configure

    echo "Result is [$status] and expected is [${expected}]" >&2
    [ "$status" = "${expected}" ]
    echo "Truststore Args should be empty, but was ${CUSTOM_TRUSTSTORE_ARGS}" >&2
    [ "${CUSTOM_TRUSTSTORE_ARGS}" = "" ]
}
