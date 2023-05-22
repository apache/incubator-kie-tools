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

source "${script_dir_path}"/configure-jvm-mvn.sh

cd serverless-workflow-project

offline_param="-o"
if [ ! -z "${QUARKUS_EXTENSIONS}" ]; then
  ${KOGITO_HOME}/launch/add-extension.sh "${QUARKUS_EXTENSIONS}"
  offline_param=""
fi


# `-o` means offline mode
"${MAVEN_HOME}"/bin/mvn -B ${MAVEN_ARGS_APPEND} \
  ${offline_param} \
  -s "${MAVEN_SETTINGS_PATH}" \
  -DskipTests \
  -Dquarkus.http.host=0.0.0.0 \
  clean compile quarkus:dev
