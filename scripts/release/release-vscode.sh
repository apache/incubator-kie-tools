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

# VSCode Extensions Release Script
# Builds VSCode extensions and optionally publishes them to the VSCode Marketplace.
#
# Usage:
#   ./release-vscode.sh <version>               # build only (dry run)
#   ./release-vscode.sh <version> --rc          # build + collect .vsix as RC artifacts
#   ./release-vscode.sh <version> --publish     # build + publish to marketplace (requires VSCE_PAT)

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"

VERSION="${1:-}"
PUBLISH=false
RC_MODE=false
SKIP_BUILD=false

shift || true
while [[ $# -gt 0 ]]; do
    case $1 in
        --publish)    PUBLISH=true;    shift ;;
        --rc)         RC_MODE=true;    shift ;;
        --skip-build) SKIP_BUILD=true; shift ;;
        *) echo "Unknown option: $1"; exit 1 ;;
    esac
done

if [[ -z "${VERSION}" ]]; then
    echo "Usage: $0 <version> [--publish] [--rc] [--skip-build]"
    exit 1
fi

echo "=========================================="
echo "VSCode Extensions Release"
echo "Version: ${VERSION}"
echo "RC mode: ${RC_MODE}"
echo "Publish: ${PUBLISH}"
echo "=========================================="

cd "${REPO_ROOT}"

# Map: package-dir → canonical RC artifact name suffix (apache-kie-<version>-incubating-<suffix>.vsix)
declare -A EXTENSIONS=(
    ["bpmn-vscode-extension"]="bpmn-vscode-extension"
    ["dmn-vscode-extension"]="dmn-vscode-extension"
    ["drl-vscode-extension"]="drl-vscode-extension"
    ["pmml-vscode-extension"]="pmml-vscode-extension"
    ["vscode-extension-kogito-bundle"]="kogito-bundle-vscode-extension"
    ["vscode-extension-kie-ba-bundle"]="business-automation-bundle-vscode-extension"
    ["extended-services-vscode-extension"]="extended-services-vscode-extension"
)

FILTER=""
for ext in "${!EXTENSIONS[@]}"; do
    FILTER="$FILTER -F $ext..."
done

if [[ "${SKIP_BUILD}" == "true" ]]; then
    echo "Skipping build (--skip-build set)..."
else
    echo "Building VSCode extensions..."
    pnpm ${FILTER} build:prod
fi

ARTIFACTS_DIR="${REPO_ROOT}/release-artifacts"

if [[ "${RC_MODE}" == "true" ]]; then
    echo "Collecting .vsix files as RC artifacts..."
    mkdir -p "${ARTIFACTS_DIR}"
    for ext in "${!EXTENSIONS[@]}"; do
        suffix="${EXTENSIONS[$ext]}"
        vsix=$(find "packages/$ext" -maxdepth 2 -name "*.vsix" 2>/dev/null | head -1)
        if [[ -n "$vsix" ]]; then
            dest="${ARTIFACTS_DIR}/apache-kie-${VERSION}-incubating-${suffix}.vsix"
            echo "  COPY  $(basename "$dest")"
            cp "$vsix" "$dest"
        else
            echo "  SKIP  $ext (no .vsix found)"
        fi
    done
    echo "VSIX files collected to: ${ARTIFACTS_DIR}/"
    ls "${ARTIFACTS_DIR}"/*.vsix 2>/dev/null || echo "  (no .vsix files found — build may have failed)"
fi

if [[ "${PUBLISH}" == "true" ]]; then
    if [[ -z "${VSCE_PAT:-}" ]]; then
        echo "ERROR: VSCE_PAT environment variable required for publishing"
        exit 1
    fi

    echo "Publishing to VSCode Marketplace..."
    for ext in "${!EXTENSIONS[@]}"; do
        find "packages/$ext" -maxdepth 2 -name "*.vsix" | while read -r vsix; do
            echo "  Publishing: ${vsix}"
            npx vsce publish --packagePath "${vsix}" --pat "${VSCE_PAT}"
        done
    done
    echo "VSCode extensions published!"
else
    echo "Dry run — extensions built:"
    for ext in "${!EXTENSIONS[@]}"; do
        find "packages/$ext" -maxdepth 2 -name "*.vsix" 2>/dev/null || true
    done
fi

echo "=========================================="
echo "DONE"
echo "=========================================="
