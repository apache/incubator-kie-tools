#!/usr/bin/env bash
# Calculate the java memory for the given maven build.
# It is based in the container-limits that can be found in
# https://github.com/jboss-openshift/cct_module/blob/master/jboss/container/java/jvm/bash/artifacts/opt/jboss/container/java/jvm/container-limits
#
# It respects the jvm.config of ${maven.projectBasedir}/.mvn/jvm.config
# Parameters:
#  $1 - resource directory, base dir or maven.projectBasedir
script_name=`basename "$0" | cut -d"." -f1`
set -e

source "${KOGITO_HOME}"/launch/logging.sh

base_dir=$1

if [ "${base_dir}x" = "x" ]; then
  log_info "{$script_name} resource directory is empty..."
else
  log_info "{$script_name} checking if .mvn/jvm.config exists."
  if [ -f "${base_dir}/.mvn/jvm.config" ]; then
    log_info "{$script_name} .mvn/jvm.config exists."
    export JAVA_OPTIONS=$(cat ${base_dir}/.mvn/jvm.config)
  else
    log_info "{$script_name} .mvn/jvm.config does not exists, memory will be calculated based on container limits."
  fi
fi


echo ${JAVA_OPTIONS} $(${JBOSS_CONTAINER_JAVA_JVM_MODULE}/java-default-options) $(${JBOSS_CONTAINER_JAVA_JVM_MODULE}/debug-options)