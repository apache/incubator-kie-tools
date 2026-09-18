#!/usr/bin/env bash
#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

set -euo pipefail

export KIE_TOOLS_BUILD__runLinters=false
export KIE_TOOLS_BUILD__runTests=false
export KIE_TOOLS_BUILD__runEndToEndTests=false

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"

VERSION="${1:-}"
PUBLISH=false
RC_MODE=false
SKIP_BUILD=false

shift || true
while [[ $# -gt 0 ]]; do
    case $1 in
        --publish)    PUBLISH=true;     shift ;;
        --rc)         RC_MODE=true;     shift ;;
        --skip-build) SKIP_BUILD=true;  shift ;;
        *)
            echo "Unknown option: $1"
            exit 1
            ;;
    esac
done

if [[ -z "${VERSION}" ]]; then
    echo "Usage: $0 <version> [--publish] [--rc] [--skip-build]"
    echo "Example: $0 1.0.0 --publish"
    echo "Example: $0 1.0.0 --rc"
    exit 1
fi

echo "=========================================="
echo "NPM Packages Release"
echo "Version: ${VERSION}"
echo "Publish: ${PUBLISH}"
echo "RC mode: ${RC_MODE}"
echo "=========================================="

cd "${REPO_ROOT}"

if [[ "${SKIP_BUILD}" == "true" ]]; then
    echo "Skipping build (--skip-build set)..."
else
    echo "Building public NPM packages..."
    FILTER=$(pnpm -r exec bash -c 'if [[ $(jq -r ".private" package.json) != "true" ]]; then echo "-F $(jq -r ".name" package.json)..."; fi')
    pnpm ${FILTER} build:prod
fi

if [[ "${RC_MODE}" == "true" ]]; then
    echo "Creating release candidate artifacts..."
    ARTIFACTS_DIR="${REPO_ROOT}/release-artifacts"
    TMP_DIR="${ARTIFACTS_DIR}/npm-packages-tmp"
    ZIP_FILE="apache-kie-${VERSION}-incubating-tools-npm-packages.zip"

    mkdir -p "${TMP_DIR}" "${ARTIFACTS_DIR}"

    PUBLISH_FILTER=$(pnpm -r exec bash -c 'if [[ $(jq -r ".private" package.json) != "true" ]]; then echo "-F $(jq -r ".name" package.json)"; fi')
    pnpm ${PUBLISH_FILTER} exec bash -c "pnpm pack --pack-destination ${TMP_DIR}"

    (cd "${TMP_DIR}" && zip -r "${ARTIFACTS_DIR}/${ZIP_FILE}" .)
    rm -rf "${TMP_DIR}"

    echo "RC artifact created: ${ARTIFACTS_DIR}/${ZIP_FILE}"
fi

if [[ "${PUBLISH}" == "true" ]]; then
    echo "Publishing to NPM registry..."

    if [[ -z "${NPM_TOKEN:-}" ]]; then
        echo "ERROR: NPM_TOKEN environment variable required"
        exit 1
    fi

    echo "//registry.npmjs.org/:_authToken=${NPM_TOKEN}" > ~/.npmrc

    PUBLISH_FILTER=$(pnpm -r exec bash -c 'if [[ $(jq -r ".private" package.json) != "true" ]]; then echo "-F $(jq -r ".name" package.json)"; fi')

    pnpm ${PUBLISH_FILTER} exec bash -c '
        PKG_NAME=$(jq -r ".name" package.json)
        if ! npm view ${PKG_NAME}@'"${VERSION}"' name &>/dev/null; then
            echo "Publishing ${PKG_NAME}@'"${VERSION}"'"
            pnpm publish --no-git-checks --access public
        else
            echo "Skipping ${PKG_NAME}@'"${VERSION}"' (already published)"
        fi
    '

    echo "NPM packages published!"
fi

if [[ "${RC_MODE}" == "false" && "${PUBLISH}" == "false" ]]; then
    echo "Dry run - skipping publish"
fi

echo "=========================================="
echo "DONE"
echo "=========================================="