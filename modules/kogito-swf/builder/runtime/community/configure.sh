#!/usr/bin/env bash
set -e

SOURCES_DIR=/tmp/artifacts

mkdir -p "${KOGITO_HOME}"/.m2/repository

# Unzip Quarkus app and Maven repository
unzip "${SOURCES_DIR}"/kogito-swf-quarkus-app.zip -d "${KOGITO_HOME}"
unzip "${SOURCES_DIR}"/kogito-swf-maven-repo.zip -d "${KOGITO_HOME}"/.m2/repository

chown -R 1001:0 "${KOGITO_HOME}"
chmod -R ug+rwX "${KOGITO_HOME}"
