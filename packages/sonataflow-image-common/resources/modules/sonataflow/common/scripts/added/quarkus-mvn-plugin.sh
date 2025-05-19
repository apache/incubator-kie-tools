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


# Useful functions for runnning Quarkus Maven Plugin. Moved from bare scripts to facilitate testing.

set -e

# Default version variables
quarkus_version="${QUARKUS_PLATFORM_VERSION}"
kogito_version="${KOGITO_VERSION}"

# shellcheck source=/dev/null
source "${KOGITO_HOME}"/launch/logging.sh

# Function to append the correct version to the extension if needed
append_version() {
    local extension="$1"
    local group_id
    local artifact_id
    local version

    # Split extension into groupId, artifactId, and version
    IFS=":" read -r group_id artifact_id version <<< "$extension"

    # If the version is missing, append the default version based on the groupId
    if [ -z "$version" ]; then
        if [[ "$group_id" == "io.quarkus" ]]; then
            extension="${group_id}:${artifact_id}:${quarkus_version}"
        elif [[ "$group_id" == *"kie"* || "$group_id" == *"kogito"* || "$artifact_id" == *"kogito"* || "$artifact_id" == *"sonataflow"* ]]; then
            extension="${group_id}:${artifact_id}:${kogito_version}"
        fi
    fi

    echo "$extension"
}

process_extensions() {
    local extensions="$1"
    local processed_extensions=""
    IFS=',' read -r -a extension_array <<< "$extensions"
    for ext in "${extension_array[@]}"; do
        processed_extensions+=$(append_version "$ext")","
    done
    # Remove the trailing comma
    processed_extensions="${processed_extensions%,}"
    echo "$processed_extensions"
}

run_quarkus_mvn_add_extension() {
    local extensions="$1"
    local ignore_jvm_settings=${2:-false}

    local processed_extensions=$(process_extensions "$extensions")
  
    if [ "${SCRIPT_DEBUG}" = "true" ]; then
        set -x
        export MAVEN_ARGS_APPEND="${MAVEN_ARGS_APPEND} -X --batch-mode"
        log_info "Script debugging is enabled, allowing bash commands and their arguments to be printed as they are executed"
        printenv
    fi

    if [ "${ignore_jvm_settings}" != "true" ]; then
        source "${KOGITO_HOME}"/launch/configure-jvm-mvn.sh
    fi

    log_info "Processed extensions to be added ${processed_extensions}"

    "${MAVEN_CMD}" -B ${MAVEN_ARGS_APPEND} \
        -nsu \
        -B \
        -s "${MAVEN_SETTINGS_PATH}" \
        -DplatformVersion="${QUARKUS_PLATFORM_VERSION}" \
        -Dextensions="${processed_extensions}" \
        ${QUARKUS_ADD_EXTENSION_ARGS} \
        "${QUARKUS_PLATFORM_GROUPID}":quarkus-maven-plugin:"${QUARKUS_PLATFORM_VERSION}":add-extension
}