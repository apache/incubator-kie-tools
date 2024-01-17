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

# imports
load $BATS_TEST_DIRNAME/../../added/launch/kogito-trusty-ui.sh

teardown() {
    rm -rf "${KOGITO_HOME}"
}

@test "test if the default value for trusty url will be set" {
    local expected=" -Dkogito.trusty.http.url=http://localhost:8180"
    configure_trusty_url
    echo "Result is [${KOGITO_TRUSTY_UI_PROPS}] and expected is [${expected}]" >&2
    [ "${expected}" = "${KOGITO_TRUSTY_UI_PROPS}" ]
}

@test "test if KOGITO_TRUSTY_URL will be correctly set " {
    export KOGITO_TRUSTY_ENDPOINT="http://10.10.10.10:8080"
    local expected=" -Dkogito.trusty.http.url=http://10.10.10.10:8080"
    configure_trusty_url
    echo "Result is [${KOGITO_TRUSTY_UI_PROPS}] and expected is [${expected}]" >&2
    [ "${expected}" = "${KOGITO_TRUSTY_UI_PROPS}" ]
}

@test "test if a invalid value for trusty url will return the expected exit code" {
    export KOGITO_TRUSTY_ENDPOINT="a.b.c"
    run configure_trusty_url
    [ "${status}" == "10" ]
}


