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

script_dir_path="$(cd $(dirname "${BASH_SOURCE[0]}") && pwd)"

source "${script_dir_path}"/logging.sh

if [ "${SCRIPT_DEBUG}" = "true" ] ; then
    set -x
    export MAVEN_ARGS_APPEND="${MAVEN_ARGS_APPEND} -X --batch-mode" 
    log_info "Script debugging is enabled, allowing bash commands and their arguments to be printed as they are executed"
    printenv
fi

# copy .mvn/jvm-config from resources to project's base dir.
find . -maxdepth 5 -name  'jvm.config' -exec echo "--> found {}" \; -exec mkdir -p .mvn \; -exec cp -v {} .mvn/ \;
source "${script_dir_path}"/configure-jvm-mvn.sh

# `-o` means offline mode
offline_param="-o"
if [ ! -z "${QUARKUS_EXTENSIONS}" ]; then
    ${KOGITO_HOME}/launch/add-extension.sh "${QUARKUS_EXTENSIONS}" "true"
    offline_param=""
fi

"${MAVEN_HOME}"/bin/mvn -B ${MAVEN_ARGS_APPEND} \
    ${offline_param} \
    -s "${MAVEN_SETTINGS_PATH}" \
    -DskipTests \
    -Dquarkus.http.host=0.0.0.0 \
    -Dquarkus.test.continuous-testing=${QUARKUS_CONTINUOUS_TESTING:-disabled} \
    clean compile quarkus:dev