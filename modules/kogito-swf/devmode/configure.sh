#!/usr/bin/env bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SOURCES_DIR=/tmp/artifacts
ADDED_DIR="${SCRIPT_DIR}"/added
LAUNCH_DIR="${KOGITO_HOME}"/launch

cp -v "${ADDED_DIR}"/* "${LAUNCH_DIR}"

# Unzip Quarkus app and Maven repository
unzip "${SOURCES_DIR}"/kogito-swf-devmode-quarkus-app.zip -d "${KOGITO_HOME}"
unzip "${SOURCES_DIR}"/kogito-swf-devmode-maven-repo.zip -d "${KOGITO_HOME}"/.m2/repository

chown -R 1001:0 "${KOGITO_HOME}"
chmod -R ug+rwX "${KOGITO_HOME}"
