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
    unset KOGITO_DATAINDEX_HTTP_URL
}

function configure() {
    configure_data_index_url
}

# Exit codes:
#   10 - invalid url
function configure_data_index_url {
    url_simple_regex='(https?)://'
    local dataIndexURL=${KOGITO_DATAINDEX_HTTP_URL}
    if [ "${dataIndexURL}x" != "x" ]; then
        if [[ ! "${dataIndexURL}x" =~ $url_simple_regex ]]; then
            log_error "URL must start with http or https."
            exit 10
        fi
    else
        log_info "Data index url not set, default will be used: http://localhost:8180"
        dataIndexURL="http://localhost:8180"
    fi
    KOGITO_TASK_CONSOLE_PROPS="${KOGITO_TASK_CONSOLE_PROPS} -Dkogito.dataindex.http.url=${dataIndexURL}"
}

