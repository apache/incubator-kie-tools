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

@docker.io/apache/incubator-kie-kogito-jobs-service-ephemeral
Feature: Kogito-jobs-service-ephemeral feature.

  Scenario: verify if all labels are correctly set kogito-jobs-service image image
    Given image is built
    Then the image should contain label maintainer with value Apache KIE <dev@kie.apache.org>
    And the image should contain label io.openshift.expose-services with value 8080:http
    And the image should contain label io.k8s.description with value Runtime image for Kogito in memory Jobs Service
    And the image should contain label io.k8s.display-name with value Kogito in memory Jobs Service
    And the image should contain label io.openshift.tags with value kogito,jobs-service-ephemeral

  Scenario: Verify if the application jar exists
    When container is started with command bash
    Then run sh -c 'ls /home/kogito/bin/ephemeral/quarkus-app/quarkus-run.jar' in container and immediately check its output for /home/kogito/bin/ephemeral/quarkus-app/quarkus-run.jar

  Scenario: Verify if the debug is correctly enabled with the ephemeral jar
    When container is started with env
      | variable     | value |
      | SCRIPT_DEBUG | true  |
    Then container log should contain -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080 -jar /home/kogito/bin/ephemeral/quarkus-app/quarkus-run.jar
    And container log should contain started in
    And container log should not contain Application failed to start

