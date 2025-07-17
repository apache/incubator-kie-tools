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

script_dir_path="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
resources_path="$1"
if [ -n "${resources_path}" ]; then
    resources_path="$(realpath "${resources_path}")"
fi

# shellcheck source=/dev/null
source "${script_dir_path}"/logging.sh

if [ "${SCRIPT_DEBUG}" = "true" ] ; then
    set -x
    export MAVEN_ARGS_APPEND="${MAVEN_ARGS_APPEND} -X --batch-mode" 
    log_info "Script debugging is enabled, allowing bash commands and their arguments to be printed as they are executed"
    log_info "Resources path is ${resources_path}"
    printenv
fi

# Copy resources if exists
log_info "-> Copying files from ${resources_path}, if any..."
if [ -n "${resources_path}" ]; then
    destination="${KOGITO_HOME}/serverless-workflow-project/src/main/resources/"
    log_info "-> Destination folder is ${destination}"
    cp -vR ${resources_path}/* ${destination}
    find "${resources_path}" -name 'jvm.config' -exec echo "--> found {}" \; -exec mkdir -p  ${destination}/.mvn \; -exec cp -v {} ${destination}/.mvn/ \;
else
    log_warning "-> Nothing to copy from ${resources_path}"
fi

# Overwrite Quarkus Registry config if necessary
if [ -n "$QUARKUS_REGISTRY_CONFIG_PATH" ] && [ -f "$QUARKUS_REGISTRY_CONFIG_PATH" ]; then
  log_info "-> Using custom Quarkus registry config: $QUARKUS_REGISTRY_CONFIG_PATH"
  cp -v "$QUARKUS_REGISTRY_CONFIG_PATH" "${KOGITO_HOME}/.quarkus/config.yaml"
fi

source "${script_dir_path}"/configure-jvm-mvn.sh

if [ ! -z "${QUARKUS_EXTENSIONS}" ]; then
    log_info "Adding extensions '${QUARKUS_EXTENSIONS}'"
    ${script_dir_path}/add-extension.sh "${QUARKUS_EXTENSIONS}" "true"
fi

cd ${KOGITO_HOME}/serverless-workflow-project

"${MAVEN_CMD}" -B ${MAVEN_ARGS_APPEND} \
    -nsu \
    -B \
    -s "${MAVEN_SETTINGS_PATH}" \
    -DskipTests \
    -Dmaven.javadoc.skip=true \
    -Dquarkus.container-image.build=false \
    clean install
