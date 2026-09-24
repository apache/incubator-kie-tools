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

# Release script for Apache KIE Tools — builds and optionally releases all components
# under a single unified build.
#
# Usage:
#   ./release.sh <version>            # dry run (build only, nothing published)
#   ./release.sh <version> --rc       # build + collect Apache RC artifacts
#   ./release.sh <version> --publish  # build + publish to all public registries
#
# Optional flags:
#   --skip-build           Skip pnpm build:prod (use existing dist/ output)
#   --upload-url <url>     GitHub Release upload URL (required by --publish for binary assets)
#   --registry <url>       Container registry (default: docker.io/apache)
#
# Credentials (required only for --publish, sourced from env):
#   NPM_TOKEN, VSCE_PAT,
#   CHROME_CLIENT_ID, CHROME_CLIENT_SECRET, CHROME_REFRESH_TOKEN, CHROME_KIE_EDITORS_EXTENSION_ID,
#   DOCKER_USERNAME, DOCKER_PASSWORD,
#   HELM_REGISTRY, HELM_USERNAME (optional), HELM_PASSWORD (optional),
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

shift || true
while [[ $# -gt 0 ]]; do
    case $1 in
        --publish)     PUBLISH=true;       shift ;;
        --rc)          RC_MODE=true;       shift ;;
        --skip-build)  SKIP_BUILD=true;    shift ;;
        --upload-url)  UPLOAD_URL="${2:-}"; shift 2 ;;
        --registry)    REGISTRY="${2:-}";  shift 2 ;;
        *)
            echo "Unknown option: $1"
            echo "Usage: $0 <version> [--publish] [--rc] [--skip-build] [--upload-url <url>] [--registry <url>]"
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
        echo "//registry.npmjs.org/:_authToken=${NPM_TOKEN}" > ~/.npmrc
        local pub_filter
        pub_filter=$(pnpm -r exec bash -c \
            'if [[ $(jq -r ".private" package.json) != "true" ]]; then echo "-F $(jq -r ".name" package.json)"; fi')
        pnpm ${pub_filter} exec bash -c '
            PKG_NAME=$(jq -r ".name" package.json)
            if ! npm view ${PKG_NAME}@'"${VERSION}"' name &>/dev/null; then
                echo "Publishing ${PKG_NAME}@'"${VERSION}"'"
                pnpm publish --no-git-checks --access public
            else
                echo "Skipping ${PKG_NAME}@'"${VERSION}"' (already published)"
            fi
        '
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

        local access_token
        access_token=$(curl -sS -X POST "https://oauth2.googleapis.com/token" \
            -d "client_id=${CHROME_CLIENT_ID}" \
            -d "client_secret=${CHROME_CLIENT_SECRET}" \
            -d "refresh_token=${CHROME_REFRESH_TOKEN}" \
            -d "grant_type=refresh_token" \
            | grep -o '"access_token":"[^"]*"' | cut -d'"' -f4)

        if [[ -z "${access_token}" ]]; then
            echo "ERROR: Failed to obtain Chrome Web Store OAuth token"
            return 1
        fi

        _chrome_upload() {
            local ext_id="$1" zip="$2"
            local state
            state=$(curl -sS -X PUT \
                "https://www.googleapis.com/upload/chromewebstore/v1.1/items/${ext_id}" \
                -H "Authorization: Bearer ${access_token}" \
                -H "x-goog-api-version:2" \
                -T "${zip}" | grep -o '"uploadState":"[^"]*"' | cut -d'"' -f4)
            [[ "${state}" == "SUCCESS" ]] || { echo "ERROR: upload failed for ${ext_id}"; return 1; }
        }
        _chrome_publish() {
            local ext_id="$1"
            curl -sS -X POST \
                "https://www.googleapis.com/chromewebstore/v1.1/items/${ext_id}/publish" \
                -H "Authorization: Bearer ${access_token}" \
                -H "x-goog-api-version:2" \
                -H "Content-Length:0" > /dev/null
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

    export KIE_TOOLS_BUILD__buildContainerImages=true

    if [[ "${SKIP_BUILD}" == "false" ]]; then
        if ! docker info &>/dev/null; then
            echo "WARN: Docker is not running — skipping container image build"
            return 0
        fi
        echo "Building container images..."
        for artifact_name in "${!IMAGES[@]}"; do
            local pkg_path="${IMAGES[$artifact_name]}"
            [[ -d "${pkg_path}" ]] || { echo "  SKIP  ${artifact_name} (${pkg_path} not found)"; continue; }
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

    local output_dir="${ARTIFACTS_DIR}/container-images"
    [[ "${RC_MODE}" == "true" ]] && mkdir -p "${output_dir}"

    echo "Tagging images..."
    for artifact_name in "${!IMAGES[@]}"; do
        local pkg_path="${IMAGES[$artifact_name]}"
        [[ -d "${pkg_path}" ]] || continue
        local full_image="${REGISTRY}/incubator-kie-${artifact_name}:${VERSION}"
        docker tag "$(node -p "require('./${pkg_path}/package.json').name" 2>/dev/null | sed 's|@kie-tools/||')" "${full_image}" 2>/dev/null \
            || docker tag "incubator-kie-${artifact_name}:latest" "${full_image}" 2>/dev/null \
            || docker tag "apache/incubator-kie-${artifact_name}:main" "${full_image}" 2>/dev/null \
            || echo "  WARN  could not tag ${artifact_name}"

        if [[ "${RC_MODE}" == "true" ]]; then
            if docker image inspect "${full_image}" &>/dev/null; then
                local tarball="${output_dir}/apache-kie-${VERSION}-incubating-${artifact_name}-image.tar.gz"
                echo "  SAVE  ${tarball}"
                docker save "${full_image}" | gzip > "${tarball}"
            else
                echo "  SKIP  ${artifact_name} (image not found locally)"
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
            local pkg_path="${IMAGES[$artifact_name]}"
            [[ -d "${pkg_path}" ]] || continue
            local full_image="${REGISTRY}/incubator-kie-${artifact_name}:${VERSION}"
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

    local output_dir="${ARTIFACTS_DIR}/helm-charts"
    mkdir -p "${output_dir}"

    echo "Packaging Helm charts..."
    for slug in "${!CHARTS[@]}"; do
        local chart_pkg="${CHARTS[$slug]}"
        [[ -d "${chart_pkg}" ]] || { echo "  SKIP  ${slug} (${chart_pkg} not found)"; continue; }

        local chart_yaml=""
        for candidate in "${chart_pkg}/src/Chart.yaml" "${chart_pkg}/Chart.yaml"; do
            [[ -f "${candidate}" ]] && { chart_yaml="${candidate}"; break; }
        done
        [[ -z "${chart_yaml}" ]] && { echo "  SKIP  ${slug} (Chart.yaml not found)"; continue; }

        local chart_dir
        chart_dir="$(dirname "${chart_yaml}")"
        local tmp_dir
        tmp_dir=$(mktemp -d)
        trap "rm -rf ${tmp_dir}" EXIT
        cp -r "${chart_dir}/." "${tmp_dir}/chart"
        sed -i.bak "s/^version:.*/version: ${VERSION}/" "${tmp_dir}/chart/Chart.yaml"
        sed -i.bak "s/^appVersion:.*/appVersion: \"${VERSION}\"/" "${tmp_dir}/chart/Chart.yaml"
        rm -f "${tmp_dir}/chart/Chart.yaml.bak"
        helm package "${tmp_dir}/chart" --destination "${output_dir}"
        local found
        found=$(find "${output_dir}" -maxdepth 1 -name "*-${VERSION}.tgz" | head -1)
        [[ -n "${found}" ]] && mv "${found}" "${output_dir}/apache-kie-${VERSION}-incubating-${slug}.tar.gz" \
            && echo "  DONE  ${output_dir}/apache-kie-${VERSION}-incubating-${slug}.tar.gz"
    done

    if [[ "${PUBLISH}" == "true" ]]; then
        if [[ -z "${HELM_REGISTRY:-}" ]]; then
            echo "ERROR: HELM_REGISTRY is required for publishing Helm charts"
            return 1
        fi
        if [[ -n "${HELM_USERNAME:-}" && -n "${HELM_PASSWORD:-}" ]]; then
            echo "${HELM_PASSWORD}" | helm registry login "$(echo "${HELM_REGISTRY}" | cut -d/ -f1)" \
                --username "${HELM_USERNAME}" --password-stdin
        fi
        for slug in "${!CHARTS[@]}"; do
            local tarball="${output_dir}/apache-kie-${VERSION}-incubating-${slug}.tar.gz"
            [[ -f "${tarball}" ]] && helm push "${tarball}" "oci://${HELM_REGISTRY}" && echo "  PUSH  ${tarball}"
        done
        echo "Helm charts pushed."
    fi
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

        local auth_url="${kogito_online_repo/https:\/\//https://${GITHUB_TOKEN}@}"
        git clone --branch gh-pages --depth 1 "${auth_url}" "${tmp_dir}/kogito-online"
        (
            cd "${tmp_dir}/kogito-online"
            git config user.email "asf-ci-kie@jenkins.kie.apache.org"
            git config user.name "Apache KIE Release Bot"
            find . -maxdepth 1 ! -name '.' ! -name 'dev' ! -name 'editors' ! -name 'standalone' \
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
            local auth_accel="${accelerator_repo/https:\/\//https://${GITHUB_TOKEN}@}"
            git clone --depth 1 "${auth_accel}" "${tmp_dir}/accelerator"
            (
                cd "${tmp_dir}/accelerator"
                git config user.email "asf-ci-kie@jenkins.kie.apache.org"
                git config user.name "Apache KIE Release Bot"
                git checkout --orphan "${VERSION}"
                cp -r "${accel_content}/." .
                git add .
                git commit -m "Apache KIE Sandbox Quarkus Accelerator ${VERSION}"
                git tag "${VERSION}"
                git push origin "${VERSION}"
            )
            echo "Pushed accelerator to tag ${VERSION}."
        fi
    fi
}

# ---------------------------------------------------------------------------
# 7. kn-plugin-workflow
# ---------------------------------------------------------------------------
release_kn_plugin_workflow() {
    echo ""
    echo "--- kn-plugin-workflow ---"

    if [[ "${SKIP_BUILD}" == "false" ]]; then
        echo "Building kn-plugin-workflow..."
        pnpm -F "@kie-tools/kn-plugin-workflow..." build:prod
    fi

    local dist="${REPO_ROOT}/packages/kn-plugin-workflow/dist"
    if [[ ! -d "${dist}" ]]; then
        echo "SKIP: packages/kn-plugin-workflow/dist not found"
        return 0
    fi

    if [[ "${RC_MODE}" == "true" ]]; then
        mkdir -p "${ARTIFACTS_DIR}"
        declare -A RC_ZIPS=(
            ["kn-workflow-linux-amd64"]="apache-kie-${VERSION}-incubating-sonataflow-knative-plugin-linux-x86.zip"
            ["kn-workflow-darwin-amd64"]="apache-kie-${VERSION}-incubating-sonataflow-knative-plugin-macOS-x86.zip"
            ["kn-workflow-darwin-arm64"]="apache-kie-${VERSION}-incubating-sonataflow-knative-plugin-macOS-arm64.zip"
            ["kn-workflow-windows-amd64.exe"]="apache-kie-${VERSION}-incubating-sonataflow-knative-plugin-windows-x86.zip"
        )
        (cd "${dist}" && for binary in "${!RC_ZIPS[@]}"; do
            local zip_name="${RC_ZIPS[$binary]}"
            [[ -f "${binary}" ]] && zip "${ARTIFACTS_DIR}/${zip_name}" "${binary}" \
                && echo "  ZIP  ${zip_name}" || echo "  SKIP ${binary} (not found)"
        done)
        echo "RC artifacts in: ${ARTIFACTS_DIR}"
    fi

    if [[ "${PUBLISH}" == "true" ]]; then
        if [[ -z "${GITHUB_TOKEN:-}" || -z "${UPLOAD_URL:-}" ]]; then
            echo "ERROR: GITHUB_TOKEN and --upload-url are required for publishing kn-plugin-workflow"
            return 1
        fi
        (cd "${dist}"
            upload_github_asset "kn-workflow-linux-amd64"       "kn-workflow-linux-amd64-${VERSION}"
            upload_github_asset "kn-workflow-darwin-amd64"      "kn-workflow-darwin-amd64-${VERSION}"
            upload_github_asset "kn-workflow-darwin-arm64"      "kn-workflow-darwin-arm64-${VERSION}"
            upload_github_asset "kn-workflow-windows-amd64.exe" "kn-workflow-windows-amd64-${VERSION}.exe"
        )
        echo "kn-plugin-workflow binaries uploaded."
    fi
}

# ---------------------------------------------------------------------------
# 8. dev-deployment-upload-service
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
        echo "SKIP: packages/dev-deployment-upload-service/dist not found"
        return 0
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
            [[ -f "${dist}/${src_name}" ]] && cp "${dist}/${src_name}" "${ARTIFACTS_DIR}/${rc_name}" \
                && echo "  COPY  ${rc_name}" || echo "  SKIP  ${src_name} (not found)"
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
# 9. Source tarball (always created; no --publish step)
# ---------------------------------------------------------------------------
release_source_tarball() {
    echo ""
    echo "--- Source tarball ---"
    mkdir -p "${ARTIFACTS_DIR}"
    local zipfile="${ARTIFACTS_DIR}/apache-kie-${VERSION}-incubating-sources.zip"
    echo "Creating git archive..."
    git archive --format=zip --prefix="apache-kie-${VERSION}-incubating/" HEAD > "${zipfile}"
    if ! unzip -t "${zipfile}" > /dev/null 2>&1; then
        echo "ERROR: Source zip verification failed"
        return 1
    fi
    local size
    size=$(du -h "${zipfile}" | cut -f1)
    echo "Created: ${zipfile} (${size})"
    for required in LICENSE NOTICE DISCLAIMER-WIP; do
        unzip -l "${zipfile}" | grep -q "apache-kie-${VERSION}-incubating/${required}" \
            || echo "WARN: ${required} not found in source zip"
    done
}

# ---------------------------------------------------------------------------
# Run all release steps
# ---------------------------------------------------------------------------
FAILED=()
run_step() {
    local name="$1" fn="$2"
    if "${fn}"; then
        echo "✅ ${name} — OK"
    else
        echo "❌ ${name} — FAILED"
        FAILED+=("${name}")
    fi
}

run_step "npm-packages"                    release_npm_packages
run_step "chrome-extensions"               release_chrome_extensions
run_step "vscode"                          release_vscode
run_step "container-images"               release_container_images
run_step "helm-charts"                    release_helm_charts
run_step "github-pages"                   release_github_pages
run_step "kn-plugin-workflow"             release_kn_plugin_workflow
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
