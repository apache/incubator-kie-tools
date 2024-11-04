#!/bin/bash
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
MGMT_CONSOLE_HOME="${KOGITO_HOME}/management-console"

# Add the generated files that can't be downloaded on Cpaas
cp -v "${SCRIPT_DIR}/added/EnvJson.schema.json" "${MGMT_CONSOLE_HOME}"
cp -v "${SCRIPT_DIR}/added/image-env-to-json-standalone" "${MGMT_CONSOLE_HOME}"

# Unzip the app
cd "${MGMT_CONSOLE_HOME}/app"
unzip -q "sonataflow-management-console-webapp-image-build.zip"
rm -rf "sonataflow-management-console-webapp-image-build.zip"