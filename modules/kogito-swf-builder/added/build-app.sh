#!/usr/bin/env bash
set -e

script_dir_path="$(cd $(dirname "${BASH_SOURCE[0]}") && pwd)"
resources_path="$1"
if [ ! -z "${resources_path}" ]; then
  resources_path="$(realpath "${resources_path}")"
fi

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

if [ ! -z "${QUARKUS_EXTENSIONS}" ]; then
  ${script_dir_path}/add-extension.sh "${QUARKUS_EXTENSIONS}"
fi

# Copy resources if exists
if [ ! -z "${resources_path}" ]; then
  if [ -d "${resources_path}" ]; then
    cp -rv "${resources_path}"/* src/main/resources/
  else
    cp -rv "${resources_path}" src/main/resources/
  fi
fi

"${MAVEN_HOME}"/bin/mvn ${MAVEN_ARGS_APPEND} -U -B clean install -DskipTests -s "${MAVEN_SETTINGS_PATH}" -Dquarkus.container-image.build=false
