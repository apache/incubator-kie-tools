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
        --publish)    PUBLISH=true;    shift ;;
        --rc)         RC_MODE=true;    shift ;;
        --skip-build) SKIP_BUILD=true; shift ;;
        *)
            echo "Unknown option: $1"
            exit 1
            ;;
    esac
done

if [[ -z "${VERSION}" ]]; then
    echo "Usage: $0 <version> [--publish] [--rc] [--skip-build]"
    exit 1
fi

echo "=========================================="
echo "Chrome Extensions Release"
echo "Version: ${VERSION}"
echo "Publish: ${PUBLISH}"
echo "RC mode: ${RC_MODE}"
echo "=========================================="

cd "${REPO_ROOT}"

if [[ "${SKIP_BUILD}" == "true" ]]; then
    echo "Skipping build (--skip-build set)..."
else
    echo "Building Chrome extensions..."
    pnpm -F "chrome-extension-pack-kogito-kie-editors..." build:prod
fi

KIE_EDITORS_ZIP=$(find "packages/chrome-extension-pack-kogito-kie-editors/dist" -maxdepth 1 -name "chrome_extension_kogito_kie_editors_*.zip" 2>/dev/null | head -1)
if [[ -z "$KIE_EDITORS_ZIP" ]]; then
    echo "ERROR: chrome_extension_kogito_kie_editors_*.zip not found in packages/chrome-extension-pack-kogito-kie-editors/dist"
    exit 1
fi
echo "Found: $KIE_EDITORS_ZIP"

if [[ "${RC_MODE}" == "true" ]]; then
    echo "Creating release candidate artifacts..."
    ARTIFACTS_DIR="${REPO_ROOT}/release-artifacts"
    mkdir -p "${ARTIFACTS_DIR}"

    cp "${KIE_EDITORS_ZIP}" "${ARTIFACTS_DIR}/apache-kie-${VERSION}-incubating-business-automation-chrome-extension.zip"

    TMP_KIE="${ARTIFACTS_DIR}/kie-editors-tmp"
    mkdir -p "${TMP_KIE}"
    cp -r packages/chrome-extension-pack-kogito-kie-editors/dist/{fonts,*-envelope.*} "${TMP_KIE}/"
    (cd "${TMP_KIE}" && zip -r "${ARTIFACTS_DIR}/apache-kie-${VERSION}-incubating-business-automation-chrome-extension-editors.zip" .)
    rm -rf "${TMP_KIE}"

    echo "RC artifacts created in: ${ARTIFACTS_DIR}"
fi

if [[ "${PUBLISH}" == "true" ]]; then
    echo "Publishing to Chrome Web Store..."

    if [[ -z "${CHROME_CLIENT_ID:-}" ]] || [[ -z "${CHROME_CLIENT_SECRET:-}" ]] || [[ -z "${CHROME_REFRESH_TOKEN:-}" ]]; then
        echo "ERROR: Chrome Web Store credentials required:"
        echo "  - CHROME_CLIENT_ID"
        echo "  - CHROME_CLIENT_SECRET"
        echo "  - CHROME_REFRESH_TOKEN"
        echo "  - CHROME_KIE_EDITORS_EXTENSION_ID"
        exit 1
    fi

    get_access_token() {
        local response
        response=$(curl -sS -X POST "https://oauth2.googleapis.com/token" \
            -d "client_id=${CHROME_CLIENT_ID}" \
            -d "client_secret=${CHROME_CLIENT_SECRET}" \
            -d "refresh_token=${CHROME_REFRESH_TOKEN}" \
            -d "grant_type=refresh_token")
        echo "${response}" | grep -o '"access_token":"[^"]*"' | cut -d'"' -f4
    }

    upload_extension() {
        local extension_id=$1
        local zip_file=$2
        local access_token=$3
        echo "Uploading extension ${extension_id}..."
        local response
        response=$(curl -sS -X PUT \
            "https://www.googleapis.com/upload/chromewebstore/v1.1/items/${extension_id}" \
            -H "Authorization: Bearer ${access_token}" \
            -H "x-goog-api-version:2" \
            -T "${zip_file}")
        local upload_state
        upload_state=$(echo "${response}" | grep -o '"uploadState":"[^"]*"' | cut -d'"' -f4)
        if [[ "${upload_state}" != "SUCCESS" ]]; then
            echo "ERROR: Upload failed for ${extension_id}"
            echo "Response: ${response}"
            return 1
        fi
        echo "Upload successful for ${extension_id}"
    }

    publish_extension() {
        local extension_id=$1
        local access_token=$2
        echo "Publishing extension ${extension_id}..."
        local response
        response=$(curl -sS -X POST \
            "https://www.googleapis.com/chromewebstore/v1.1/items/${extension_id}/publish" \
            -H "Authorization: Bearer ${access_token}" \
            -H "x-goog-api-version:2" \
            -H "Content-Length:0")
        local status
        status=$(echo "${response}" | grep -o '"status":\["[^"]*"\]' | cut -d'"' -f4)
        if [[ "${status}" != "OK" ]] && [[ "${status}" != "PUBLISHED" ]]; then
            echo "WARNING: Publish status for ${extension_id}: ${status}"
            echo "Response: ${response}"
        else
            echo "Publish successful for ${extension_id}"
        fi
    }

    echo "Getting OAuth access token..."
    ACCESS_TOKEN=$(get_access_token)

    if [[ -z "${ACCESS_TOKEN}" ]]; then
        echo "ERROR: Failed to get access token"
        exit 1
    fi

    echo "Access token obtained successfully"

    if [[ -n "${CHROME_KIE_EDITORS_EXTENSION_ID:-}" ]]; then
        echo ""
        echo "Publishing Business Automation Chrome Extension..."
        if upload_extension "${CHROME_KIE_EDITORS_EXTENSION_ID}" "${KIE_EDITORS_ZIP}" "${ACCESS_TOKEN}"; then
            publish_extension "${CHROME_KIE_EDITORS_EXTENSION_ID}" "${ACCESS_TOKEN}"
        else
            echo "ERROR: Failed to upload Business Automation extension"
            exit 1
        fi
    else
        echo "WARNING: CHROME_KIE_EDITORS_EXTENSION_ID not set, skipping Business Automation extension"
    fi

    echo ""
    echo "Chrome Web Store publishing complete!"
fi

if [[ "${RC_MODE}" == "false" && "${PUBLISH}" == "false" ]]; then
    echo "Dry run - extensions built at:"
    echo "  - ${KIE_EDITORS_ZIP}"
fi

echo "=========================================="
echo "DONE"
echo "=========================================="

