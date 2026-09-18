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

# Skip linting, tests, and e2e for all child builds — same flags the Jenkinsfile sets.
export KIE_TOOLS_BUILD__runLinters=false
export KIE_TOOLS_BUILD__runTests=false
export KIE_TOOLS_BUILD__runEndToEndTests=false

# Master release script — runs all component release scripts.
#
# Usage:
#   ./release-all.sh <version>                              # dry run (build only)
#   ./release-all.sh <version> --rc                        # create RC artifacts
#   ./release-all.sh <version> --publish                   # publish to all registries
#   ./release-all.sh <version> --rc --components npm,vscode  # subset only
#
# Components (comma-separated):
#   npm-packages, chrome-extensions, vscode, container-images,
#   helm-charts, github-pages, kn-plugin-workflow, dev-deployment-upload-service,
#   source-tarball

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"

VERSION="${1:-}"
PUBLISH_FLAG=""
RC_FLAG=""
SKIP_BUILD_FLAG=""
COMPONENTS="npm-packages,chrome-extensions,vscode,container-images,helm-charts,github-pages,kn-plugin-workflow,dev-deployment-upload-service,source-tarball"

shift || true
while [[ $# -gt 0 ]]; do
    case $1 in
        --publish)
            PUBLISH_FLAG="--publish"
            shift
            ;;
        --rc)
            RC_FLAG="--rc"
            shift
            ;;
        --skip-build)
            SKIP_BUILD_FLAG="--skip-build"
            shift
            ;;
        --components)
            COMPONENTS="${2:-}"
            shift 2
            ;;
        *)
            echo "Unknown option: $1"
            exit 1
            ;;
    esac
done

if [[ -z "${VERSION}" ]]; then
    echo "Usage: $0 <version> [--publish] [--rc] [--skip-build] [--components <list>]"
    echo "Example: $0 10.2.0 --rc"
    echo "Example: $0 10.2.0 --rc --skip-build   # assumes repo already built"
    echo "Example: $0 10.2.0 --publish"
    echo "Example: $0 10.2.0 --rc --components npm-packages,vscode"
    exit 1
fi

echo "=========================================="
echo "KIE Tools Release — All Components"
echo "Version:      ${VERSION}"
echo "Publish:      ${PUBLISH_FLAG:-false}"
echo "RC mode:      ${RC_FLAG:-false}"
echo "Skip build:   ${SKIP_BUILD_FLAG:+true}${SKIP_BUILD_FLAG:-false}"
echo "Components:   ${COMPONENTS}"
echo "=========================================="

cd "${REPO_ROOT}"

FAILED_COMPONENTS=()

release_component() {
    local component=$1
    local script="${SCRIPT_DIR}/release-${component}.sh"

    if [[ "${component}" == "source-tarball" ]]; then
        script="${SCRIPT_DIR}/create-source-tarball.sh"
    fi

    if [[ ! -f "${script}" ]]; then
        echo "⚠  Script not found: ${script} (skipping)"
        FAILED_COMPONENTS+=("${component}")
        return 1
    fi

    echo ""
    echo "=========================================="
    echo "Releasing: ${component}"
    echo "=========================================="

    local flags="${PUBLISH_FLAG} ${RC_FLAG} ${SKIP_BUILD_FLAG}"
    if [[ "${component}" == "source-tarball" ]]; then
        flags=""
    fi

    if "${script}" "${VERSION}" ${flags}; then
        echo "✅ ${component} — SUCCESS"
    else
        echo "❌ ${component} — FAILED"
        FAILED_COMPONENTS+=("${component}")
    fi
}

IFS=',' read -ra COMPONENT_ARRAY <<< "${COMPONENTS}"
for component in "${COMPONENT_ARRAY[@]}"; do
    component=$(echo "${component}" | xargs)
    release_component "${component}" || true
done

echo ""
echo "=========================================="
echo "Release Summary"
echo "=========================================="

if [[ ${#FAILED_COMPONENTS[@]} -eq 0 ]]; then
    echo "✅ All components released successfully!"
    exit 0
else
    echo "❌ Failed components:"
    for c in "${FAILED_COMPONENTS[@]}"; do
        echo "  - ${c}"
    done
    exit 1
fi
