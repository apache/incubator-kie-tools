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

# copy .mvn/jvm-config from resources to project's base dir.
find . -maxdepth 5 -name  'jvm.config' -exec echo "--> found {}" \; -exec mkdir -p .mvn \; -exec cp -v {} .mvn/ \;
source "${script_dir_path}"/configure-jvm-mvn.sh

# `-o` means offline mode
offline_param="-o"
if [ ! -z "${QUARKUS_EXTENSIONS}" ]; then
    ${KOGITO_HOME}/launch/add-extension.sh "${QUARKUS_EXTENSIONS}" "true"
    offline_param=""
fi

"${MAVEN_HOME}"/bin/mvn -B ${MAVEN_ARGS_APPEND} \
    ${offline_param} \
    -s "${MAVEN_SETTINGS_PATH}" \
    -DskipTests \
    -Dquarkus.http.host=0.0.0.0 \
    -Dquarkus.test.continuous-testing=${QUARKUS_CONTINUOUS_TESTING:-disabled} \
    clean compile quarkus:dev
