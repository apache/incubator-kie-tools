#!/bin/sh
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

SCRIPT_DIR=$(dirname "${0}")
ADDED_DIR="${SCRIPT_DIR}"/added
BUILD_DIR="${KOGITO_HOME}"/build
APPS_MAVEN_OPTIONS="-Dquarkus.package.type=fast-jar -Dquarkus.build.image=false -Dquarkus.container-image.build=false -B"
MAVEN_OPTIONS="${MAVEN_OPTIONS} ${APPS_MAVEN_OPTIONS}"

mkdir -p "${BUILD_DIR}"
cp -v "${ADDED_DIR}"/* "${BUILD_DIR}"

env MAVEN_SETTINGS_PATH=${MAVEN_CONTAINER_BUILD_SETTINGS_PATH} mvn -am package ${MAVEN_OPTIONS} -f "${BUILD_DIR}"/pom.xml

chown -R 1001:0 "${KOGITO_HOME}"
chmod -R ug+rwX "${KOGITO_HOME}"
