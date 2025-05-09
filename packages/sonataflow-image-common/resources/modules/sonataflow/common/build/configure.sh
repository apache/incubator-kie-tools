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

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ADDED_DIR="${SCRIPT_DIR}"/added
LAUNCH_DIR="${KOGITO_HOME}"/launch
BUILD_DIR="${KOGITO_HOME}"/build

mkdir -p "${BUILD_DIR}"
cp -v "${ADDED_DIR}"/cleanup_project.sh "${BUILD_DIR}"
cp -v "${ADDED_DIR}"/zip_files.sh "${BUILD_DIR}"
cp -v "${ADDED_DIR}"/create-app.sh "${LAUNCH_DIR}"

chown -R 1001:0 "${KOGITO_HOME}"
chmod -R ug+rwX "${KOGITO_HOME}"

cd "${KOGITO_HOME}"

# Create app (settings.xml path is replaced only for these commands and automatically restored when they end)
env MAVEN_SETTINGS_PATH=${MAVEN_CONTAINER_BUILD_SETTINGS_PATH} "${LAUNCH_DIR}"/create-app.sh

env MAVEN_SETTINGS_PATH=${MAVEN_CONTAINER_BUILD_SETTINGS_PATH} "${BUILD_DIR}"/cleanup_project.sh
env MAVEN_SETTINGS_PATH=${MAVEN_CONTAINER_BUILD_SETTINGS_PATH} "${BUILD_DIR}"/zip_files.sh

chown -R 1001:0 "${KOGITO_HOME}"
chmod -R ug+rwX "${KOGITO_HOME}"
