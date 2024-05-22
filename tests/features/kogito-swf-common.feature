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

@docker.io/apache/incubator-kie-sonataflow-builder
@docker.io/apache/incubator-kie-sonataflow-devmode
Feature: Serverless Workflow images common

  Scenario: Verify if the swf and quarkus files are under /home/kogito/.m2/repository
    When container is started with command bash
    Then file /home/kogito/.m2/repository/io/quarkus/platform/quarkus-bom/3.8.4/quarkus-bom-3.8.4.pom should exist
      And file /home/kogito/.m2/repository/org/apache/kie/sonataflow/sonataflow-quarkus/ should exist and be a directory

  # This check should be enabled again once a similar check is done on runtimes
  # to make sure we only have one version of quarkus bom ...
  # See https://issues.redhat.com/browse/KOGITO-8555 to enable again
  # Scenario: verify if there is no dependencies with multiple versions in /home/kogito/.m2/repository
  #   When container is started with command bash
  #   Then run sh -c 'ls /home/kogito/.m2/repository/io/quarkus/quarkus-bom  | wc -l' in container and immediately check its output for 1
