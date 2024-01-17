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


#import
source "${KOGITO_HOME}"/launch/logging.sh

function prepareEnv() {
    # keep it on alphabetical order
    unset EXPLAINABILITY_ENABLED
}

function configure() {
    enable_explainability
}


function enable_explainability {
    local allowed_values=("TRUE" "FALSE")
    local explainability_enabled="true"
    # shellcheck disable=SC2153
    if [[ ! "${allowed_values[*]}" =~ ${EXPLAINABILITY_ENABLED^^} ]]; then
        log_warning "Explainability enabled type ${EXPLAINABILITY_ENABLED} is not allowed, the allowed types are [${allowed_values[*]}]. Defaulting to ${explainability_enabled}."
    elif [ "${EXPLAINABILITY_ENABLED^^}" == "FALSE" ]; then
        explainability_enabled="${EXPLAINABILITY_ENABLED^^}"
    fi
    log_info "Explainability is enabled: ${explainability_enabled}"
    KOGITO_TRUSTY_PROPS="${KOGITO_TRUSTY_PROPS} -Dtrusty.explainability.enabled=${explainability_enabled,,}"
}
