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

# shellcheck source=/dev/null
source "${script_dir_path}"/logging.sh

if [ "${SCRIPT_DEBUG}" = "true" ] ; then
    set -x
    export MAVEN_ARGS_APPEND="${MAVEN_ARGS_APPEND} -X --batch-mode"
    log_info "Script debugging is enabled, allowing bash commands and their arguments to be printed as they are executed"
    printenv
fi

source "${script_dir_path}"/configure-jvm-mvn.sh

"${MAVEN_CMD}" -B ${MAVEN_ARGS_APPEND} \
  -nsu \
  -B \
  -s "${MAVEN_SETTINGS_PATH}" \
  "${QUARKUS_PLATFORM_GROUPID}":quarkus-maven-plugin:"${QUARKUS_PLATFORM_VERSION}":create ${QUARKUS_CREATE_ARGS} \
  -DprojectGroupId="${PROJECT_GROUP_ID}" \
  -DprojectArtifactId="${PROJECT_ARTIFACT_ID}" \
  -DprojectVersionId="${PROJECT_VERSION}" \
  -DplatformVersion="${QUARKUS_PLATFORM_VERSION}" \
  -Dextensions="${QUARKUS_EXTENSIONS}"

cd "${PROJECT_ARTIFACT_ID}"

source "${script_dir_path}"/configure-jvm-mvn.sh

# Fix as we cannot rely on Quarkus platform
# Should be removed once https://issues.redhat.com/browse/KOGITO-9120 is implemented
if [ ! -z ${KOGITO_VERSION} ]; then
    echo "Replacing Kogito Platform BOM with version ${KOGITO_VERSION}"
    # [ ]* -> is a regexp pattern to match any number of spaces
    pattern_1="[ ]*<groupId>.*<\/groupId>"
    pattern_2="[ ]*<artifactId>quarkus-kogito-bom<\/artifactId>\n"
    pattern_3="[ ]*<version>.*<\/version>\n"
    complete_pattern="$pattern_1\n$pattern_2$pattern_3"

    replace_1="        <groupId>org.kie.kogito<\/groupId>\n"
    replace_2="        <artifactId>kogito-bom<\/artifactId>\n"
    replace_3="        <version>${KOGITO_VERSION}<\/version>\n"
    complete_replace="$replace_1$replace_2$replace_3"

    sed -i.bak -e "/$pattern_1/{
        N;N;N
        s/$complete_pattern/$complete_replace/
        }" pom.xml
fi

# if the image being built is X86_64, remove the arm64 maven dependencies from
# kogito-addons-quarkus-jobs-service-embedded and kogito-addons-quarkus-data-index-inmemory
# using maven exclusions
exclusion_jobs_service=""
pattern_jobs_service="<artifactId>kogito-addons-quarkus-jobs-service-embedded</artifactId>"
base_exclusions="<exclusion>\
          <groupId>io.zonky.test.postgres</groupId>\
          <artifactId>embedded-postgres-binaries-linux-amd64-alpine</artifactId>\
        </exclusion>\
        <exclusion>\
          <groupId>io.zonky.test.postgres</groupId>\
          <artifactId>embedded-postgres-binaries-darwin-amd64</artifactId>\
        </exclusion>\
        <exclusion>\
          <groupId>io.zonky.test.postgres</groupId>\
          <artifactId>embedded-postgres-binaries-darwin-arm64v8</artifactId>\
        </exclusion>\
        <exclusion>\
          <groupId>io.zonky.test.postgres</groupId>\
          <artifactId>embedded-postgres-binaries-windows-amd64</artifactId>\
        </exclusion>"

arch=$(uname -p)
if [ "${arch}" = "x86_64" ]; then
    echo "Removing arm64 dependencies from kogito-addons-quarkus-jobs-service-embedded and kogito-addons-quarkus-data-index-inmemory dependencies"
    exclusion_jobs_service="${pattern_jobs_service}\
     <exclusions>\
        $base_exclusions\
        <exclusion>\
          <groupId>io.zonky.test.postgres</groupId>\
          <artifactId>embedded-postgres-binaries-linux-arm64v8</artifactId>\
        </exclusion>\
     </exclusions>"

elif [ "${arch}" = "aarch64" ]; then
    echo "Removing amd64 dependencies from kogito-addons-quarkus-jobs-service-embedded and kogito-addons-quarkus-data-index-inmemory dependencies"
    exclusion_jobs_service="${pattern_jobs_service}\
     <exclusions>\
        $base_exclusions\
        <exclusion>\
          <groupId>io.zonky.test.postgres</groupId>\
          <artifactId>embedded-postgres-binaries-linux-amd64</artifactId>\
        </exclusion>\
     </exclusions>"
fi

# Do the replace if needed
if [ ! -z "${exclusion_jobs_service}" ]; then
    sed -i.bak "s|$pattern_jobs_service|$exclusion_jobs_service|" pom.xml
fi

if [ "${SCRIPT_DEBUG^^}" = "TRUE" ]; then
    cat pom.xml
fi

# we force the dependencies download beforehand, so we won't have problems when running or building our apps in offline mode
# see:
#   https://quarkus.io/guides/maven-tooling#downloading-maven-artifact-dependencies-for-offline-development-and-testing
#   https://maven.apache.org/plugins/maven-dependency-plugin/go-offline-mojo.html
"${MAVEN_CMD}" -B ${MAVEN_ARGS_APPEND} \
  -nsu \
  -B \
  -s "${MAVEN_SETTINGS_PATH}" \
  -DskipTests=true \
  -Dmaven.javadoc.skip=true \
  clean dependency:go-offline "${QUARKUS_PLATFORM_GROUPID}":quarkus-maven-plugin:"${QUARKUS_PLATFORM_VERSION}":go-offline install

# clean up
"${MAVEN_CMD}" -B ${MAVEN_ARGS_APPEND} \
  -nsu \
  -B \
  -s "${MAVEN_SETTINGS_PATH}" \
  clean
