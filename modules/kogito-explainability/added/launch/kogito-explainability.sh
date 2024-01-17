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
    unset EXPLAINABILITY_COMMUNICATION
}

function configure() {
    configure_explainability_jar
}

function configure_explainability_jar {
    local allowed_communication_types=("REST" "MESSAGING")
    local communication="MESSAGING"
    if [[ ! "${allowed_communication_types[*]}" =~ ${EXPLAINABILITY_COMMUNICATION^^} ]]; then
        log_warning "Explainability communication type ${EXPLAINABILITY_COMMUNICATION} is not allowed, the allowed types are [${allowed_communication_types[*]}]. Defaulting to ${communication}."
        unset EXPLAINABILITY_COMMUNICATION

    elif [ "x${EXPLAINABILITY_COMMUNICATION}" != "x" ]; then
        communication="${EXPLAINABILITY_COMMUNICATION}"
    fi

    log_info "Explainability communication is set to ${communication}"
    EXPLAINABILITY_SERVICE_COMMUNICATION="${communication,,}"
}
