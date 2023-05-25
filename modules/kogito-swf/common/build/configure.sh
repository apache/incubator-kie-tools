#!/usr/bin/env bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ADDED_DIR="${SCRIPT_DIR}"/added
LAUNCH_DIR="${KOGITO_HOME}"/launch
BUILD_DIR="${KOGITO_HOME}"/build

mkdir -p "${BUILD_DIR}"
cp -v "${ADDED_DIR}"/* "${BUILD_DIR}"

chown -R 1001:0 "${KOGITO_HOME}"
chmod -R ug+rwX "${KOGITO_HOME}"

cd "${KOGITO_HOME}"

# Create app
"${LAUNCH_DIR}"/create-app.sh

"${BUILD_DIR}"/cleanup_project.sh
"${BUILD_DIR}"/zip_files.sh

chown -R 1001:0 "${KOGITO_HOME}"
chmod -R ug+rwX "${KOGITO_HOME}"