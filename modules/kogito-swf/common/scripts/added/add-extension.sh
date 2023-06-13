#!/usr/bin/env bash
set -e

script_dir_path="$(cd $(dirname "${BASH_SOURCE[0]}") && pwd)"
# extensions to be added, comma separated.
extensions="$1"
# parameter passed which will trigger or not the jvm/maven configuration.
ignore_jvm_settings=${2:-false}

source "${script_dir_path}"/logging.sh

if [ "${SCRIPT_DEBUG}" = "true" ] ; then
    set -x
    export MAVEN_ARGS_APPEND="${MAVEN_ARGS_APPEND} -X --batch-mode"
    log_info "Script debugging is enabled, allowing bash commands and their arguments to be printed as they are executed"
    printenv
fi

if [ "${ignore_jvm_settings}" != "true" ]; then
    source "${script_dir_path}"/configure-jvm-mvn.sh
fi

"${MAVEN_HOME}"/bin/mvn -B ${MAVEN_ARGS_APPEND} \
    -nsu \
    -s "${MAVEN_SETTINGS_PATH}" \
    -DplatformVersion="${QUARKUS_PLATFORM_VERSION}" \
    -Dextensions="${extensions}" \
    ${QUARKUS_ADD_EXTENSION_ARGS} \
    io.quarkus.platform:quarkus-maven-plugin:"${QUARKUS_PLATFORM_VERSION}":add-extension
