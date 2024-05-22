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

@docker.io/apache/incubator-kie-kogito-data-index-ephemeral
@docker.io/apache/incubator-kie-kogito-data-index-postgresql
@docker.io/apache/incubator-kie-kogito-jit-runner
@docker.io/apache/incubator-kie-kogito-jobs-service-ephemeral
@docker.io/apache/incubator-kie-kogito-jobs-service-postgresql
Feature: Common tests for Custom TrustStore configuration
  # This test sets an invalid certificate to the container, it fails to start, and if timing is bad cekit hangs on 'Running command ps -C java in container'
  # See https://github.com/apache/incubator-kie-kogito-images/issues/1722
  @ignore
  Scenario: Verify if a custom certificate is correctly handled
    When container is started with command bash -c "/home/kogito/kogito-app-launch.sh"
      | variable            | value              |
      | CUSTOM_TRUSTSTORE   | my-truststore.jks  |
      | RUNTIME_TYPE        | quarkus            |
    Then container log should contain INFO ---> Configuring custom Java Truststore 'my-truststore.jks' in the path /home/kogito/certs/custom-truststore
    Then container log should contain ERROR ---> A custom truststore was specified ('my-truststore.jks'), but wasn't found in the path /home/kogito/certs/custom-truststore
