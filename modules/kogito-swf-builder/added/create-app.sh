#!/usr/bin/env bash
set -e

script_dir_path="$(cd $(dirname "${BASH_SOURCE[0]}") && pwd)"

source "${script_dir_path}"/logging.sh

if [ "${SCRIPT_DEBUG}" = "true" ] ; then
    set -x
    export MAVEN_ARGS_APPEND="${MAVEN_ARGS_APPEND} -X --batch-mode" 
    log_info "Script debugging is enabled, allowing bash commands and their arguments to be printed as they are executed"
    printenv
fi

# Call the configure-maven here
source "${script_dir_path}"/configure-maven.sh
configure

# auto configure JVM settings
source "${KOGITO_HOME}"/launch/jvm-settings.sh

"${MAVEN_HOME}"/bin/mvn -B -s "${MAVEN_SETTINGS_PATH}" \
  -nsu \
  io.quarkus.platform:quarkus-maven-plugin:"${QUARKUS_PLATFORM_VERSION}":create ${QUARKUS_CREATE_ARGS} \
  -DprojectGroupId="${PROJECT_GROUP_ID}" \
  -DprojectArtifactId="${PROJECT_ARTIFACT_ID}" \
  -DprojectVersionId="${PROJECT_VERSION}" \
  -DplatformVersion="${QUARKUS_PLATFORM_VERSION}" \
  -Dextensions="${QUARKUS_EXTENSIONS}"

cd "${PROJECT_ARTIFACT_ID}"

"${MAVEN_HOME}"/bin/mvn -B ${MAVEN_ARGS_APPEND} \
  -nsu \
  -s "${MAVEN_SETTINGS_PATH}" \
  -DskipTests \
  -Dquarkus.container-image.build=false \
   clean install
