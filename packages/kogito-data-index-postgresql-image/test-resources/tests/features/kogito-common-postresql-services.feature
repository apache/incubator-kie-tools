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

@docker.io/apache/incubator-kie-kogito-data-index-postgresql
Feature: Kogito-data-index postgresql feature.

  Scenario: verify if of container is correctly started with postgresql parameters
    When container is started with env
      | variable                         | value                                     |
      | SCRIPT_DEBUG                     | false                                     |
      | QUARKUS_DATASOURCE_JDBC_URL      | jdbc:postgresql://localhost:5432/quarkus  |
      | QUARKUS_DATASOURCE_REACTIVE_URL  | postgresql://localhost:5432/quarkus       |
      | QUARKUS_DATASOURCE_USERNAME      | kogito                                    |
      | QUARKUS_DATASOURCE_PASSWORD      | s3cr3t                                    |
    Then container log should contain -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080 -jar "/home/kogito/bin/quarkus-app/quarkus-run.jar"
     And container log should contain Datasource '<default>': Connection to localhost:5432 refused