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

# imports
# shellcheck source=/dev/null
source "${KOGITO_HOME}"/launch/logging.sh

function prepareEnv() {
    # keep it on alphabetical order
    unset CUSTOM_TRUSTSTORE
    unset CUSTOM_TRUSTSTORE_PASSWORD
}

function configure() {
    configure_custom_truststore
}

# Exit codes
# 1 - General error
function configure_custom_truststore() {
    local defaultCustomTruststorePath="${KOGITO_HOME}/certs/custom-truststore"

    if [ ! -z "${CUSTOM_TRUSTSTORE}" ]; then
        CUSTOM_TRUSTSTORE_PATH="${defaultCustomTruststorePath}/${CUSTOM_TRUSTSTORE}"
        log_info "---> Configuring custom Java Truststore '${CUSTOM_TRUSTSTORE}' in the path ${defaultCustomTruststorePath}"
        if [ ! -f "${CUSTOM_TRUSTSTORE_PATH}" ]; then
            log_error "---> A custom truststore was specified ('${CUSTOM_TRUSTSTORE}'), but wasn't found in the path ${defaultCustomTruststorePath}. \
Make sure that the path is mounted and accessible in your container"
            exit 1
        fi
        CUSTOM_TRUSTSTORE_ARGS="-Djavax.net.ssl.trustStore=${CUSTOM_TRUSTSTORE_PATH}"
        if [ ! -z "${CUSTOM_TRUSTSTORE_PASSWORD}" ]; then
            CUSTOM_TRUSTSTORE_ARGS="${CUSTOM_TRUSTSTORE_ARGS} -Djavax.net.ssl.trustStorePassword=${CUSTOM_TRUSTSTORE_PASSWORD}"
        fi
    fi
}
