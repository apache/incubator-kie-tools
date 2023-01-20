#!/usr/bin/env bash
set -e

script_dir_path="$(cd $(dirname "${BASH_SOURCE[0]}") && pwd)"
extensions="$1"

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

cd "${PROJECT_ARTIFACT_ID}"
# auto configure JVM settings
source "${KOGITO_HOME}"/launch/jvm-settings.sh

"${MAVEN_HOME}"/bin/mvn -U -B ${MAVEN_ARGS_APPEND} \
  -s "${MAVEN_SETTINGS_PATH}" \
  -Dquarkus.version="${QUARKUS_VERSION}" \
  -DplatformVersion="${QUARKUS_VERSION}" \
  -Dextensions="${extensions}" \
  ${QUARKUS_ADD_EXTENSION_ARGS} \
  io.quarkus.platform:quarkus-maven-plugin:"${QUARKUS_VERSION}":add-extension
