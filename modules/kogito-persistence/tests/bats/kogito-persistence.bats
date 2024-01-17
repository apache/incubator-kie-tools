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


export KOGITO_HOME=$BATS_TMPDIR/kogito_home
export MOCK_RESPONSE=""
mkdir -p "${KOGITO_HOME}"/launch

cp $BATS_TEST_DIRNAME/../../../kogito-logging/added/logging.sh "${KOGITO_HOME}"/launch/
cp $BATS_TEST_DIRNAME/../../../kogito-kubernetes-client/added/kogito-kubernetes-client.sh "${KOGITO_HOME}"/launch/

# imports
source $BATS_TEST_DIRNAME/../../added/kogito-persistence.sh

setup() {
    export HOME="${KOGITO_HOME}"
    mkdir -p "${KOGITO_HOME}"
    mkdir -p "${KOGITO_HOME}"/bin
    mkdir -p "${KOGITO_HOME}"/data/protobufs/
    mkdir -p "${KOGITO_HOME}"/podinfo
    echo "exampleapp-cm" > "${KOGITO_HOME}"/podinfo/protobufcm
}

teardown() {
    rm -rf "${KOGITO_HOME}"
    rm -rf /tmp/src
    rm -rf "${KOGITO_HOME}"/bin/*
}

@test "There's some proto files in the target directory" {
    mkdir -p /tmp/src/target/classes/META-INF/resources/persistence/protobuf
    touch /tmp/src/target/classes/META-INF/resources/persistence/protobuf/{file1.proto,kogito-application.proto}

    run copy_persistence_files

    echo "result= ${lines[@]}"

    [ "$status" -eq 0 ]
    [ "${lines[0]}" = "INFO ---> [persistence] Copying persistence files..." ]
    [ "${lines[1]}" = "'/tmp/src/target/classes/META-INF/resources/persistence/protobuf/file1.proto' -> '"${KOGITO_HOME}"/bin/file1.proto'" ]
    [ "${lines[2]}" = "'/tmp/src/target/classes/META-INF/resources/persistence/protobuf/kogito-application.proto' -> '"${KOGITO_HOME}"/bin/kogito-application.proto'" ]
    [ "${lines[3]}" = "removed '"${KOGITO_HOME}"/bin/kogito-application.proto'" ]
    [ "${lines[4]}" = "INFO ---> [persistence] Moving persistence files to final directory" ]
    [ "${lines[5]}" = "'"${KOGITO_HOME}"/bin/file1.proto' -> '"${KOGITO_HOME}"/data/protobufs/file1.proto'" ]
}

@test "There are no persistence files" {
    KOGITO_HOME=/tmp/kogito

    run copy_persistence_files

    echo "result= ${lines[@]}"

    [ "$status" -eq 0 ]
    [ "${lines[0]}" = "INFO ---> [persistence] Copying persistence files..." ]
    [ "${lines[1]}" = "INFO ---> [persistence] Skip copying files, persistence directory does not exist..." ]
}

@test "There's some proto files in the bin directory" {
    touch "${KOGITO_HOME}"/bin/file1.proto

    run move_persistence_files

    echo "result= ${lines[@]}"

    [ "$status" -eq 0 ]
    [ "${lines[0]}" = "INFO ---> [persistence] Moving persistence files to final directory" ]
    [ "${lines[1]}" = "'"${KOGITO_HOME}"/bin/file1.proto' -> '"${KOGITO_HOME}"/data/protobufs/file1.proto'" ]
}

@test "There's no proto files in the bin directory" {
    run move_persistence_files

    echo "result= ${lines[@]}"

    [ "$status" -eq 0 ]
    [ "${lines[0]}" = "INFO ---> [persistence] Moving persistence files to final directory" ]
    [ "${lines[1]}" = "INFO ---> [persistence] Skip copying files, "${KOGITO_HOME}"/bin directory does not have proto files!" ]
}
