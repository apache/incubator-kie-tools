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
load $BATS_TEST_DIRNAME/../../added/launch/kogito-explainability.sh

teardown() {
    rm -rf "${KOGITO_HOME}"
}


@test "test if the default explainability communication type is correctly set" {
    configure_explainability_jar
    expected="messaging"
    echo "result: ${EXPLAINABILITY_SERVICE_JAR} \n expected: ${expected}"
    [ "${EXPLAINABILITY_SERVICE_COMMUNICATION}" = "${expected}" ]
}

@test "test if explainability communication service default value is correctly set if a nonsense type is set" {
    EXPLAINABILITY_COMMUNICATION="nonsense"
    configure_explainability_jar
    expected="messaging"
    echo "result: ${EXPLAINABILITY_SERVICE_JAR} \n expected: ${expected}"
    [ "${EXPLAINABILITY_SERVICE_COMMUNICATION}" = "${expected}" ]
}

@test "test if explainability communication service default value s correctly set if set to rest" {
    EXPLAINABILITY_COMMUNICATION="rest"
    configure_explainability_jar
    expected="rest"
    echo "result: ${EXPLAINABILITY_SERVICE_JAR} \n expected: ${expected}"
    [ "${EXPLAINABILITY_SERVICE_COMMUNICATION}" = "${expected}" ]
}