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
    cp -r $BATS_TEST_DIRNAME/../../../../../../tests/shell/sonataflow-builder/resources/greet-with-inputschema/* ${TEMPD}

    # We don't care about the errors to try to execute and build the program, just the copy matters
    source ${KOGITO_HOME}/launch/build-app.sh ${TEMPD} || true
    
    [[ -f "${KOGITO_HOME}"/serverless-workflow-project/src/main/resources/greet.sw.json ]]
    [[ -f "${KOGITO_HOME}"/serverless-workflow-project/src/main/resources/schemas/input.json ]]
}
