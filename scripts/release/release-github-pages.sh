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

# GitHub Pages + Webapp/Accelerator Release Script
#
# Covers three distinct publish targets:
#
#   1. GitHub Pages (incubator-kie-kogito-online @ gh-pages)
#      - KIE Sandbox webapp  (packages/online-editor/dist → sandbox/<version>/)
#      - KIE editors CDN     (packages/kie-editors-standalone/dist → editors/<version>/)
#      - Chrome ext editors  (packages/chrome-extension-pack-kogito-kie-editors/dist)
#
#   2. KIE Sandbox webapp RC zip  (incubator-kie-<version>-sandbox-webapp.zip)
#
#   3. KIE Sandbox Quarkus Accelerator
#      - RC: zip packages/kie-sandbox-accelerator-quarkus/dist → incubator-kie-<version>-sandbox-accelerator-quarkus.zip
#      - Publish: push dist/git-repo-content to apache/incubator-kie-sandbox-quarkus-accelerator @ <version> tag
#
# Usage:
#   ./release-github-pages.sh <version>              # dry run (prepare locally, no push)
#   ./release-github-pages.sh <version> --rc         # create RC zip artifacts locally
#   ./release-github-pages.sh <version> --publish    # push to GitHub Pages + accelerator repo

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

usage() {
    echo "Usage: $0 <version> [OPTIONS]"
    echo ""
    echo "Options:"
    echo "    --publish      Push to GitHub Pages and accelerator repo"
    echo "    --rc           Create RC zip artifacts (no push)"
    echo "    --help"
    echo ""
    echo "Environment Variables (required for --publish):"
    echo "    GITHUB_TOKEN   Personal access token with push access"
    exit 1
}

VERSION=""
PUBLISH=false
RC_MODE=false
KOGITO_ONLINE_REPO="https://github.com/apache/incubator-kie-kogito-online.git"
ACCELERATOR_REPO="https://github.com/apache/incubator-kie-sandbox-quarkus-accelerator.git"
GH_PAGES_BRANCH="gh-pages"

SKIP_BUILD=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --publish)    PUBLISH=true;    shift ;;
        --rc)         RC_MODE=true;    shift ;;
        --skip-build) SKIP_BUILD=true; shift ;;
        --help)       usage ;;
        *)
            if [[ -z "$VERSION" ]]; then VERSION="$1"; else echo "Unknown argument: $1"; usage; fi
            shift ;;
    esac
done

[[ -z "$VERSION" ]] && { echo "ERROR: version is required"; usage; }

if [[ "$PUBLISH" == "true" && -z "${GITHUB_TOKEN:-}" ]]; then
    echo "ERROR: GITHUB_TOKEN is required for --publish"
    exit 1
fi

echo "=========================================="
echo "GitHub Pages / Webapp / Accelerator Release"
echo "Version: $VERSION"
echo "RC mode: $RC_MODE"
echo "Publish: $PUBLISH"
echo "=========================================="
echo ""

ARTIFACTS_DIR="$REPO_ROOT/release-artifacts"
TEMP_DIR=$(mktemp -d)
trap "rm -rf $TEMP_DIR" EXIT

cd "$REPO_ROOT"

if [[ "$SKIP_BUILD" == "true" ]]; then
    echo "Skipping build (--skip-build set)..."
else
    echo "Building online-editor, kie-editors-standalone, chrome-extension-pack-kogito-kie-editors, kie-sandbox-accelerator-quarkus..."
    export ONLINE_EDITOR__buildInfo="$VERSION"
    export ONLINE_EDITOR__extendedServicesCompatibleVersion="$VERSION"
    export ONLINE_EDITOR__devDeploymentBaseImageRegistry="docker.io"
    export ONLINE_EDITOR__devDeploymentBaseImageAccount="apache"
    export ONLINE_EDITOR__devDeploymentBaseImageName="incubator-kie-sandbox-dev-deployment-base"
    export ONLINE_EDITOR__devDeploymentBaseImageTag="$VERSION"
    export ONLINE_EDITOR__devDeploymentDmnFormWebappImageRegistry="docker.io"
    export ONLINE_EDITOR__devDeploymentDmnFormWebappImageAccount="apache"
    export ONLINE_EDITOR__devDeploymentDmnFormWebappImageName="incubator-kie-sandbox-dev-deployment-dmn-form-webapp"
    export ONLINE_EDITOR__devDeploymentDmnFormWebappImageTag="$VERSION"
    export ONLINE_EDITOR__devDeploymentQuarkusBlankAppImageRegistry="docker.io"
    export ONLINE_EDITOR__devDeploymentQuarkusBlankAppImageAccount="apache"
    export ONLINE_EDITOR__devDeploymentQuarkusBlankAppImageName="incubator-kie-sandbox-dev-deployment-quarkus-blank-app"
    export ONLINE_EDITOR__devDeploymentQuarkusBlankAppImageTag="$VERSION"

    pnpm \
        -F "@kie-tools/online-editor..." \
        -F "@kie-tools/kie-editors-standalone..." \
        -F "@kie-tools/chrome-extension-pack-kogito-kie-editors..." \
        -F "@kie-tools/kie-sandbox-accelerator-quarkus..." \
        build:prod
fi

if [[ "$RC_MODE" == "true" ]]; then
    mkdir -p "$ARTIFACTS_DIR"

    SANDBOX_SRC="$REPO_ROOT/packages/online-editor/dist"
    if [[ -d "$SANDBOX_SRC" ]]; then
        SANDBOX_ZIP="$ARTIFACTS_DIR/apache-kie-$VERSION-incubating-sandbox-webapp.zip"
        echo "Creating RC artifact: $(basename "$SANDBOX_ZIP")"
        (cd "$SANDBOX_SRC" && zip -r "$SANDBOX_ZIP" .)
    else
        echo "WARN: packages/online-editor/dist not found — skipping sandbox-webapp zip"
    fi

    STANDALONE_SRC="$REPO_ROOT/packages/kie-editors-standalone/dist"
    if [[ -d "$STANDALONE_SRC" ]]; then
        STANDALONE_ZIP="$ARTIFACTS_DIR/apache-kie-$VERSION-incubating-business-automation-standalone-editors.zip"
        echo "Creating RC artifact: $(basename "$STANDALONE_ZIP")"
        (cd "$STANDALONE_SRC" && zip -r "$STANDALONE_ZIP" .)
    else
        echo "WARN: packages/kie-editors-standalone/dist not found — skipping standalone editors zip"
    fi

    ACCEL_SRC="$REPO_ROOT/packages/kie-sandbox-accelerator-quarkus/dist"
    if [[ -d "$ACCEL_SRC" ]]; then
        ACCEL_ZIP="$ARTIFACTS_DIR/apache-kie-$VERSION-incubating-sandbox-accelerator-quarkus.zip"
        echo "Creating RC artifact: $(basename "$ACCEL_ZIP")"
        (cd "$ACCEL_SRC" && zip -r "$ACCEL_ZIP" .)
    else
        echo "WARN: packages/kie-sandbox-accelerator-quarkus/dist not found — skipping accelerator zip"
    fi

    echo "RC artifacts written to: $ARTIFACTS_DIR"
fi

if [[ "$PUBLISH" == "true" ]]; then
    AUTH_REPO_URL="${KOGITO_ONLINE_REPO/https:\/\//https://${GITHUB_TOKEN}@}"
    echo "Cloning $KOGITO_ONLINE_REPO @ $GH_PAGES_BRANCH ..."
    git clone --branch "$GH_PAGES_BRANCH" --depth 1 "$AUTH_REPO_URL" "$TEMP_DIR/kogito-online"

    cd "$TEMP_DIR/kogito-online"
    git config user.email "asf-ci-kie@jenkins.kie.apache.org"
    git config user.name "Apache KIE Release Bot"
    git checkout "$GH_PAGES_BRANCH"

    # Remove everything except the directories/files we want to keep
    find . -maxdepth 1 ! -name '.' ! -name 'dev' ! -name 'editors' ! -name 'standalone' \
        ! -name 'chrome-extension' ! -name '.nojekyll' ! -name 'CNAME' \
        -exec rm -rf {} +

    ONLINE_DIST="$REPO_ROOT/packages/online-editor/dist"
    if [[ -d "$ONLINE_DIST" ]]; then
        EDITORS_DIR="editors/$VERSION"
        rm -rf "$EDITORS_DIR"
        mkdir -p "$EDITORS_DIR"
        rm -rf editors/latest
        ln -s "$VERSION" editors/latest
        echo "Copying Online Editor resources..."
        cp -r "$ONLINE_DIST"/. .
        echo "  deployed sandbox webapp to root + editors/$VERSION"
    fi

    STANDALONE_DIST="$REPO_ROOT/packages/kie-editors-standalone/dist"
    if [[ -d "$STANDALONE_DIST" ]]; then
        mkdir -p "$EDITORS_DIR"
        cp -r "$STANDALONE_DIST"/. "$EDITORS_DIR/"
        echo "  deployed standalone editors to editors/$VERSION"
    fi

    CHROME_DIST="$REPO_ROOT/packages/chrome-extension-pack-kogito-kie-editors/dist"
    if [[ -d "$CHROME_DIST" ]]; then
        CHROME_DIR="chrome-extension/$VERSION"
        rm -rf "$CHROME_DIR"
        mkdir -p "$CHROME_DIR"
        cp -r "$CHROME_DIST"/fonts "$CHROME_DIR/" 2>/dev/null || true
        cp "$CHROME_DIST"/*-envelope.* "$CHROME_DIR/" 2>/dev/null || true
        (cd "$CHROME_DIR" && ln -sf "../../editors/$VERSION/bpmn" bpmn 2>/dev/null || true)
        (cd "$CHROME_DIR" && ln -sf "../../editors/$VERSION/dmn" dmn 2>/dev/null || true)
        (cd "$CHROME_DIR" && ln -sf "../../editors/$VERSION/scesim" scesim 2>/dev/null || true)
        echo "  deployed chrome extension editors to chrome-extension/$VERSION"
    fi

    git add .
    git commit -m "Deploy $VERSION (Online Editor + Standalone Editors + Chrome Extension editors)" || echo "  (nothing to commit)"
    git push origin "$GH_PAGES_BRANCH"
    echo "Pushed to GitHub Pages."
    cd "$REPO_ROOT"

    ACCEL_CONTENT="$REPO_ROOT/packages/kie-sandbox-accelerator-quarkus/dist/git-repo-content"
    if [[ -d "$ACCEL_CONTENT" ]]; then
        AUTH_ACCEL_URL="${ACCELERATOR_REPO/https:\/\//https://${GITHUB_TOKEN}@}"
        echo "Cloning $ACCELERATOR_REPO @ main ..."
        git clone --depth 1 "$AUTH_ACCEL_URL" "$TEMP_DIR/accelerator"
        cd "$TEMP_DIR/accelerator"
        git config user.email "asf-ci-kie@jenkins.kie.apache.org"
        git config user.name "Apache KIE Release Bot"

        git checkout --orphan "$VERSION"
        cp -r "$ACCEL_CONTENT"/. .
        git add .
        git commit -m "Apache KIE Sandbox Quarkus Accelerator $VERSION"
        git tag "$VERSION"
        git push origin "$VERSION"
        echo "Pushed accelerator content to tag $VERSION."
    else
        echo "WARN: packages/kie-sandbox-accelerator-quarkus/dist/git-repo-content not found — skipping accelerator publish"
    fi
else
    echo "Dry run complete (pass --publish to push to GitHub Pages and accelerator repo)."
fi

echo ""
echo "=========================================="
echo "DONE"
echo "=========================================="