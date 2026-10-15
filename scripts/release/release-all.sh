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

# Require Bash 4+ for associative array support (declare -A).
# macOS ships Bash 3.2 by default; install via: brew install bash
if [[ "${BASH_VERSINFO[0]}" -lt 4 ]]; then
    echo "ERROR: Bash 4+ is required. Found: ${BASH_VERSION}"
    echo "       On macOS, install with: brew install bash"
    echo "       Then run: /opt/homebrew/bin/bash ${BASH_SOURCE[0]} $*"
    exit 1
fi

# Release script for Apache KIE Tools — builds and optionally releases all components
# under a single unified build.
#
# Usage:
#   ./release-all.sh <version>            # dry run (build only, nothing published)
#   ./release-all.sh <version> --rc       # build + collect Apache RC artifacts
#   ./release-all.sh <version> --publish  # build + publish to all public registries
#
# Optional flags:
#   --skip-build           Skip pnpm build:prod (use existing dist/ output)
#   --upload-url <url>     GitHub Release upload URL (required by --publish for binary assets)
#   --registry <url>       Container registry (default: docker.io/apache)
#   --git-ref <ref>        Git reference for source tarball (default: HEAD)
#
# Credentials (required only for --publish, sourced from env):
#   NPM_TOKEN, VSCE_PAT,
#   CHROME_CLIENT_ID, CHROME_CLIENT_SECRET, CHROME_REFRESH_TOKEN, CHROME_KIE_EDITORS_EXTENSION_ID,
#   DOCKER_USERNAME, DOCKER_PASSWORD,
#   HELM_REGISTRY (default: docker.io/apache), HELM_USERNAME (optional), HELM_PASSWORD (optional),
#   GITHUB_TOKEN

export KIE_TOOLS_BUILD__runLinters=false
export KIE_TOOLS_BUILD__runTests=false
export KIE_TOOLS_BUILD__runEndToEndTests=false

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"

# ---------------------------------------------------------------------------
# Argument parsing
# ---------------------------------------------------------------------------
VERSION="${1:-}"
PUBLISH=false
RC_MODE=false
SKIP_BUILD=false
UPLOAD_URL=""
REGISTRY="docker.io/apache"
GIT_REF="HEAD"

shift || true
while [[ $# -gt 0 ]]; do
    case $1 in
        --publish)     PUBLISH=true;        shift ;;
        --rc)          RC_MODE=true;        shift ;;
        --skip-build)  SKIP_BUILD=true;     shift ;;
        --upload-url)  UPLOAD_URL="${2:-}";  shift 2 ;;
        --registry)    REGISTRY="${2:-}";   shift 2 ;;
        --git-ref)     GIT_REF="${2:-}";    shift 2 ;;
        *)
            echo "Unknown option: $1"
            echo "Usage: $0 <version> [--publish] [--rc] [--skip-build] [--upload-url <url>] [--registry <url>] [--git-ref <ref>]"
            exit 1
            ;;
    esac
done

if [[ -z "${VERSION}" ]]; then
    echo "Usage: $0 <version> [--publish] [--rc] [--skip-build] [--upload-url <url>] [--registry <url>]"
    echo "Example: $0 10.2.0 --rc"
    echo "Example: $0 10.2.0 --publish"
    exit 1
fi

ARTIFACTS_DIR="${REPO_ROOT}/release-artifacts"

echo "=========================================="
echo "KIE Tools Release"
echo "Version:    ${VERSION}"
echo "RC mode:    ${RC_MODE}"
echo "Publish:    ${PUBLISH}"
echo "Skip build: ${SKIP_BUILD}"
echo "Registry:   ${REGISTRY}"
echo "=========================================="

cd "${REPO_ROOT}"
if [[ "${SKIP_BUILD}" == "false" ]]; then
    echo ""
    echo "--- Bumping workspace version to ${VERSION} ---"
    pnpm update-version-to "${VERSION}"
    pnpm update-kogito-version-to --maven "${VERSION}"
    pnpm update-stream-name-to "${VERSION}"
fi

# ---------------------------------------------------------------------------
# Helper: sign and upload a single asset to a GitHub Release
# ---------------------------------------------------------------------------
upload_github_asset() {
    local file="$1"
    local asset_name="$2"
    local content_type="${3:-application/octet-stream}"
    if [[ ! -f "${file}" ]]; then
        echo "  SKIP  ${asset_name} (file not found)"
        return
    fi
    echo "  UPLOAD  ${asset_name}"
    curl -sSf \
        -H "Authorization: token ${GITHUB_TOKEN}" \
        -H "Content-Type: ${content_type}" \
        --data-binary "@${file}" \
        "${UPLOAD_URL}?name=${asset_name}"
}

# ---------------------------------------------------------------------------
# 1. NPM packages
# ---------------------------------------------------------------------------
release_npm_packages() {
    echo ""
    echo "--- NPM packages ---"

    if [[ "${SKIP_BUILD}" == "false" ]]; then
        echo "Building public NPM packages..."
        local filter
        filter=$(pnpm -r exec bash -c \
            'if [[ $(jq -r ".private" package.json) != "true" ]]; then echo "-F $(jq -r ".name" package.json)..."; fi')
        pnpm ${filter} build:prod
    fi

    if [[ "${RC_MODE}" == "true" ]]; then
        local tmp_dir="${ARTIFACTS_DIR}/npm-packages-tmp"
        local zip_file="apache-kie-${VERSION}-incubating-tools-npm-packages.zip"
        mkdir -p "${tmp_dir}" "${ARTIFACTS_DIR}"
        local pub_filter
        pub_filter=$(pnpm -r exec bash -c \
            'if [[ $(jq -r ".private" package.json) != "true" ]]; then echo "-F $(jq -r ".name" package.json)"; fi')
        pnpm ${pub_filter} exec bash -c "pnpm pack --pack-destination ${tmp_dir}"
        (cd "${tmp_dir}" && zip -r "${ARTIFACTS_DIR}/${zip_file}" .)
        rm -rf "${tmp_dir}"
        echo "RC artifact: ${ARTIFACTS_DIR}/${zip_file}"
    fi

    if [[ "${PUBLISH}" == "true" ]]; then
        if [[ -z "${NPM_TOKEN:-}" ]]; then
            echo "ERROR: NPM_TOKEN is required for publishing npm packages"
            return 1
        fi
        local npmrc_tmp
        npmrc_tmp=$(mktemp)
        trap "rm -f '${npmrc_tmp}'" EXIT
        echo "//registry.npmjs.org/:_authToken=${NPM_TOKEN}" > "${npmrc_tmp}"
        export NPM_CONFIG_USERCONFIG="${npmrc_tmp}"

        local pub_filter
        pub_filter=$(pnpm -r exec bash -c \
            'if [[ $(jq -r ".private" package.json) != "true" ]]; then echo "-F $(jq -r ".name" package.json)"; fi')
        pnpm ${pub_filter} exec bash -c '
            PKG_NAME=$(jq -r ".name" package.json)
            if ! npm view ${PKG_NAME}@'"${VERSION}"' name --userconfig "'"${npmrc_tmp}"'" &>/dev/null; then
                echo "Publishing ${PKG_NAME}@'"${VERSION}"'"
                pnpm publish --no-git-checks --access public --userconfig "'"${npmrc_tmp}"'"
            else
                echo "Skipping ${PKG_NAME}@'"${VERSION}"' (already published)"
            fi
        '
        rm -f "${npmrc_tmp}"
        trap - EXIT
        echo "NPM packages published."
    fi
}

# ---------------------------------------------------------------------------
# 2. Chrome extensions
# ---------------------------------------------------------------------------
release_chrome_extensions() {
    echo ""
    echo "--- Chrome extensions ---"

    if [[ "${SKIP_BUILD}" == "false" ]]; then
        echo "Building Chrome extensions..."
        pnpm -F "chrome-extension-pack-kogito-kie-editors..." build:prod
    fi

    local kie_editors_zip
    kie_editors_zip=$(find "packages/chrome-extension-pack-kogito-kie-editors/dist" \
        -maxdepth 1 -name "chrome_extension_kogito_kie_editors_*.zip" 2>/dev/null | head -1)
    if [[ -z "${kie_editors_zip}" ]]; then
        echo "ERROR: chrome_extension_kogito_kie_editors_*.zip not found — was the build successful?"
        return 1
    fi

    if [[ "${RC_MODE}" == "true" ]]; then
        mkdir -p "${ARTIFACTS_DIR}"
        cp "${kie_editors_zip}" \
            "${ARTIFACTS_DIR}/apache-kie-${VERSION}-incubating-business-automation-chrome-extension.zip"

        local tmp_kie="${ARTIFACTS_DIR}/kie-editors-tmp"
        mkdir -p "${tmp_kie}"
        cp -r packages/chrome-extension-pack-kogito-kie-editors/dist/{fonts,*-envelope.*} "${tmp_kie}/"
        (cd "${tmp_kie}" && zip -r \
            "${ARTIFACTS_DIR}/apache-kie-${VERSION}-incubating-business-automation-chrome-extension-editors.zip" .)
        rm -rf "${tmp_kie}"
        echo "RC artifacts created in: ${ARTIFACTS_DIR}"
    fi

    if [[ "${PUBLISH}" == "true" ]]; then
        if [[ -z "${CHROME_CLIENT_ID:-}" || -z "${CHROME_CLIENT_SECRET:-}" || -z "${CHROME_REFRESH_TOKEN:-}" ]]; then
            echo "ERROR: CHROME_CLIENT_ID, CHROME_CLIENT_SECRET, and CHROME_REFRESH_TOKEN are required"
            return 1
        fi

        local token_res
        token_res=$(curl -sSf -X POST "https://oauth2.googleapis.com/token" \
            -d "client_id=${CHROME_CLIENT_ID}" \
            -d "client_secret=${CHROME_CLIENT_SECRET}" \
            -d "refresh_token=${CHROME_REFRESH_TOKEN}" \
            -d "grant_type=refresh_token")
        local access_token
        access_token=$(echo "${token_res}" | jq -r '.access_token // empty')

        if [[ -z "${access_token}" ]]; then
            echo "ERROR: Failed to obtain Chrome Web Store OAuth token: ${token_res}"
            return 1
        fi

        _chrome_upload() {
            local ext_id="$1" zip="$2"
            local res state
            res=$(curl -sSf -X PUT \
                "https://www.googleapis.com/upload/chromewebstore/v1.1/items/${ext_id}" \
                -H "Authorization: Bearer ${access_token}" \
                -H "x-goog-api-version:2" \
                -T "${zip}")
            state=$(echo "${res}" | jq -r '.uploadState // empty')
            if [[ "${state}" != "SUCCESS" ]]; then
                echo "ERROR: Chrome Web Store upload failed for ${ext_id}: ${res}"
                return 1
            fi
        }
        _chrome_publish() {
            local ext_id="$1"
            local res pub_status
            res=$(curl -sSf -X POST \
                "https://www.googleapis.com/chromewebstore/v1.1/items/${ext_id}/publish" \
                -H "Authorization: Bearer ${access_token}" \
                -H "x-goog-api-version:2" \
                -H "Content-Length: 0")
            pub_status=$(echo "${res}" | jq -r '.status[0] // .status // empty')
            if [[ "${pub_status}" != "OK" && "${pub_status}" != "PUBLISHED_WITH_FRICTION_WARNING" ]]; then
                echo "ERROR: Chrome Web Store publish failed for ${ext_id}: ${res}"
                return 1
            fi
        }

        if [[ -n "${CHROME_KIE_EDITORS_EXTENSION_ID:-}" ]]; then
            _chrome_upload "${CHROME_KIE_EDITORS_EXTENSION_ID}" "${kie_editors_zip}"
            _chrome_publish "${CHROME_KIE_EDITORS_EXTENSION_ID}"
            echo "Chrome extension published."
        else
            echo "WARN: CHROME_KIE_EDITORS_EXTENSION_ID not set — skipping Chrome publish"
        fi
    fi
}

# ---------------------------------------------------------------------------
# 3. VSCode extensions
# ---------------------------------------------------------------------------
release_vscode() {
    echo ""
    echo "--- VSCode extensions ---"

    declare -A EXTENSIONS=(
        ["bpmn-vscode-extension"]="bpmn-vscode-extension"
        ["dmn-vscode-extension"]="dmn-vscode-extension"
        ["drl-vscode-extension"]="drl-vscode-extension"
        ["pmml-vscode-extension"]="pmml-vscode-extension"
        ["vscode-extension-kogito-bundle"]="kogito-bundle-vscode-extension"
        ["vscode-extension-kie-ba-bundle"]="business-automation-bundle-vscode-extension"
        ["extended-services-vscode-extension"]="extended-services-vscode-extension"
    )

    if [[ "${SKIP_BUILD}" == "false" ]]; then
        local filter=""
        for ext in "${!EXTENSIONS[@]}"; do filter="${filter} -F ${ext}..."; done
        echo "Building VSCode extensions..."
        pnpm ${filter} build:prod
    fi

    if [[ "${RC_MODE}" == "true" ]]; then
        mkdir -p "${ARTIFACTS_DIR}"
        for ext in "${!EXTENSIONS[@]}"; do
            local suffix="${EXTENSIONS[$ext]}"
            local vsix
            vsix=$(find "packages/${ext}" -maxdepth 2 -name "*.vsix" 2>/dev/null | head -1)
            if [[ -n "${vsix}" ]]; then
                local dest="${ARTIFACTS_DIR}/apache-kie-${VERSION}-incubating-${suffix}.vsix"
                echo "  COPY  $(basename "${dest}")"
                cp "${vsix}" "${dest}"
            else
                echo "  SKIP  ${ext} (no .vsix found)"
            fi
        done
        echo "VSIX files in: ${ARTIFACTS_DIR}/"
    fi

    if [[ "${PUBLISH}" == "true" ]]; then
        if [[ -z "${VSCE_PAT:-}" ]]; then
            echo "ERROR: VSCE_PAT is required for publishing VSCode extensions"
            return 1
        fi
        echo "Publishing to VSCode Marketplace..."
        for ext in "${!EXTENSIONS[@]}"; do
            find "packages/${ext}" -maxdepth 2 -name "*.vsix" | while read -r vsix; do
                echo "  Publishing: ${vsix}"
                npx vsce publish --packagePath "${vsix}" --pat "${VSCE_PAT}"
            done
        done
        echo "VSCode extensions published."
    fi

    if [[ "${PUBLISH}" == "true" && -n "${UPLOAD_URL:-}" && -n "${GITHUB_TOKEN:-}" ]]; then
        echo "Uploading .vsix files to GitHub Release..."
        for ext in "${!EXTENSIONS[@]}"; do
            find "packages/${ext}" -maxdepth 2 -name "*.vsix" | while read -r vsix; do
                upload_github_asset "${vsix}" "$(basename "${vsix}")" "application/zip"
            done
        done
    fi
}

# ---------------------------------------------------------------------------
# 4. Container images
# ---------------------------------------------------------------------------
release_container_images() {
    echo ""
    echo "--- Container images ---"

    declare -A IMAGES=(
        ["cors-proxy"]="packages/cors-proxy-image:incubator-kie-cors-proxy"
        ["sandbox-webapp"]="packages/kie-sandbox-webapp-image:incubator-kie-sandbox-webapp"
        ["sandbox-extended-services"]="packages/kie-sandbox-extended-services-image:incubator-kie-sandbox-extended-services"
        ["sandbox-dev-deployment-base"]="packages/dev-deployment-base-image:incubator-kie-sandbox-dev-deployment-base"
        ["sandbox-dev-deployment-dmn-form-webapp"]="packages/dev-deployment-dmn-form-webapp-image:incubator-kie-sandbox-dev-deployment-dmn-form-webapp"
        ["sandbox-dev-deployment-quarkus-blank-app"]="packages/dev-deployment-quarkus-blank-app-image:incubator-kie-sandbox-dev-deployment-quarkus-blank-app"
        ["kogito-management-console"]="packages/kogito-management-console:incubator-kie-kogito-management-console"
    )

    export KIE_TOOLS_BUILD__buildContainerImages=true

    if [[ "${SKIP_BUILD}" == "false" ]]; then
        if ! docker info &>/dev/null; then
            echo "WARN: Docker is not running — skipping container image build"
            return 0
        fi
        echo "Building container images..."
        for artifact_name in "${!IMAGES[@]}"; do
            local mapping="${IMAGES[$artifact_name]}"
            local pkg_path="${mapping%%:*}"
            if [[ ! -d "${pkg_path}" ]]; then
                echo "ERROR: Image package directory ${pkg_path} not found"
                return 1
            fi
            local pkg_name
            pkg_name=$(node -p "require('./${pkg_path}/package.json').name" 2>/dev/null || basename "${pkg_path}")
            echo "  BUILD  ${artifact_name} (${pkg_name})"
            pnpm --filter "${pkg_name}..." build:prod
        done
    else
        if ! docker info &>/dev/null; then
            echo "SKIP: Docker is not running — cannot save container image tarballs with --skip-build"
            return 0
        fi
    fi

    local output_dir="${ARTIFACTS_DIR}"
    [[ "${RC_MODE}" == "true" ]] && mkdir -p "${output_dir}"

    echo "Tagging images..."
    for artifact_name in "${!IMAGES[@]}"; do
        local mapping="${IMAGES[$artifact_name]}"
        local pkg_path="${mapping%%:*}"
        local image_name="${mapping##*:}"
        if [[ ! -d "${pkg_path}" ]]; then
            echo "ERROR: Image package directory ${pkg_path} not found"
            return 1
        fi
        local target_image="${REGISTRY}/${image_name}:${VERSION}"

        # Each image package builds docker.io/apache/<image_name>:<VERSION> by default.
        # Check that canonical tag first, then fall back to unqualified variants.
        local source_image=""
        for candidate in \
            "docker.io/apache/${image_name}:${VERSION}" \
            "apache/${image_name}:${VERSION}" \
            "${image_name}:${VERSION}"; do
            if docker image inspect "${candidate}" &>/dev/null; then
                source_image="${candidate}"
                break
            fi
        done

        if [[ -n "${source_image}" ]]; then
            docker tag "${source_image}" "${target_image}"
        else
            echo "ERROR: Could not find local image for ${artifact_name} (expected docker.io/apache/${image_name}:${VERSION})"
            return 1
        fi

        if [[ "${RC_MODE}" == "true" ]]; then
            if docker image inspect "${target_image}" &>/dev/null; then
                local tarball="${output_dir}/apache-kie-${VERSION}-incubating-${artifact_name}-image.tar.gz"
                echo "  SAVE  ${tarball}"
                docker save "${target_image}" | gzip > "${tarball}"
            else
                echo "ERROR: Image ${target_image} not found locally for RC export"
                return 1
            fi
        fi
    done

    if [[ "${PUBLISH}" == "true" ]]; then
        if [[ -z "${DOCKER_USERNAME:-}" || -z "${DOCKER_PASSWORD:-}" ]]; then
            echo "ERROR: DOCKER_USERNAME and DOCKER_PASSWORD are required for publishing container images"
            return 1
        fi
        echo "Pushing images to ${REGISTRY}..."
        echo "${DOCKER_PASSWORD}" | docker login "$(echo "${REGISTRY}" | cut -d/ -f1)" \
            -u "${DOCKER_USERNAME}" --password-stdin
        for artifact_name in "${!IMAGES[@]}"; do
            local mapping="${IMAGES[$artifact_name]}"
            local image_name="${mapping##*:}"
            local full_image="${REGISTRY}/${image_name}:${VERSION}"
            echo "  PUSH  ${full_image}"
            docker push "${full_image}"
        done
        docker logout "$(echo "${REGISTRY}" | cut -d/ -f1)"
        echo "Container images pushed."
    fi
}

# ---------------------------------------------------------------------------
# 5. Helm charts
# ---------------------------------------------------------------------------
release_helm_charts() {
    echo ""
    echo "--- Helm charts ---"

    if ! command -v helm &>/dev/null; then
        echo "WARN: helm not found — skipping Helm chart release"
        return 0
    fi

    declare -A CHARTS=(
        ["sandbox-helm-chart"]="packages/kie-sandbox-helm-chart"
        ["runtime-tools-console-helm-chart"]="packages/runtime-tools-consoles-helm-chart"
    )

    local output_dir="${ARTIFACTS_DIR}"
    mkdir -p "${output_dir}"

    local helm_tmp_pkg
    helm_tmp_pkg=$(mktemp -d)
    trap "rm -rf ${helm_tmp_pkg}" EXIT

    echo "Packaging Helm charts..."
    for slug in "${!CHARTS[@]}"; do
        local chart_pkg="${CHARTS[$slug]}"
        [[ -d "${chart_pkg}" ]] || { echo "ERROR: Helm chart directory ${chart_pkg} not found"; return 1; }

        local chart_yaml=""
        for candidate in "${chart_pkg}/src/Chart.yaml" "${chart_pkg}/Chart.yaml"; do
            [[ -f "${candidate}" ]] && { chart_yaml="${candidate}"; break; }
        done
        [[ -z "${chart_yaml}" ]] && { echo "ERROR: Chart.yaml not found in ${chart_pkg}"; return 1; }

        local chart_dir
        chart_dir="$(dirname "${chart_yaml}")"
        local tmp_dir
        tmp_dir=$(mktemp -d)
        cp -r "${chart_dir}/." "${tmp_dir}/chart"
        # Use portable sed: GNU sed on Linux and BSD sed on macOS both accept `sed -i ''`
        # only on macOS; to handle both, write to a temp file then move.
        local chart_yaml_tmp
        chart_yaml_tmp=$(mktemp)
        sed "s/^version:.*/version: ${VERSION}/" "${tmp_dir}/chart/Chart.yaml" \
            | sed "s/^appVersion:.*/appVersion: \"${VERSION}\"/" > "${chart_yaml_tmp}"
        mv "${chart_yaml_tmp}" "${tmp_dir}/chart/Chart.yaml"
        helm package "${tmp_dir}/chart" --destination "${helm_tmp_pkg}"
        rm -rf "${tmp_dir}"

        local found
        found=$(find "${helm_tmp_pkg}" -maxdepth 1 -name "*-${VERSION}.tgz" | head -1)
        if [[ -n "${found}" ]]; then
            # Copy as Apache-named .tar.gz for RC staging (SVN / vote archive).
            # The original .tgz in helm_tmp_pkg is preserved for `helm push`.
            cp "${found}" "${output_dir}/apache-kie-${VERSION}-incubating-${slug}.tar.gz"
            echo "  DONE  ${output_dir}/apache-kie-${VERSION}-incubating-${slug}.tar.gz"
        else
            echo "ERROR: Failed to package helm chart for ${slug}"
            return 1
        fi
    done

    if [[ "${PUBLISH}" == "true" ]]; then
        local helm_reg="${HELM_REGISTRY:-docker.io/apache}"
        if [[ -z "${helm_reg}" ]]; then
            echo "ERROR: HELM_REGISTRY is required for publishing Helm charts"
            return 1
        fi
        if [[ -n "${HELM_USERNAME:-}" && -n "${HELM_PASSWORD:-}" ]]; then
            echo "${HELM_PASSWORD}" | helm registry login "$(echo "${helm_reg}" | cut -d/ -f1)" \
                --username "${HELM_USERNAME}" --password-stdin
        fi
        # helm push requires the original .tgz produced by `helm package`.
        for tgz in "${helm_tmp_pkg}"/*-"${VERSION}".tgz; do
            if [[ -f "${tgz}" ]]; then
                echo "  PUSH  ${tgz} -> oci://${helm_reg}"
                helm push "${tgz}" "oci://${helm_reg}"
            fi
        done
        echo "Helm charts pushed."
    fi
    rm -rf "${helm_tmp_pkg}"
    trap - EXIT
}

# ---------------------------------------------------------------------------
# 6. GitHub Pages / webapp / accelerator
# ---------------------------------------------------------------------------
release_github_pages() {
    echo ""
    echo "--- GitHub Pages / webapp / accelerator ---"

    if [[ "${SKIP_BUILD}" == "false" ]]; then
        echo "Building online-editor, kie-editors-standalone, chrome-extension-pack-kogito-kie-editors, kie-sandbox-accelerator-quarkus..."
        export ONLINE_EDITOR__buildInfo="${VERSION}"
        export ONLINE_EDITOR__extendedServicesCompatibleVersion="${VERSION}"
        export ONLINE_EDITOR__devDeploymentBaseImageRegistry="docker.io"
        export ONLINE_EDITOR__devDeploymentBaseImageAccount="apache"
        export ONLINE_EDITOR__devDeploymentBaseImageName="incubator-kie-sandbox-dev-deployment-base"
        export ONLINE_EDITOR__devDeploymentBaseImageTag="${VERSION}"
        export ONLINE_EDITOR__devDeploymentDmnFormWebappImageRegistry="docker.io"
        export ONLINE_EDITOR__devDeploymentDmnFormWebappImageAccount="apache"
        export ONLINE_EDITOR__devDeploymentDmnFormWebappImageName="incubator-kie-sandbox-dev-deployment-dmn-form-webapp"
        export ONLINE_EDITOR__devDeploymentDmnFormWebappImageTag="${VERSION}"
        export ONLINE_EDITOR__devDeploymentQuarkusBlankAppImageRegistry="docker.io"
        export ONLINE_EDITOR__devDeploymentQuarkusBlankAppImageAccount="apache"
        export ONLINE_EDITOR__devDeploymentQuarkusBlankAppImageName="incubator-kie-sandbox-dev-deployment-quarkus-blank-app"
        export ONLINE_EDITOR__devDeploymentQuarkusBlankAppImageTag="${VERSION}"
        pnpm \
            -F "@kie-tools/online-editor..." \
            -F "@kie-tools/kie-editors-standalone..." \
            -F "@kie-tools/chrome-extension-pack-kogito-kie-editors..." \
            -F "@kie-tools/kie-sandbox-accelerator-quarkus..." \
            build:prod
    fi

    if [[ "${RC_MODE}" == "true" ]]; then
        mkdir -p "${ARTIFACTS_DIR}"
        for src_path_suffix in \
            "packages/online-editor/dist:apache-kie-${VERSION}-incubating-sandbox-webapp.zip" \
            "packages/kie-editors-standalone/dist:apache-kie-${VERSION}-incubating-business-automation-standalone-editors.zip" \
            "packages/kie-sandbox-accelerator-quarkus/dist:apache-kie-${VERSION}-incubating-sandbox-accelerator-quarkus.zip"
        do
            local src="${REPO_ROOT}/${src_path_suffix%%:*}"
            local zip_name="${src_path_suffix##*:}"
            if [[ -d "${src}" ]]; then
                echo "  ZIP  ${zip_name}"
                (cd "${src}" && zip -r "${ARTIFACTS_DIR}/${zip_name}" .)
            else
                echo "  SKIP  ${zip_name} (${src} not found)"
            fi
        done
        echo "RC artifacts in: ${ARTIFACTS_DIR}"
    fi

    if [[ "${PUBLISH}" == "true" ]]; then
        if [[ -z "${GITHUB_TOKEN:-}" ]]; then
            echo "ERROR: GITHUB_TOKEN is required for publishing to GitHub Pages"
            return 1
        fi

        local kogito_online_repo="https://github.com/apache/incubator-kie-kogito-online.git"
        local accelerator_repo="https://github.com/apache/incubator-kie-sandbox-quarkus-accelerator.git"
        local tmp_dir
        tmp_dir=$(mktemp -d)
        trap "rm -rf ${tmp_dir}" EXIT

        git clone --branch gh-pages --depth 1 "${kogito_online_repo}" "${tmp_dir}/kogito-online" \
            --config "http.extraHeader=Authorization: Bearer ${GITHUB_TOKEN}"
        (
            cd "${tmp_dir}/kogito-online"
            git config user.email "asf-ci-kie@jenkins.kie.apache.org"
            git config user.name "Apache KIE Release Bot"
            git config "http.extraHeader" "Authorization: Bearer ${GITHUB_TOKEN}"
            find . -maxdepth 1 ! -name '.' ! -name '.git' ! -name '.github' ! -name 'dev' ! -name 'editors' ! -name 'standalone' \
                ! -name 'chrome-extension' ! -name '.nojekyll' ! -name 'CNAME' -exec rm -rf {} +

            local online_dist="${REPO_ROOT}/packages/online-editor/dist"
            if [[ -d "${online_dist}" ]]; then
                local editors_dir="editors/${VERSION}"
                rm -rf "${editors_dir}" && mkdir -p "${editors_dir}"
                rm -rf editors/latest && ln -s "${VERSION}" editors/latest
                cp -r "${online_dist}/." .
            fi
            local standalone_dist="${REPO_ROOT}/packages/kie-editors-standalone/dist"
            [[ -d "${standalone_dist}" ]] && cp -r "${standalone_dist}/." "editors/${VERSION}/"
            local chrome_dist="${REPO_ROOT}/packages/chrome-extension-pack-kogito-kie-editors/dist"
            if [[ -d "${chrome_dist}" ]]; then
                local chrome_dir="chrome-extension/${VERSION}"
                rm -rf "${chrome_dir}" && mkdir -p "${chrome_dir}"
                cp -r "${chrome_dist}/fonts" "${chrome_dir}/" 2>/dev/null || true
                cp "${chrome_dist}"/*-envelope.* "${chrome_dir}/" 2>/dev/null || true
            fi
            git add .
            git commit -m "Deploy ${VERSION} (Online Editor + Standalone Editors + Chrome Extension editors)" \
                || echo "  (nothing to commit)"
            git push origin gh-pages
        )
        echo "Pushed to GitHub Pages."

        local accel_content="${REPO_ROOT}/packages/kie-sandbox-accelerator-quarkus/dist/git-repo-content"
        if [[ -d "${accel_content}" ]]; then
            git clone --depth 1 "${accelerator_repo}" "${tmp_dir}/accelerator" \
                --config "http.extraHeader=Authorization: Bearer ${GITHUB_TOKEN}"
            (
                cd "${tmp_dir}/accelerator"
                git config user.email "asf-ci-kie@jenkins.kie.apache.org"
                git config user.name "Apache KIE Release Bot"
                git config "http.extraHeader" "Authorization: Bearer ${GITHUB_TOKEN}"
                git checkout --orphan "${VERSION}"
                # Remove all staged and untracked content from the previous checkout
                # before populating the orphan branch with the release payload.
                git rm -rf . 2>/dev/null || true
                git clean -fdx
                cp -r "${accel_content}/." .
                git add .
                git commit -m "Apache KIE Sandbox Quarkus Accelerator ${VERSION}"
                git tag "${VERSION}"
                git push origin "refs/heads/${VERSION}:refs/heads/${VERSION}" "refs/tags/${VERSION}:refs/tags/${VERSION}"
            )
            echo "Pushed accelerator to tag ${VERSION}."
        fi
    fi
}

# ---------------------------------------------------------------------------
# 7. dev-deployment-upload-service
# ---------------------------------------------------------------------------
release_dev_deployment_upload_service() {
    echo ""
    echo "--- dev-deployment-upload-service ---"

    if [[ "${SKIP_BUILD}" == "false" ]]; then
        echo "Building dev-deployment-upload-service..."
        DDUS_VERSION="${VERSION}" pnpm -F "@kie-tools/dev-deployment-upload-service..." build:prod
    fi

    local dist="${REPO_ROOT}/packages/dev-deployment-upload-service/dist"
    if [[ ! -d "${dist}" ]]; then
        echo "ERROR: packages/dev-deployment-upload-service/dist not found"
        return 1
    fi

    declare -A PLATFORM_MAP=(
        ["dev-deployment-upload-service-darwin-arm64-${VERSION}.tar.gz"]="apache-kie-${VERSION}-incubating-sandbox-dev-deployment-upload-service-macOS-arm64.tar.gz"
        ["dev-deployment-upload-service-darwin-amd64-${VERSION}.tar.gz"]="apache-kie-${VERSION}-incubating-sandbox-dev-deployment-upload-service-macOS-x86.tar.gz"
        ["dev-deployment-upload-service-linux-amd64-${VERSION}.tar.gz"]="apache-kie-${VERSION}-incubating-sandbox-dev-deployment-upload-service-linux-x86.tar.gz"
        ["dev-deployment-upload-service-windows-amd64-${VERSION}.tar.gz"]="apache-kie-${VERSION}-incubating-sandbox-dev-deployment-upload-service-windows-x86.tar.gz"
    )

    if [[ "${RC_MODE}" == "true" ]]; then
        mkdir -p "${ARTIFACTS_DIR}"
        for src_name in "${!PLATFORM_MAP[@]}"; do
            local rc_name="${PLATFORM_MAP[$src_name]}"
            if [[ -f "${dist}/${src_name}" ]]; then
                cp "${dist}/${src_name}" "${ARTIFACTS_DIR}/${rc_name}"
                echo "  COPY  ${rc_name}"
            else
                echo "ERROR: Artifact ${dist}/${src_name} not found"
                return 1
            fi
        done
        echo "RC artifacts in: ${ARTIFACTS_DIR}"
    fi

    if [[ "${PUBLISH}" == "true" ]]; then
        if [[ -z "${GITHUB_TOKEN:-}" || -z "${UPLOAD_URL:-}" ]]; then
            echo "ERROR: GITHUB_TOKEN and --upload-url are required for publishing dev-deployment-upload-service"
            return 1
        fi
        for src_name in "${!PLATFORM_MAP[@]}"; do
            upload_github_asset "${dist}/${src_name}" "${src_name}" "application/gzip"
        done
        echo "dev-deployment-upload-service binaries uploaded."
    fi
}

# ---------------------------------------------------------------------------
# 8. Source tarball (always created; no --publish step)
# ---------------------------------------------------------------------------
release_source_tarball() {
    echo ""
    echo "--- Source tarball ---"
    mkdir -p "${ARTIFACTS_DIR}"
    local zipfile="${ARTIFACTS_DIR}/apache-kie-${VERSION}-incubating-sources.zip"
    local archive_ref="${GIT_REF}"

    # If archiving HEAD and there are uncommitted working tree modifications (e.g. local version bumps),
    # create a stash commit to ensure the working tree state is preserved in the archive.
    if [[ "${archive_ref}" == "HEAD" ]] && ! git diff --quiet HEAD 2>/dev/null; then
        local stash_sha
        stash_sha=$(git stash create 2>/dev/null || true)
        if [[ -n "${stash_sha}" ]]; then
            archive_ref="${stash_sha}"
        fi
    fi

    echo "Creating git archive from ref ${archive_ref}..."
    git archive --format=zip --prefix="apache-kie-${VERSION}-incubating/" "${archive_ref}" > "${zipfile}"
    if ! unzip -t "${zipfile}" > /dev/null 2>&1; then
        echo "ERROR: Source zip verification failed"
        return 1
    fi
    local size
    size=$(du -h "${zipfile}" | cut -f1)
    echo "Created: ${zipfile} (${size})"
    local zip_contents
    zip_contents=$(unzip -l "${zipfile}")
    for required in LICENSE NOTICE DISCLAIMER-WIP; do
        echo "${zip_contents}" | grep -q "apache-kie-${VERSION}-incubating/${required}" \
            || echo "WARN: ${required} not found in source zip"
    done
}

# ---------------------------------------------------------------------------
# Run all release steps
# ---------------------------------------------------------------------------
FAILED=()
run_step() {
    local name="$1" fn="$2"
    local status=0
    (
        set -euo pipefail
        "${fn}"
    ) || status=$?

    if [[ ${status} -eq 0 ]]; then
        echo "✅ ${name} — OK"
    else
        echo "❌ ${name} — FAILED (exit code ${status})"
        FAILED+=("${name}")
    fi
}

run_step "npm-packages"                    release_npm_packages
run_step "chrome-extensions"               release_chrome_extensions
run_step "vscode"                          release_vscode
run_step "container-images"               release_container_images
run_step "helm-charts"                    release_helm_charts
run_step "github-pages"                   release_github_pages
run_step "dev-deployment-upload-service"  release_dev_deployment_upload_service
run_step "source-tarball"                 release_source_tarball

echo ""
echo "=========================================="
echo "Release Summary"
echo "=========================================="
if [[ ${#FAILED[@]} -eq 0 ]]; then
    echo "✅ All components completed successfully!"
    [[ "${RC_MODE}" == "true" || "${PUBLISH}" == "true" ]] && echo "Artifacts in: ${ARTIFACTS_DIR}"
    exit 0
else
    echo "❌ Failed components:"
    for c in "${FAILED[@]}"; do echo "  - ${c}"; done
    exit 1
fi
