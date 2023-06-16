#!/usr/bin/env bash
set -e

SOURCES_DIR=/tmp/artifacts

mkdir -p "${KOGITO_HOME}"/.m2/repository

# Unzip Quarkus app and Maven repository
unzip "${SOURCES_DIR}"/kogito-builder-quarkus-app-image-build.zip -d "${KOGITO_HOME}"
unzip "${SOURCES_DIR}"/kogito-builder-maven-repository-image-build.zip -d "${KOGITO_HOME}"/.m2/repository

chown -R 1001:0 "${KOGITO_HOME}"
chmod -R ug+rwX "${KOGITO_HOME}"
