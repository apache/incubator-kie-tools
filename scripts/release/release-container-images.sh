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

# Container Image Release Script
# Builds, saves, and optionally publishes container images.
#
# Usage:
#   ./release-container-images.sh <version>
#   ./release-container-images.sh <version> --rc
#   ./release-container-images.sh <version> --publish --registry docker.io/apache

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

usage() {
    echo "Usage: $0 <version> [OPTIONS]"
    echo ""
    echo "Arguments:"
    echo "    version         Release version (e.g., 10.2.0)"
    echo ""
    echo "Options:"
    echo "    --publish       Push images to container registry (default: false)"
    echo "    --rc            Save images as tarballs for RC (default: false)"
    echo "    --registry      Container registry URL (default: docker.io/apache)"
    echo "    --help          Show this help"
    echo ""
    echo "Environment Variables (required for --publish):"
    echo "    DOCKER_USERNAME"
    echo "    DOCKER_PASSWORD"
    exit 1
}

VERSION=""
PUBLISH=false
RC_MODE=false
REGISTRY="docker.io/apache"

SKIP_BUILD=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --publish)    PUBLISH=true;    shift ;;
        --rc)         RC_MODE=true;    shift ;;
        --skip-build) SKIP_BUILD=true; shift ;;
        --registry)   REGISTRY="$2";   shift 2 ;;
        --help)       usage ;;
        *)
            if [[ -z "$VERSION" ]]; then VERSION="$1"; else echo "Unknown argument: $1"; usage; fi
            shift ;;
    esac
done

[[ -z "$VERSION" ]] && { echo "ERROR: version is required"; usage; }

if [[ "$PUBLISH" == "true" ]]; then
    [[ -z "${DOCKER_USERNAME:-}" || -z "${DOCKER_PASSWORD:-}" ]] && {
        echo "ERROR: DOCKER_USERNAME and DOCKER_PASSWORD are required for --publish"
        exit 1
    }
fi

echo "=========================================="
echo "Container Image Release"
echo "Version:  $VERSION"
echo "RC mode:  $RC_MODE"
echo "Publish:  $PUBLISH"
echo "Registry: $REGISTRY"
echo "=========================================="
echo ""

# Keys are the artifact name suffixes used in apache-kie-<version>-incubating-<key>-image.tar.gz
declare -A IMAGES=(
    ["kogito-base-builder"]="packages/kogito-base-builder-image"
    ["kogito-data-index-ephemeral"]="packages/kogito-data-index-ephemeral-image"
    ["kogito-data-index-postgresql"]="packages/kogito-data-index-postgresql-image"
    ["kogito-jit-runner"]="packages/kogito-jit-runner-image"
    ["kogito-jobs-service-allinone"]="packages/kogito-jobs-service-allinone-image"
    ["kogito-jobs-service-ephemeral"]="packages/kogito-jobs-service-ephemeral-image"
    ["kogito-jobs-service-postgresql"]="packages/kogito-jobs-service-postgresql-image"
    ["kogito-management-console"]="packages/kogito-management-console"
    ["kogito-db-migrator-tool"]="packages/kogito-db-migrator-tool-image"
    ["cors-proxy"]="packages/cors-proxy-image"
    ["sandbox-webapp"]="packages/kie-sandbox-webapp-image"
    ["sandbox-extended-services"]="packages/kie-sandbox-extended-services-image"
    ["sandbox-dev-deployment-base"]="packages/dev-deployment-base-image"
    ["sandbox-dev-deployment-dmn-form-webapp"]="packages/dev-deployment-dmn-form-webapp-image"
    ["sandbox-dev-deployment-quarkus-blank-app"]="packages/dev-deployment-quarkus-blank-app-image"
)

OUTPUT_DIR="$REPO_ROOT/release-artifacts/container-images"
[[ "$RC_MODE" == "true" ]] && mkdir -p "$OUTPUT_DIR"

cd "$REPO_ROOT"
export KIE_TOOLS_BUILD__buildContainerImages=true

if [[ "$SKIP_BUILD" == "true" ]]; then
    # Docker must already be running and images must already be present locally.
    if ! docker info &>/dev/null; then
        echo "SKIP: Docker is not running — cannot save container image tarballs with --skip-build"
        echo "      Start Docker (e.g. colima start) and ensure images were built with"
        echo "      KIE_TOOLS_BUILD__buildContainerImages=true, then re-run."
        exit 0
    fi
    echo "Skipping build (--skip-build set)..."
else
    # Build each image (build:prod triggers the Containerfile build via the package's build script)
    echo "Building container images..."
    for artifact_name in "${!IMAGES[@]}"; do
        pkg_path="${IMAGES[$artifact_name]}"
        if [[ ! -d "$pkg_path" ]]; then
            echo "  SKIP  $artifact_name  ($pkg_path not found)"
            continue
        fi
        pkg_name=$(node -p "require('./$pkg_path/package.json').name" 2>/dev/null || basename "$pkg_path")
        echo "  BUILD $artifact_name  ($pkg_name)"
        pnpm --filter "$pkg_name..." build:prod
    done
fi

echo ""
echo "Tagging images..."
for artifact_name in "${!IMAGES[@]}"; do
    pkg_path="${IMAGES[$artifact_name]}"
    [[ ! -d "$pkg_path" ]] && continue

    full_image="$REGISTRY/incubator-kie-$artifact_name:$VERSION"
    docker tag "$(node -p "require('./$pkg_path/package.json').name" 2>/dev/null | sed 's|@kie-tools/||')" "$full_image" 2>/dev/null \
        || docker tag "incubator-kie-$artifact_name:latest" "$full_image" 2>/dev/null \
        || docker tag "apache/incubator-kie-$artifact_name:main" "$full_image" 2>/dev/null \
        || docker tag "apache/incubator-kie-$artifact_name:latest" "$full_image" 2>/dev/null \
        || echo "  WARN  could not tag $artifact_name (image may have a different local name)"

    if [[ "$RC_MODE" == "true" ]]; then
        if docker image inspect "$full_image" &>/dev/null; then
            tarball="$OUTPUT_DIR/apache-kie-$VERSION-incubating-$artifact_name-image.tar.gz"
            echo "  SAVE  $tarball"
            docker save "$full_image" | gzip > "$tarball"
        else
            echo "  SKIP  $artifact_name (image $full_image not found in local Docker daemon)"
        fi
    fi
done

if [[ "$PUBLISH" == "true" ]]; then
    echo ""
    echo "Pushing images to $REGISTRY..."
    echo "$DOCKER_PASSWORD" | docker login "$(echo "$REGISTRY" | cut -d/ -f1)" \
        -u "$DOCKER_USERNAME" --password-stdin

    for artifact_name in "${!IMAGES[@]}"; do
        pkg_path="${IMAGES[$artifact_name]}"
        [[ ! -d "$pkg_path" ]] && continue
        full_image="$REGISTRY/incubator-kie-$artifact_name:$VERSION"
        echo "  PUSH  $full_image"
        docker push "$full_image"
    done

    docker logout "$(echo "$REGISTRY" | cut -d/ -f1)"
fi

echo ""
echo "=========================================="
echo "DONE"
if [[ "$RC_MODE" == "true" ]]; then
    echo "RC tarballs written to: $OUTPUT_DIR"
fi
echo "=========================================="
