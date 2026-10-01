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

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

usage() {
    echo "Usage: $0 <version> [--git-ref <ref>] [--output-dir <dir>]"
    echo ""
    echo "Arguments:"
    echo "    version           Release or RC version (e.g., 10.2.0 or 10.2.0-rc1)"
    echo ""
    echo "Options:"
    echo "    --git-ref <ref>   Git reference to archive (default: HEAD)"
    echo "    --output-dir <d>  Output directory (default: ./release-artifacts)"
    echo "    --help"
    exit 1
}

VERSION=""
GIT_REF="HEAD"
OUTPUT_DIR="$REPO_ROOT/release-artifacts"

while [[ $# -gt 0 ]]; do
    case $1 in
        --git-ref)    GIT_REF="$2";    shift 2 ;;
        --output-dir) OUTPUT_DIR="$2"; shift 2 ;;
        --help)       usage ;;
        *)
            if [[ -z "$VERSION" ]]; then VERSION="$1"; else echo "Unknown argument: $1"; usage; fi
            shift ;;
    esac
done

[[ -z "$VERSION" ]] && { echo "ERROR: version is required"; usage; }

echo "=========================================="
echo "Source Tarball"
echo "Version:   $VERSION"
echo "Git ref:   $GIT_REF"
echo "Output:    $OUTPUT_DIR"
echo "=========================================="

mkdir -p "$OUTPUT_DIR"
ZIPFILE="$OUTPUT_DIR/apache-kie-$VERSION-incubating-sources.zip"

cd "$REPO_ROOT"
echo "Creating git archive..."
git archive --format=zip --prefix="apache-kie-$VERSION-incubating/" "$GIT_REF" > "$ZIPFILE"

if ! unzip -t "$ZIPFILE" > /dev/null 2>&1; then
    echo "ERROR: zip verification failed"
    exit 1
fi

SIZE=$(du -h "$ZIPFILE" | cut -f1)
echo "Created: $ZIPFILE ($SIZE)"

for required in LICENSE NOTICE DISCLAIMER-WIP; do
    if ! unzip -l "$ZIPFILE" | grep -q "apache-kie-$VERSION-incubating/$required"; then
        echo "WARN: $required not found in zip"
    fi
done

echo "=========================================="
echo "DONE"
echo "=========================================="