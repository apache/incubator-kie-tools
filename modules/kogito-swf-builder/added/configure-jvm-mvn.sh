#!/usr/bin/env bash
set -e

export MAVEN_ARGS_APPEND="${MAVEN_ARGS_APPEND} -Dmaven.compiler.release=${JAVA_VERSION}"

# Call the configure-maven here
source "${KOGITO_HOME}"/launch/configure-maven.sh
configure

# auto configure JVM settings
source "${KOGITO_HOME}"/launch/jvm-settings.sh