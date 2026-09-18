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

# kn-plugin-workflow Release Script
#
# Builds the kn-plugin-workflow Go binaries for all platforms and either:
#   --rc       : zips each binary for the Apache RC artifact set
#   --publish  : uploads raw binaries to an existing GitHub Release
#
# RC artifact names (matching former_release.txt convention):
#   incubator-kie-<version>-incubating-sonataflow-knative-plugin-linux-x86.zip
#   incubator-kie-<version>-incubating-sonataflow-knative-plugin-macOS-arm64.zip
#   incubator-kie-<version>-incubating-sonataflow-knative-plugin-macOS-x86.zip
#   incubator-kie-<version>-incubating-sonataflow-knative-plugin-windows-x86.zip
#
# GitHub Release assets (matching old Jenkinsfile.kn-plugin-workflow):
#   kn-workflow-linux-amd64-<version>
#   kn-workflow-darwin-amd64-<version>
#   kn-workflow-darwin-arm64-<version>
#   kn-workflow-windows-amd64-<version>.exe
#
# Usage:
#   ./release-kn-plugin-workflow.sh <version>
#   ./release-kn-plugin-workflow.sh <version> --rc
#   ./release-kn-plugin-workflow.sh <version> --publish --upload-url <github-upload-url>
#
# Environment Variables (required for --publish):
#   GITHUB_TOKEN          Token with write access to the release

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

usage() {
    echo "Usage: $0 <version> [OPTIONS]"
    echo ""
    echo "Options:"
    echo "    --rc                  Create RC zip artifacts"
    echo "    --publish             Upload binaries to GitHub Release"
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
echo "kn-plugin-workflow Release"
echo "Version: $VERSION"
echo "RC mode: $RC_MODE"
echo "Publish: $PUBLISH"
echo "=========================================="
echo ""

cd "$REPO_ROOT"

if [[ "$SKIP_BUILD" == "true" ]]; then
    echo "Skipping build (--skip-build set)..."
else
    echo "Building kn-plugin-workflow..."
    pnpm -F "@kie-tools/kn-plugin-workflow..." build:prod
fi

DIST="$REPO_ROOT/packages/kn-plugin-workflow/dist"

if [[ ! -d "$DIST" ]]; then
    if [[ "$SKIP_BUILD" == "true" ]]; then
        echo "SKIP: packages/kn-plugin-workflow/dist not found — run without --skip-build or build Go binaries first"
        exit 0
    else
        echo "ERROR: packages/kn-plugin-workflow/dist not found after build"
        exit 1
    fi
fi

ARTIFACTS_DIR="$REPO_ROOT/release-artifacts"

if [[ "$RC_MODE" == "true" ]]; then
    mkdir -p "$ARTIFACTS_DIR"
    cd "$DIST"

    declare -A RC_ZIPS=(
        ["kn-workflow-linux-amd64"]="apache-kie-$VERSION-incubating-sonataflow-knative-plugin-linux-x86.zip"
        ["kn-workflow-darwin-amd64"]="apache-kie-$VERSION-incubating-sonataflow-knative-plugin-macOS-x86.zip"
        ["kn-workflow-darwin-arm64"]="apache-kie-$VERSION-incubating-sonataflow-knative-plugin-macOS-arm64.zip"
        ["kn-workflow-windows-amd64.exe"]="apache-kie-$VERSION-incubating-sonataflow-knative-plugin-windows-x86.zip"
    )

    for binary in "${!RC_ZIPS[@]}"; do
        zip_name="${RC_ZIPS[$binary]}"
        if [[ -f "$binary" ]]; then
            echo "  ZIP  $zip_name"
            zip "$ARTIFACTS_DIR/$zip_name" "$binary"
        else
            echo "  SKIP $binary (not found in $DIST)"
        fi
    done

    echo "RC artifacts written to: $ARTIFACTS_DIR"
    cd "$REPO_ROOT"
fi

if [[ "$PUBLISH" == "true" ]]; then
    cd "$DIST"

    upload_asset() {
        local file="$1"
        local asset_name="$2"
        local content_type="${3:-application/octet-stream}"
        if [[ ! -f "$file" ]]; then
            echo "  SKIP  $asset_name ($file not found)"
            return
        fi
        echo "  UPLOAD  $asset_name"
        curl -sSf \
            -H "Authorization: token ${GITHUB_TOKEN}" \
            -H "Content-Type: $content_type" \
            --data-binary "@$file" \
            "${UPLOAD_URL}?name=${asset_name}"
    }

    upload_asset "kn-workflow-linux-amd64"      "kn-workflow-linux-amd64-$VERSION"
    upload_asset "kn-workflow-darwin-amd64"     "kn-workflow-darwin-amd64-$VERSION"
    upload_asset "kn-workflow-darwin-arm64"     "kn-workflow-darwin-arm64-$VERSION"
    upload_asset "kn-workflow-windows-amd64.exe" "kn-workflow-windows-amd64-$VERSION.exe"

    cd "$REPO_ROOT"
fi

echo ""
echo "=========================================="
echo "DONE"
echo "=========================================="
