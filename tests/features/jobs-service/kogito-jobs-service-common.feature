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

@quay.io/kiegroup/kogito-jobs-service-ephemeral
@quay.io/kiegroup/kogito-jobs-service-postgresql
@quay.io/kiegroup/kogito-jobs-service-allinone
Feature: Kogito-jobs-service common feature.

  Scenario: verify if the events is correctly enabled
    When container is started with env
      | variable                | value                                     |
      | SCRIPT_DEBUG            | true                                      |
      | ENABLE_EVENTS           | true                                      |
      | KOGITO_JOBS_PROPS       | -Dkafka.bootstrap.servers=localhost:11111 |
    Then container log should contain -Dkafka.bootstrap.servers=localhost:11111 -Dquarkus.profile=events-support -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080 -jar
