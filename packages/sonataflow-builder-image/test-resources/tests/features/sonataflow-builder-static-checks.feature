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
Feature: SonataFlow Builder Static Checks

  Scenario: Verify if the sonataflow and quarkus files are under /home/kogito/.m2/repository
    When container is started with command bash
    Then file /home/kogito/.m2/repository/io/quarkus/platform/quarkus-bom should exist and be a directory
    And file /home/kogito/.m2/repository/org/apache/kie/sonataflow/sonataflow-quarkus/ should exist and be a directory
    And file /home/kogito/.m2/repository/org/kie/kie-addons-quarkus-persistence-jdbc/ should exist and be a directory
    And file /home/kogito/.m2/repository/io/quarkus/quarkus-agroal should exist and be a directory
    And file /home/kogito/.m2/repository/io/quarkus/quarkus-jdbc-postgresql should exist and be a directory
