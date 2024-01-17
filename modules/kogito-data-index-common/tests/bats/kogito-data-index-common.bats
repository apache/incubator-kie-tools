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
load $BATS_TEST_DIRNAME/../../added/launch/kogito-data-index-common.sh


teardown() {
    rm -rf "${KOGITO_HOME}"
}

@test "check if the default quarkus profile is correctly set on data index" {
    local expected=" -Dquarkus.profile=kafka-events-support"
    configure_data_index_quarkus_profile
    echo "Result is [${KOGITO_DATA_INDEX_PROPS}] and expected is [${expected}]"
    [ "${expected}" = "${KOGITO_DATA_INDEX_PROPS}" ]
}

@test "check if a provided data index quarkus profile is correctly set on data index" {
    export KOGITO_DATA_INDEX_QUARKUS_PROFILE="http-events-support"
    local expected=" -Dquarkus.profile=http-events-support"
    configure_data_index_quarkus_profile
    echo "Result is [${KOGITO_DATA_INDEX_PROPS}] and expected is [${expected}]"
    [ "${expected}" = "${KOGITO_DATA_INDEX_PROPS}" ]
}

