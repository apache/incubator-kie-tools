#!/usr/bin/env bash
set -e

script_dir_path="$(cd $(dirname "${BASH_SOURCE[0]}") && pwd)"
resources_path="$1"
if [ -n "${resources_path}" ]; then
    resources_path="$(realpath "${resources_path}")"
fi

source "${script_dir_path}"/logging.sh

if [ "${SCRIPT_DEBUG}" = "true" ] ; then
    set -x
    export MAVEN_ARGS_APPEND="${MAVEN_ARGS_APPEND} -X --batch-mode" 
    log_info "Script debugging is enabled, allowing bash commands and their arguments to be printed as they are executed"
    log_info "Resources path is ${resources_path}"
    printenv
fi

# Copy resources if exists
log_info "-> Copying files from ${resources_path}, if any..."
if [ -n "${resources_path}" ]; then
    destination="${KOGITO_HOME}/serverless-workflow-project/src/main/resources/"
    log_info "-> Destination folder is ${destination}"
    cp -vR ${resources_path}/* ${destination}
    find "${resources_path}" -name 'jvm.config' -exec echo "--> found {}" \; -exec mkdir -p  ${destination}/.mvn \; -exec cp -v {} ${destination}/.mvn/ \;
else
    log_warning "-> Nothing to copy from ${resources_path}"
fi

source "${script_dir_path}"/configure-jvm-mvn.sh

if [ ! -z "${QUARKUS_EXTENSIONS}" ]; then
    log_info "Adding extensions '${QUARKUS_EXTENSIONS}'"
    ${script_dir_path}/add-extension.sh "${QUARKUS_EXTENSIONS}" "true"
fi

cd ${KOGITO_HOME}/serverless-workflow-project

"${MAVEN_HOME}"/bin/mvn -B ${MAVEN_ARGS_APPEND} \
    -nsu \
    -s "${MAVEN_SETTINGS_PATH}" \
    -DskipTests \
    -Dquarkus.container-image.build=false \
    clean install
