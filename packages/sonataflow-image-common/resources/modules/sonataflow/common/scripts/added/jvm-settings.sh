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

# Calculate the java memory for the given maven build.
# It is based in the container-limits that can be found in
# https://github.com/jboss-openshift/cct_module/blob/master/jboss/container/java/jvm/bash/artifacts/opt/jboss/container/java/jvm/container-limits
#
# It respects the jvm.config of ${maven.projectBasedir}/.mvn/jvm.config
# Usage: add the following line in the desired script:
#   source "${KOGITO_HOME}"/launch/jvm-settings.sh
# If you want to provide the jvm.config, be sure to access the target directory before sourcing this script.


set -e

# shellcheck source=/dev/null
source "${KOGITO_HOME}"/launch/logging.sh

log_info "--> checking if .mvn/jvm.config exists."
if [ -f ".mvn/jvm.config" ]; then
  log_info "---> .mvn/jvm.config exists."
  JAVA_OPTIONS=$(cat .mvn/jvm.config)
  export JAVA_OPTIONS
else
  log_info "---> .mvn/jvm.config does not exists, memory will be calculated based on container limits."
fi

MAVEN_OPTS="${JAVA_OPTIONS} $("${JBOSS_CONTAINER_JAVA_JVM_MODULE}"/java-default-options) $("${JBOSS_CONTAINER_JAVA_JVM_MODULE}"/debug-options)"
export MAVEN_OPTS