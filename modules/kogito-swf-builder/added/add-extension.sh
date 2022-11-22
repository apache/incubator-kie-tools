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

"${MAVEN_HOME}"/bin/mvn -U -B -s "${MAVEN_SETTINGS_PATH}" \
io.quarkus.platform:quarkus-maven-plugin:"${QUARKUS_VERSION}":add-extension ${QUARKUS_ADD_EXTENSION_ARGS}\
-DplatformVersion="${QUARKUS_VERSION}" \
-Dextensions="${extensions}"
