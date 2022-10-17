#!/usr/bin/env bash
set -e

script_dir_path="$(cd $(dirname "${BASH_SOURCE[0]}") && pwd)"
extensions="$1"

# Call the configure-maven here
source "${KOGITO_HOME}"/launch/configure-maven.sh
configure

source "${KOGITO_HOME}"/launch/logging.sh

if [ "${SCRIPT_DEBUG}" = "true" ] ; then
    set -x
    log_info "Script debugging is enabled, allowing bash commands and their arguments to be printed as they are executed"
    printenv
fi

cd "${KOGITO_HOME}/${PROJECT_ARTIFACT_ID}"

"${MAVEN_HOME}"/bin/mvn -U -B -s "${MAVEN_SETTINGS_PATH}" \
io.quarkus.platform:quarkus-maven-plugin:"${QUARKUS_VERSION}":add-extension ${QUARKUS_ADD_EXTENSION_ARGS}\
-DplatformVersion="${QUARKUS_VERSION}" \
-Dextensions="${extensions}"
