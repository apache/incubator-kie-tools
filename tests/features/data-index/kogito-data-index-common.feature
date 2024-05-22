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
Feature: Kogito-data-index common feature.

  Scenario: Verify if the debug is correctly enabled and test default http port
    When container is started with env
      | variable               | value   |
      | SCRIPT_DEBUG           | true    |
    Then container log should contain -Djava.library.path=/home/kogito/lib -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080

  Scenario: check if a provided data index quarkus profile is correctly set on data index
    When container is started with env
      | variable                           | value               |
      | SCRIPT_DEBUG                       | true                |
      | KOGITO_DATA_INDEX_QUARKUS_PROFILE  | http-events-support |
    Then container log should contain -Dquarkus.profile=http-events-support