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
@docker.io/apache/incubator-kie-sonataflow-builder
Feature: SonataFlow Builder Image Sanity Checks

  Scenario: Verify that the application is built and started correctly
    When container is started with command bash -c '/home/kogito/launch/build-app.sh && java -jar target/quarkus-app/quarkus-run.jar'
      | variable           | value                 |
      | SCRIPT_DEBUG       | false                 |
      | KOGITO_SERVICE_URL | http://localhost:8080 |
    Then container log should match regex Installed features:.*kogito-serverless-workflow
    And container log should match regex Installed features:.*kie-addon-knative-eventing-extension
    And container log should match regex Installed features:.*smallrye-health
    And container log should match regex Listening on: http://0\.0\.0\.0:8080
    And run curl -fsS -o /dev/null -w %{http_code} http://127.0.0.1:8080/q/health/ready in container and immediately check its output contains 200

  Scenario: Verify that the application is built and started correctly when QUARKUS_EXTENSIONS env is used
    When container is started with command bash -c '/home/kogito/launch/build-app.sh && java -jar target/quarkus-app/quarkus-run.jar'
      | variable           | value                                    |
      | SCRIPT_DEBUG       | false                                    |
      | KOGITO_SERVICE_URL | http://localhost:8080                    |
      | QUARKUS_EXTENSIONS | io.quarkus:quarkus-elytron-security-jdbc |
    Then container log should match regex Extension io\.quarkus:quarkus-elytron-security-jdbc.* has been installed
    And container log should match regex Installed features:.*security-jdbc
