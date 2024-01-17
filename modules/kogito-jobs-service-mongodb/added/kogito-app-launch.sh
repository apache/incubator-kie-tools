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

if [ "${SCRIPT_DEBUG}" = "true" ] ; then
    set -x
    SHOW_JVM_SETTINGS="-XshowSettings:properties"
    log_info "Script debugging is enabled, allowing bash commands and their arguments to be printed as they are executed"
    log_info "JVM settings debug is enabled."
    printenv
fi

# Configuration scripts
# Any configuration script that needs to run on image startup must be added here.
CONFIGURE_SCRIPTS=(
  "${KOGITO_HOME}"/launch/kogito-jobs-service-common.sh
  "${KOGITO_HOME}"/launch/configure-custom-truststore.sh
)
source "${KOGITO_HOME}"/launch/configure.sh
#############################################

DYNAMIC_RESOURCES_OPTS="$(${JBOSS_CONTAINER_JAVA_JVM_MODULE}/java-default-options) $(${JBOSS_CONTAINER_JAVA_JVM_MODULE}/debug-options)"

# shellcheck disable=SC2086
exec java ${SHOW_JVM_SETTINGS} ${DYNAMIC_RESOURCES_OPTS} ${JAVA_OPTIONS} ${KOGITO_JOBS_PROPS} ${CUSTOM_TRUSTSTORE_ARGS} \
    -Dquarkus.http.host=0.0.0.0 \
    -Dquarkus.http.port=8080 \
    -jar "${KOGITO_HOME}"/bin/mongodb/quarkus-app/quarkus-run.jar
