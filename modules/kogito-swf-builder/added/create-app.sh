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

"${MAVEN_HOME}"/bin/mvn -U -B -s "${MAVEN_SETTINGS_PATH}" \
  io.quarkus.platform:quarkus-maven-plugin:"${QUARKUS_VERSION}":create ${QUARKUS_CREATE_ARGS} \
  -DprojectGroupId="${PROJECT_GROUP_ID}" \
  -DprojectArtifactId="${PROJECT_ARTIFACT_ID}" \
  -DprojectVersionId="${PROJECT_VERSION}" \
  -DplatformVersion="${QUARKUS_VERSION}" \
  -Dextensions="${QUARKUS_EXTENSIONS}"

cd "${PROJECT_ARTIFACT_ID}"

# Quarkus version is enforced if some dependency pulled has older version of Quarkus set.
# This avoids to have, for example, Quarkus BOMs or orther artifacts with multiple versions.
"${MAVEN_HOME}"/bin/mvn -U -B ${MAVEN_ARGS_APPEND} \
  -s "${MAVEN_SETTINGS_PATH}" \
  -Dquarkus.version="${QUARKUS_VERSION}" \
  -DskipTests \
  -Dquarkus.container-image.build=false \
   clean install
