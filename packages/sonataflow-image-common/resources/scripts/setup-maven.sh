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

# Holds common maven configuration for CI;
# Usage: . setup-maven.sh

MVN_MODULE="$(dirname "${BASH_SOURCE[0]}")/../modules/kogito-maven/common"
export MAVEN_OPTIONS="${BUILD_MVN_OPTS} -DskipTests"
# Do not remove below, this can be updated by the python scripts
MAVEN_IGNORE_SELF_SIGNED_CERTIFICATE=true

maven_settings_path=$1
if [ -z "${maven_settings_path}" ]; then
    maven_settings_path="${HOME}"/.m2/settings.xml
    echo "Maven settings path argument is empty, using ${maven_settings_path}"
fi

LOGGING_MODULE="$(dirname "${BASH_SOURCE[0]}")/../modules/kogito-logging/"
source "${LOGGING_MODULE}"/added/logging.sh

echo "Updating settings file ${maven_settings_path}"

# setup maven env
# Do not remove below, this can be updated by the python scripts
export DEFAULT_MAVEN_REPO_URL="https://repository.apache.org/content/groups/public/"
# export MAVEN_REPO_URL=
cp "${MVN_MODULE}"/maven/settings.xml "${maven_settings_path}"
export MAVEN_SETTINGS_PATH="${maven_settings_path}"
source "${MVN_MODULE}"/added/configure-maven.sh
configure

export MAVEN_OPTIONS="${MAVEN_OPTIONS} -s ${maven_settings_path}"

# Add NPM registry if needed
if [ ! -z "${NPM_REGISTRY_URL}" ]; then
    echo "enabling npm repository: ${NPM_REGISTRY_URL}"
    npm_profile="\
<profile>\
<id>internal-npm-registry</id>\
<properties>\
<npmRegistryURL>${NPM_REGISTRY_URL}</npmRegistryURL>\
<yarnDownloadRoot>http://download.devel.redhat.com/rcm-guest/staging/rhba/dist/yarn/</yarnDownloadRoot>\
<nodeDownloadRoot>http://download.devel.redhat.com/rcm-guest/staging/rhba/dist/node/</nodeDownloadRoot>\
<npmDownloadRoot>http://download.devel.redhat.com/rcm-guest/staging/rhba/dist/npm/</npmDownloadRoot>\
<pnpmDownloadRoot>http://download.devel.redhat.com/rcm-guest/staging/rhba/dist/pnpm/</pnpmDownloadRoot>\
</properties>\
</profile>\
"   
    sed -i.bak -E "s|(<!-- ### extra maven repositories ### -->)|\1\n${npm_profile}|" "${MAVEN_SETTINGS_PATH}"
    sed -i.bak -E "s|(<!-- ### extra maven profile ### -->)|\1\n<activeProfile>internal-npm-registry</activeProfile>|" "${MAVEN_SETTINGS_PATH}"
    
    rm -rf "${MAVEN_SETTINGS_PATH}/*.bak"
fi

cat "${maven_settings_path}"

if [ "${MAVEN_IGNORE_SELF_SIGNED_CERTIFICATE}" = "true" ]; then
    export MAVEN_OPTIONS="${MAVEN_OPTIONS} -Denforcer.skip"
fi

