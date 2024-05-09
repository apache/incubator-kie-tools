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

# Run only on images that won't die instantly
# See https://github.com/apache/incubator-kie-kogito-images/issues/1722
@quay.io/kiegroup/kogito-data-index-ephemeral
#@quay.io/kiegroup/kogito-data-index-postgresql
#@quay.io/kiegroup/kogito-jit-runner
@quay.io/kiegroup/kogito-jobs-service-ephemeral
#@quay.io/kiegroup/kogito-jobs-service-postgresql
Feature: Common tests for Kogito images

  Scenario: Verify if the properties were correctly set using DEFAULT MEM RATIO
    When container is started with args
      | arg       | value                                                                           |
      | command   | bash -c "sleep 5s; /home/kogito/kogito-app-launch.sh"                           |
      | mem_limit | 1073741824                                                                      |
      | env_json  | {"SCRIPT_DEBUG":"true", "JAVA_MAX_MEM_RATIO": 80, "JAVA_INITIAL_MEM_RATIO": 25} |
    Then container log should match regex -Xms205m
    And container log should match regex -Xmx819m

  Scenario: Verify if the DEFAULT MEM RATIO properties are overridden with different values
    When container is started with args
      | arg       | value                                                                           |
      | command   | bash -c "sleep 5s; /home/kogito/kogito-app-launch.sh"                           |
      | mem_limit | 1073741824                                                                      |
      | env_json  | {"SCRIPT_DEBUG":"true", "JAVA_MAX_MEM_RATIO": 50, "JAVA_INITIAL_MEM_RATIO": 10} |
    Then container log should match regex -Xms51m
    And container log should match regex -Xmx512m

  Scenario: Verify if the properties were correctly set when aren't passed
    When container is started with args
      | arg       | value                                                 |
      | command   | bash -c "sleep 5s; /home/kogito/kogito-app-launch.sh" |
      | mem_limit | 1073741824                                            |
      | env_json  | {"SCRIPT_DEBUG":"true"}                               |
    Then container log should match regex -Xms128m
    And container log should match regex -Xmx512m

  Scenario: Verify if Java Remote Debug is correctly configured
    When container is started with args
      | arg       | value                                                                   |
      | command   | bash -c "sleep 5s; /home/kogito/kogito-app-launch.sh"                   |
      | env_json  | {"SCRIPT_DEBUG":"true", "JAVA_DEBUG":"true", "JAVA_DEBUG_PORT":"9222"}  |
    Then container log should match regex -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9222

  Scenario: Verify if the DEFAULT MEM RATIO properties are overridden with different values from user provided Xmx and Xms
    When container is started with args
      | arg       | value                                                                                                                 |
      | command   | bash -c "sleep 5s; /home/kogito/kogito-app-launch.sh"                                                                 |
      | mem_limit | 1073741824                                                                                                            |
      | env_json  | {"SCRIPT_DEBUG":"true", "JAVA_MAX_MEM_RATIO": 50, "JAVA_INITIAL_MEM_RATIO": 10, "JAVA_OPTIONS":"-Xms4000m -Xmx8000m"} |
    Then container log should match regex -Xms4000m
    And container log should match regex -Xmx8000m

