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

@docker.io/apache/incubator-kie-kogito-jit-runner
Feature: Kogito-jit-runner feature.

  Scenario: verify if all labels are correctly set on kogito-jit-runner image
    Given image is built
    Then the image should contain label maintainer with value Apache KIE <dev@kie.apache.org>
    And the image should contain label io.openshift.expose-services with value 8080:http
    And the image should contain label io.k8s.description with value Runtime image for Kogito JIT Runner
    And the image should contain label io.k8s.display-name with value Kogito JIT Runner
    And the image should contain label io.openshift.tags with value kogito,jit-runner

  Scenario: Verify default http port
    When container is started with env
      | variable     | value |
      | SCRIPT_DEBUG | false |
    Then container log should match regex -Djava\.library\.path="/home/kogito/lib" -Dquarkus\.http\.host=0\.0\.0\.0 -Dquarkus\.http\.port=8080 -jar "/home/kogito/bin/quarkus-app/quarkus-run\.jar"

  Scenario: Verify that jit runner can evaluate a DMN model with a context
    When container is started with env
      | variable     | value |
      | SCRIPT_DEBUG | false  |
    Then container log should match regex Listening on: http://0\.0\.0\.0:8080
    And copy ../../tests/features/jitdmn.json to /tmp in container
    And run head -c 200 /tmp/jitdmn.json in container and immediately check its output contains "model"
    And run curl -sS -H Content-Type:application/json --request POST --data-binary @/tmp/jitdmn.json http://127.0.0.1:8080/jitdmn in container and check its output contains {"sum":3,"m":2,"n":1}
