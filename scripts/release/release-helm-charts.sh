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

# Helm Chart Release Script
# Packages Helm charts and optionally pushes them to an OCI Helm repository.
#
# Usage:
#   ./release-helm-charts.sh <version>              # package only (dry run)
#   ./release-helm-charts.sh <version> --rc         # package + collect as RC artifacts
#   ./release-helm-charts.sh <version> --publish    # package + push to OCI registry
#
# For OCI push, set:
#   HELM_REGISTRY   e.g. ghcr.io/apache/incubator-kie-helm-charts
#   HELM_USERNAME   (optional, if registry requires auth)
#   HELM_PASSWORD   (optional, if registry requires auth)

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

usage() {
    echo "Usage: $0 <version> [OPTIONS]"
    echo ""
    echo "Options:"
    echo "    --publish    Push charts to OCI Helm registry"
    echo "    --rc         Collect chart tarballs as RC artifacts"
    echo "    --help       Show this help"
    exit 1
}

VERSION=""
PUBLISH=false
RC_MODE=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --publish)    PUBLISH=true; shift ;;
        --rc)         RC_MODE=true; shift ;;
        --skip-build) shift ;;          # helm-charts has no build step; accepted and ignored
        --help)       usage ;;
        *)
            if [[ -z "$VERSION" ]]; then VERSION="$1"; else echo "Unknown argument: $1"; usage; fi
            shift ;;
    esac
done

[[ -z "$VERSION" ]] && { echo "ERROR: version is required"; usage; }

if ! command -v helm &>/dev/null; then
    echo "ERROR: helm is not installed (need Helm 3.x)"
    exit 1
fi

echo "=========================================="
echo "Helm Chart Release"
echo "Version: $VERSION"
echo "RC mode: $RC_MODE"
echo "Publish: $PUBLISH"
echo "=========================================="
echo ""

# Keys are the artifact name suffixes used in apache-kie-<version>-incubating-<key>.tar.gz
declare -A CHARTS=(
    ["sandbox-helm-chart"]="packages/kie-sandbox-helm-chart"
    ["runtime-tools-console-helm-chart"]="packages/runtime-tools-consoles-helm-chart"
)

OUTPUT_DIR="$REPO_ROOT/release-artifacts/helm-charts"
mkdir -p "$OUTPUT_DIR"

cd "$REPO_ROOT"

echo "Packaging Helm charts..."
for slug in "${!CHARTS[@]}"; do
    chart_pkg="${CHARTS[$slug]}"

    if [[ ! -d "$chart_pkg" ]]; then
        echo "  SKIP  $slug  ($chart_pkg not found)"
        continue
    fi

    # Locate Chart.yaml (could be in src/ or at root of the package)
    chart_yaml=""
    for candidate in "$chart_pkg/src/Chart.yaml" "$chart_pkg/Chart.yaml"; do
        [[ -f "$candidate" ]] && { chart_yaml="$candidate"; break; }
    done

    if [[ -z "$chart_yaml" ]]; then
        echo "  SKIP  $slug  (Chart.yaml not found in $chart_pkg)"
        continue
    fi

    chart_dir="$(dirname "$chart_yaml")"

    echo "  PACKAGE  $slug  ($chart_dir)"

    TEMP_DIR=$(mktemp -d)
    trap "rm -rf $TEMP_DIR" EXIT
    cp -r "$chart_dir/." "$TEMP_DIR/chart"
    sed -i.bak "s/^version:.*/version: $VERSION/" "$TEMP_DIR/chart/Chart.yaml"
    sed -i.bak "s/^appVersion:.*/appVersion: \"$VERSION\"/" "$TEMP_DIR/chart/Chart.yaml"
    rm -f "$TEMP_DIR/chart/Chart.yaml.bak"

    helm package "$TEMP_DIR/chart" --destination "$OUTPUT_DIR"

    found=$(find "$OUTPUT_DIR" -maxdepth 1 -name "*-${VERSION}.tgz" | head -1)
    if [[ -z "$found" ]]; then
        echo "  ERROR  could not find packaged chart for $slug"
        continue
    fi

    apache_name="apache-kie-$VERSION-incubating-$slug.tar.gz"
    mv "$found" "$OUTPUT_DIR/$apache_name"
    echo "  DONE   $OUTPUT_DIR/$apache_name"
done

if [[ "$PUBLISH" == "true" ]]; then
    HELM_REGISTRY="${HELM_REGISTRY:-}"
    if [[ -z "$HELM_REGISTRY" ]]; then
        echo "ERROR: HELM_REGISTRY environment variable is required for --publish"
        echo "  e.g. HELM_REGISTRY=ghcr.io/apache/incubator-kie-helm-charts"
        exit 1
    fi

    if [[ -n "${HELM_USERNAME:-}" && -n "${HELM_PASSWORD:-}" ]]; then
        registry_host="$(echo "$HELM_REGISTRY" | cut -d/ -f1)"
        echo "$HELM_PASSWORD" | helm registry login "$registry_host" \
            --username "$HELM_USERNAME" --password-stdin
    fi

    echo ""
    echo "Pushing charts to oci://$HELM_REGISTRY ..."
    for slug in "${!CHARTS[@]}"; do
        tarball="$OUTPUT_DIR/apache-kie-$VERSION-incubating-$slug.tar.gz"
        [[ ! -f "$tarball" ]] && continue
        echo "  PUSH  $tarball"
        helm push "$tarball" "oci://$HELM_REGISTRY"
    done
fi

echo ""
echo "=========================================="
echo "DONE"
if [[ "$RC_MODE" == "true" || "$PUBLISH" == "false" ]]; then
    echo "Chart tarballs in: $OUTPUT_DIR"
fi
echo "=========================================="
