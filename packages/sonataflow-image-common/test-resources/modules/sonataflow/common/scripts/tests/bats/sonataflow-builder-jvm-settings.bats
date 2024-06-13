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
export JBOSS_CONTAINER_JAVA_JVM_MODULE=/tmp/container/java/jvm
mkdir -p "${KOGITO_HOME}"/launch
mkdir -p "${JBOSS_CONTAINER_JAVA_JVM_MODULE}"
cp $BATS_TEST_DIRNAME/../../../../../kogito-logging/added/logging.sh "${KOGITO_HOME}"/launch/
cp -r $BATS_TEST_DIRNAME/../../../../../kogito-dynamic-resources/added/* "${JBOSS_CONTAINER_JAVA_JVM_MODULE}"/
chmod -R +x "${JBOSS_CONTAINER_JAVA_JVM_MODULE}"
cp $BATS_TEST_DIRNAME/../../added/jvm-settings.sh "${KOGITO_HOME}"/launch/

teardown() {
    rm -rf "${KOGITO_HOME}"
    rm -rf "${JBOSS_CONTAINER_JAVA_JVM_MODULE}"
}

@test "run jvm-settings with no custom conf" {
    expected_status_code=0
    mkdir -p $KOGITO_HOME/my-app

    source ${KOGITO_HOME}/launch/jvm-settings.sh

    echo "MAVEN_OPTS is: ${MAVEN_OPTS}"
    [[ "${MAVEN_OPTS}" == *"-XX:+UseParallelGC -XX:MinHeapFreeRatio=10 -XX:MaxHeapFreeRatio=20 -XX:GCTimeRatio=4 -XX:AdaptiveSizePolicyWeight=90 -XX:+ExitOnOutOfMemoryError"* ]]
}

@test "run jvm-settings with custom conf" {
    expected_status_code=0
    mkdir -p $KOGITO_HOME/my-app/.mvn
    cd $KOGITO_HOME/my-app
    echo "-Xmx1024m -Xms512m -Xotherthing" > $KOGITO_HOME/my-app/.mvn/jvm.config

    source ${KOGITO_HOME}/launch/jvm-settings.sh

    echo "MAVEN_OPTS is: ${MAVEN_OPTS}"
    [[ "${MAVEN_OPTS}" == *"-Xmx1024m -Xms512m -Xotherthing -XX:+UseParallelGC -XX:MinHeapFreeRatio=10 -XX:MaxHeapFreeRatio=20 -XX:GCTimeRatio=4 -XX:AdaptiveSizePolicyWeight=90 -XX:+ExitOnOutOfMemoryError"* ]]
}
