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

# Creates apache-kie-<version>-incubating-sources.zip. The source release covers BOTH Apache KIE repositories, so
# the zip contains (under apache-kie-<version>-incubating/):
#
#   BUILD              how to build everything from source
#   DISCLAIMER-WIP     incubator disclaimer
#   LICENSE            merged LICENSE of incubator-kie and incubator-kie-tools
#   NOTICE             merged NOTICE of incubator-kie and incubator-kie-tools
#   incubator-kie/     git archive of apache/incubator-kie at --kie-git-ref
#   incubator-kie-tools/  git archive of this repository at --git-ref

usage() {
    echo "Usage: $0 <version> [--git-ref <ref>] [--kie-git-ref <ref>] [--kie-repo <url|path>] [--output-dir <dir>]"
    echo ""
    echo "Arguments:"
    echo "    version              Release or RC version (e.g., 10.2.0 or 10.2.0-rc1)"
    echo ""
    echo "Options:"
    echo "    --git-ref <ref>      incubator-kie-tools git reference to archive (default: HEAD)"
    echo "    --kie-git-ref <ref>  incubator-kie branch or tag to archive (default: same as --git-ref;"
    echo "                         when that is HEAD, the default branch of the incubator-kie repository)"
    echo "    --kie-repo <u|p>     incubator-kie repository URL or local path"
    echo "                         (default: https://github.com/apache/incubator-kie.git)"
    echo "    --output-dir <d>     Output directory (default: ./release-artifacts)"
    echo "    --help"
    echo ""
    echo "Environment: KIE_REPO and KIE_GIT_REF are used when the matching options are not given."
    exit 1
}

VERSION=""
GIT_REF="HEAD"
KIE_GIT_REF="${KIE_GIT_REF:-}"
KIE_REPO="${KIE_REPO:-https://github.com/apache/incubator-kie.git}"
OUTPUT_DIR="$REPO_ROOT/release-artifacts"

while [[ $# -gt 0 ]]; do
    case $1 in
        --git-ref)     GIT_REF="$2";     shift 2 ;;
        --kie-git-ref) KIE_GIT_REF="$2"; shift 2 ;;
        --kie-repo)    KIE_REPO="$2";    shift 2 ;;
        --output-dir)  OUTPUT_DIR="$2";  shift 2 ;;
        --help)        usage ;;
        *)
            if [[ -z "$VERSION" ]]; then VERSION="$1"; else echo "Unknown argument: $1"; usage; fi
            shift ;;
    esac
done

[[ -z "$VERSION" ]] && { echo "ERROR: version is required"; usage; }
[[ -z "$KIE_GIT_REF" ]] && KIE_GIT_REF="$GIT_REF"

# A local path is cloned through file:// so that --depth is honored.
if [[ -d "$KIE_REPO" ]]; then
    KIE_REPO="file://$(cd "$KIE_REPO" && pwd)"
fi

echo "=========================================="
echo "Source Tarball"
echo "Version:        $VERSION"
echo "kie-tools ref:  $GIT_REF"
echo "kie repo:       $KIE_REPO"
echo "kie ref:        $KIE_GIT_REF"
echo "Output:         $OUTPUT_DIR"
echo "=========================================="

mkdir -p "$OUTPUT_DIR"
OUTPUT_DIR="$(cd "$OUTPUT_DIR" && pwd)"
ROOT_DIR_NAME="apache-kie-$VERSION-incubating"
ZIPFILE="$OUTPUT_DIR/$ROOT_DIR_NAME-sources.zip"

WORK_DIR="$(mktemp -d)"
trap 'rm -rf "$WORK_DIR"' EXIT
STAGE="$WORK_DIR/$ROOT_DIR_NAME"
mkdir -p "$STAGE/incubator-kie" "$STAGE/incubator-kie-tools"

# --- incubator-kie-tools ----------------------------------------------------------------------------------------
cd "$REPO_ROOT"
local_archive_ref="$GIT_REF"
if [[ "$local_archive_ref" == "HEAD" ]] && ! git diff --quiet HEAD 2>/dev/null; then
    stash_sha=$(git stash create 2>/dev/null || true)
    if [[ -n "$stash_sha" ]]; then
        local_archive_ref="$stash_sha"
    fi
fi
echo "Archiving incubator-kie-tools from ref $local_archive_ref..."
git archive --format=tar "$local_archive_ref" | tar -x -C "$STAGE/incubator-kie-tools"

# --- incubator-kie ----------------------------------------------------------------------------------------------
echo "Archiving incubator-kie from $KIE_REPO ($KIE_GIT_REF)..."
KIE_CLONE="$WORK_DIR/incubator-kie-clone"
clone_args=(--quiet --depth 1)
if [[ "$KIE_GIT_REF" == "HEAD" ]]; then
    echo "WARN: no incubator-kie ref given, using the default branch. Pass --kie-git-ref <tag> for a real release."
else
    clone_args+=(--branch "$KIE_GIT_REF")
fi
git -c advice.detachedHead=false clone "${clone_args[@]}" "$KIE_REPO" "$KIE_CLONE"
git -C "$KIE_CLONE" archive --format=tar HEAD | tar -x -C "$STAGE/incubator-kie"

# --- Top-level files --------------------------------------------------------------------------------------------
KIE_DIR="$STAGE/incubator-kie"
TOOLS_DIR="$STAGE/incubator-kie-tools"
for f in LICENSE NOTICE DISCLAIMER-WIP; do
    for d in "$KIE_DIR" "$TOOLS_DIR"; do
        [[ -f "$d/$f" ]] || { echo "ERROR: $f not found in $d"; exit 1; }
    done
done

# LICENSE: incubator-kie's file (Apache License 2.0 + its third-party sections), followed by the third-party sections
# of incubator-kie-tools, which start at the separator line right before the first "for <path>" line after the
# Apache License text. The paths listed in those sections are relative to each repository, so they are prefixed with
# the directory of the repository they now live in.
prefix_license_paths() {
    awk -v prefix="$1/" '
        /^for [A-Za-z0-9_.-]+\/.*\.[A-Za-z0-9]+$/ { sub(/^for /, "for " prefix); print; next }
        /^    [A-Za-z0-9_.-]+\/.*\.[A-Za-z0-9]+$/   { sub(/^    /, "    " prefix); print; next }
        { print }'
}
first_tools_section=$(awk '/END OF TERMS AND CONDITIONS/ {e=1} e && /^for / {print NR; exit}' "$TOOLS_DIR/LICENSE")
[[ -n "$first_tools_section" ]] || { echo "ERROR: could not find third-party sections in incubator-kie-tools/LICENSE"; exit 1; }
first_kie_section=$(awk '/END OF TERMS AND CONDITIONS/ {e=1} e && /^for / {print NR; exit}' "$KIE_DIR/LICENSE")
[[ -n "$first_kie_section" ]] || { echo "ERROR: could not find third-party sections in incubator-kie/LICENSE"; exit 1; }
{
    head -n "$((first_kie_section - 2))" "$KIE_DIR/LICENSE"
    tail -n +"$((first_kie_section - 1))" "$KIE_DIR/LICENSE" | prefix_license_paths incubator-kie
    printf '\n\n'
    tail -n +"$((first_tools_section - 1))" "$TOOLS_DIR/LICENSE" | prefix_license_paths incubator-kie-tools
} > "$STAGE/LICENSE"

# NOTICE: incubator-kie's file plus the paragraphs of incubator-kie-tools' that it does not already contain.
awk 'BEGIN { RS = ""; ORS = "\n\n" }
     FNR == 1 { f++ }
     { p = $0; gsub(/[ \t]+\n/, "\n", p); gsub(/[ \t]+$/, "", p) }
     f == 1 { seen[p] = 1; print p; next }
     !(p in seen) { print p }' "$KIE_DIR/NOTICE" "$TOOLS_DIR/NOTICE" > "$STAGE/NOTICE"

cp "$TOOLS_DIR/DISCLAIMER-WIP" "$STAGE/DISCLAIMER-WIP"

# BUILD: tool versions come from the tools repository so they cannot drift.
tool_version() {
    node -p "require('$TOOLS_DIR/repo/build-dependencies-versions.json')['$1']"
}
NODE_VERSION="$(tool_version node)"
PNPM_VERSION="$(tool_version pnpm)"
MAVEN_VERSION="$(tool_version maven)"
JAVA_VERSION="$(tool_version java)"

cat > "$STAGE/BUILD" <<EOF
Building Apache KIE (incubating) $VERSION from source
=====================================================

This source release contains two repositories, each in its own directory:

  incubator-kie/        Java modules (Drools, OptaPlanner, jBPM, Kogito)
  incubator-kie-tools/  Tooling: editors, VS Code and Chrome extensions, web apps, container images, Helm charts

Build incubator-kie first: incubator-kie-tools resolves the Java artifacts from the local Maven repository.

Requirements
------------

  - Java $JAVA_VERSION (incubator-kie also builds with Java 21)
  - Maven $MAVEN_VERSION
  - Node.js $NODE_VERSION (24 or newer)
  - pnpm $PNPM_VERSION
  - Docker or Podman, only to build the container images of incubator-kie-tools

1. incubator-kie
----------------

  cd incubator-kie
  mvn clean install -DskipTests

See incubator-kie/docs/BUILDING.md for build flags and test profiles.

2. incubator-kie-tools
----------------------

  cd incubator-kie-tools
  pnpm bootstrap
  pnpm -r build:prod

See incubator-kie-tools/README.md and incubator-kie-tools/repo/MANUAL.md for details, including how to build a
single package (pnpm -F <package>... build:prod).

Verifying this release
----------------------

Check the signature (.asc) and the SHA-512 checksum (.sha512) of the sources zip against
https://downloads.apache.org/incubator/kie/KEYS, as described in https://www.apache.org/info/verification.html
EOF

# --- Zip and verify --------------------------------------------------------------------------------------------
rm -f "$ZIPFILE"
(cd "$WORK_DIR" && zip -qr "$ZIPFILE" "$ROOT_DIR_NAME")

if ! unzip -t "$ZIPFILE" > /dev/null 2>&1; then
    echo "ERROR: zip verification failed"
    exit 1
fi

SIZE=$(du -h "$ZIPFILE" | cut -f1)
echo "Created: $ZIPFILE ($SIZE)"

# The root of the zip must contain exactly these entries.
# LC_ALL=C: the order must not depend on the locale of the machine running the release.
actual_entries=$(unzip -Z1 "$ZIPFILE" | awk -F/ -v root="$ROOT_DIR_NAME" '$1 == root && $2 != "" {print $2}' | LC_ALL=C sort -u | paste -sd' ' -)
expected_entries="BUILD DISCLAIMER-WIP LICENSE NOTICE incubator-kie incubator-kie-tools"
if [[ "$actual_entries" != "$expected_entries" ]]; then
    echo "ERROR: unexpected content in $ROOT_DIR_NAME/"
    echo "  expected: $expected_entries"
    echo "  actual:   $actual_entries"
    exit 1
fi

echo "=========================================="
echo "DONE"
echo "=========================================="
