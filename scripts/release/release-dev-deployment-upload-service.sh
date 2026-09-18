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

# Dev Deployment Upload Service Release Script
#
# Builds the dev-deployment-upload-service Go binaries for all platforms and either:
#   --rc       : copies tarballs as Apache RC artifacts
#   --publish  : uploads tarballs to an existing GitHub Release
#
# RC artifact names (matching former_release.txt convention):
#   incubator-kie-<version>-incubating-sandbox-dev-deployment-upload-service-macOS-arm64.tar.gz
#   incubator-kie-<version>-incubating-sandbox-dev-deployment-upload-service-macOS-x86.tar.gz
#   incubator-kie-<version>-incubating-sandbox-dev-deployment-upload-service-linux-x86.tar.gz
#   incubator-kie-<version>-incubating-sandbox-dev-deployment-upload-service-windows-x86.tar.gz
#
# The package build produces per-platform tarballs at:
#   packages/dev-deployment-upload-service/dist/dev-deployment-upload-service-<os>-<arch>-<version>.tar.gz
#
# Usage:
#   ./release-dev-deployment-upload-service.sh <version>
#   ./release-dev-deployment-upload-service.sh <version> --rc
#   ./release-dev-deployment-upload-service.sh <version> --publish --upload-url <github-upload-url>
#
# Environment Variables (required for --publish):
#   GITHUB_TOKEN          Token with write access to the release

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

usage() {
    echo "Usage: $0 <version> [OPTIONS]"
    echo ""
    echo "Options:"
    echo "    --rc                  Create RC artifacts"
    echo "    --publish             Upload to GitHub Release"
    echo "    --upload-url <url>    GitHub Release upload URL (required for --publish)"
    echo "    --help"
    exit 1
}

VERSION=""
RC_MODE=false
PUBLISH=false
UPLOAD_URL=""

SKIP_BUILD=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --rc)         RC_MODE=true;        shift ;;
        --publish)    PUBLISH=true;        shift ;;
        --skip-build) SKIP_BUILD=true;     shift ;;
        --upload-url) UPLOAD_URL="$2";     shift 2 ;;
        --help)       usage ;;
        *)
            if [[ -z "$VERSION" ]]; then VERSION="$1"; else echo "Unknown argument: $1"; usage; fi
            shift ;;
    esac
done

[[ -z "$VERSION" ]] && { echo "ERROR: version is required"; usage; }

if [[ "$PUBLISH" == "true" ]]; then
    [[ -z "${GITHUB_TOKEN:-}" ]] && { echo "ERROR: GITHUB_TOKEN is required for --publish"; exit 1; }
    [[ -z "$UPLOAD_URL" ]] && { echo "ERROR: --upload-url is required for --publish"; exit 1; }
fi

echo "=========================================="
echo "Dev Deployment Upload Service Release"
echo "Version: $VERSION"
echo "RC mode: $RC_MODE"
echo "Publish: $PUBLISH"
echo "=========================================="
echo ""

cd "$REPO_ROOT"

if [[ "$SKIP_BUILD" == "true" ]]; then
    echo "Skipping build (--skip-build set)..."
else
    echo "Building dev-deployment-upload-service..."
    pnpm -F "@kie-tools/dev-deployment-upload-service..." build:prod
fi

DIST="$REPO_ROOT/packages/dev-deployment-upload-service/dist"

if [[ ! -d "$DIST" ]]; then
    if [[ "$SKIP_BUILD" == "true" ]]; then
        echo "SKIP: packages/dev-deployment-upload-service/dist not found — run without --skip-build or build Go binaries first"
        exit 0
    else
        echo "ERROR: packages/dev-deployment-upload-service/dist not found after build"
        exit 1
    fi
fi

ARTIFACTS_DIR="$REPO_ROOT/release-artifacts"

declare -A PLATFORM_MAP=(
    ["dev-deployment-upload-service-darwin-arm64-${VERSION}.tar.gz"]="apache-kie-${VERSION}-incubating-sandbox-dev-deployment-upload-service-macOS-arm64.tar.gz"
    ["dev-deployment-upload-service-darwin-amd64-${VERSION}.tar.gz"]="apache-kie-${VERSION}-incubating-sandbox-dev-deployment-upload-service-macOS-x86.tar.gz"
    ["dev-deployment-upload-service-linux-amd64-${VERSION}.tar.gz"]="apache-kie-${VERSION}-incubating-sandbox-dev-deployment-upload-service-linux-x86.tar.gz"
    ["dev-deployment-upload-service-windows-amd64-${VERSION}.tar.gz"]="apache-kie-${VERSION}-incubating-sandbox-dev-deployment-upload-service-windows-x86.tar.gz"
)

if [[ "$RC_MODE" == "true" ]]; then
    mkdir -p "$ARTIFACTS_DIR"

    for src_name in "${!PLATFORM_MAP[@]}"; do
        rc_name="${PLATFORM_MAP[$src_name]}"
        src="$DIST/$src_name"
        if [[ -f "$src" ]]; then
            echo "  COPY  $rc_name"
            cp "$src" "$ARTIFACTS_DIR/$rc_name"
        else
            echo "  SKIP  $src_name (not found in $DIST)"
        fi
    done

    echo "RC artifacts written to: $ARTIFACTS_DIR"
fi

if [[ "$PUBLISH" == "true" ]]; then
    upload_asset() {
        local file="$1"
        local asset_name="$2"
        if [[ ! -f "$file" ]]; then
            echo "  SKIP  $asset_name ($file not found)"
            return
        fi
        echo "  UPLOAD  $asset_name"
        curl -sSf \
            -H "Authorization: token ${GITHUB_TOKEN}" \
            -H "Content-Type: application/gzip" \
            --data-binary "@$file" \
            "${UPLOAD_URL}?name=${asset_name}"
    }

    for src_name in "${!PLATFORM_MAP[@]}"; do
        src="$DIST/$src_name"
        upload_asset "$src" "$src_name"
    done
fi

echo ""
echo "=========================================="
echo "DONE"
echo "=========================================="
