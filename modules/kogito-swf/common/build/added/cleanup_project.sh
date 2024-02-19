#!/usr/bin/env bash
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

set -e

#remove unnecessary files
echo 'Clean unnecessary files'
rm -rfv "${PROJECT_ARTIFACT_ID}"/target
rm -rfv "${PROJECT_ARTIFACT_ID}"/src/main/resources/*
rm -rfv "${PROJECT_ARTIFACT_ID}"/src/main/docker
rm -rfv "${PROJECT_ARTIFACT_ID}"/.mvn/wrapper
rm -rfv "${PROJECT_ARTIFACT_ID}"/mvnw*
rm -rfv "${PROJECT_ARTIFACT_ID}"/src/test
rm -rfv "${PROJECT_ARTIFACT_ID}"/*.bak

# Maven useless files
# Needed to avoid Maven to automatically re-download from original Maven repository ...
echo 'Clean Maven useless files'
find "${KOGITO_HOME}"/.m2/repository -name _remote.repositories -type f -delete
find "${KOGITO_HOME}"/.m2/repository -name _maven.repositories -type f -delete
find "${KOGITO_HOME}"/.m2/repository -name *.lastUpdated -type f -delete

# Remove files that include build timestamps to have reproducible images
find "${KOGITO_HOME}"/.m2/ -name resolver-status.properties -delete 
# Remove quarkus registry
rm -rf "${KOGITO_HOME}"/.m2/repository/io/quarkus/registry/
