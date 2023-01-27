#!/usr/bin/env bash
set -e

source "${KOGITO_HOME}"/launch/logging.sh

if [ "${SCRIPT_DEBUG}" = "true" ] ; then
    set -x
    export MAVEN_ARGS_APPEND="${MAVEN_ARGS_APPEND} -X --batch-mode" 
    log_info "Script debugging is enabled, allowing bash commands and their arguments to be printed as they are executed"
    printenv
fi

# Call the configure-maven here
source "${KOGITO_HOME}"/launch/configure-maven.sh
configure

cd "${PROJECT_ARTIFACT_ID}"

if [ ! -z "${QUARKUS_EXTENSIONS}" ]; then
  ${KOGITO_HOME}/launch/add-extension.sh "${QUARKUS_EXTENSIONS}"
fi

# auto configure JVM settings
source "${KOGITO_HOME}"/launch/jvm-settings.sh

"${MAVEN_HOME}"/bin/mvn -U -B ${MAVEN_ARGS_APPEND} \
  -s "${MAVEN_SETTINGS_PATH}" \
  -DskipTests \
  -Dquarkus.http.host=0.0.0.0 \
  -Dquarkus.version="${QUARKUS_VERSION}" \
  clean compile quarkus:dev
