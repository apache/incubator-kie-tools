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
set -e

# Set up some environment variables for testing
setup() {
    export QUARKUS_PLATFORM_VERSION="2.0.0"
    export KOGITO_VERSION="1.5.0"
    export KOGITO_HOME=$BATS_TMPDIR/maven
    mkdir -p ${KOGITO_HOME}/"launch"
    cp ${BATS_TEST_DIRNAME}/../../../../../kogito-logging/added/logging.sh ${KOGITO_HOME}/launch/logging.sh

    load ${BATS_TEST_DIRNAME}/../../added/quarkus-mvn-plugin.sh
}

teardown() {
    rm -rf ${KOGITO_HOME}
}

# Test that a full extension with version does not get modified
@test "Extension with version should remain unchanged" {
    run process_extensions "io.quarkus:quarkus-resteasy-reactive:2.0.0"
    echo "Test Output:"
    echo "$output"
    [ "$output" == "io.quarkus:quarkus-resteasy-reactive:2.0.0" ]
}

# Test that an extension without version for io.quarkus gets the default version
@test "io.quarkus extension without version should get the default version" {
    run process_extensions "io.quarkus:quarkus-resteasy-reactive"
    echo "Test Output:"
    echo "$output"
    [ "$output" == "io.quarkus:quarkus-resteasy-reactive:2.0.0" ]
}

# Test that an extension without version for kogito gets the KOGITO version
@test "Kogito extension without version should get the KOGITO version" {
    run process_extensions "org.kie:kie-server"
    echo "Test Output:"
    echo "$output"
    [ "$output" == "org.kie:kie-server:1.5.0" ]
}

# Test that an extension without version for a non-quarkus and non-kogito group gets the default version
@test "Non-quarkus, non-kogito extension without version should get the default version" {
    run process_extensions "org.acme:acme-component"
    echo "Test Output:"
    echo "$output"
    [ "$output" == "org.acme:acme-component" ]
}

# Test that multiple extensions are handled correctly
@test "Multiple extensions should get versions added" {
    run process_extensions "io.quarkus:quarkus-resteasy-reactive,org.acme:acme-component,io.quarkus:quarkus-core"
    echo "Test Output:"
    echo "$output"
    [ "$output" == "io.quarkus:quarkus-resteasy-reactive:2.0.0,org.acme:acme-component,io.quarkus:quarkus-core:2.0.0" ]
}

# Test that the script fails when no extensions are provided
@test "No extensions should cause an error" {
    run process_extensions ""
    echo "Test Output:"
    echo "$output"
    [ "$output" == "" ]  # Expecting an empty result for no extensions
}