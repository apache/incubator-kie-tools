#!/usr/bin/env bash
# Calculate the java memory for the given maven build.
# It is based in the container-limits that can be found in
# https://github.com/jboss-openshift/cct_module/blob/master/jboss/container/java/jvm/bash/artifacts/opt/jboss/container/java/jvm/container-limits
#
# It respects the jvm.config of ${maven.projectBasedir}/.mvn/jvm.config
# Usage: add the following line in the desired script:
#   source "${KOGITO_HOME}"/launch/jvm-settings.sh
# If you want to provide the jvm.config, be sure to access the target directory before sourcing this script.


set -e

source "${KOGITO_HOME}"/launch/logging.sh

log_info "--> checking if .mvn/jvm.config exists."
if [ -f ".mvn/jvm.config" ]; then
  log_info "---> .mvn/jvm.config exists."
  export JAVA_OPTIONS=$(cat .mvn/jvm.config)
else
  log_info "---> .mvn/jvm.config does not exists, memory will be calculated based on container limits."
fi

export MAVEN_OPTS="${JAVA_OPTIONS} $(${JBOSS_CONTAINER_JAVA_JVM_MODULE}/java-default-options) $(${JBOSS_CONTAINER_JAVA_JVM_MODULE}/debug-options)"