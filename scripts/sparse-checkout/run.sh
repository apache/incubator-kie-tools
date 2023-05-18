#!/bin/sh

ARGV=("$@")
KIE_TOOLS_ORG=$1
KIE_TOOLS_BRANCH=$2
KIE_TOOLS_PACKAGE_NAMES_TO_BUILD=("${ARGV[@]:2}")
KIE_TOOLS_PACKAGES_PNPM_FILTER_STRING=$(echo ${KIE_TOOLS_PACKAGE_NAMES_TO_BUILD[@]} | xargs -n1 -I{} echo -n "-F {}... " | xargs)
KIE_TOOLS_GIT_REMOTE_URL="https://github.com/$KIE_TOOLS_ORG/kie-tools"
KIE_TOOLS_CLONE_DIR_PATH='kie-tools'
KIE_TOOLS_PATHS_INCLUDED_BY_DEFAULT='scripts repo docs patches'

echo "[kie-tools-sparse-checkout] Starting..."
echo "KIE_TOOLS_ORG:                           $KIE_TOOLS_ORG"
echo "KIE_TOOLS_BRANCH:                        $KIE_TOOLS_BRANCH"
echo "KIE_TOOLS_PACKAGE_NAMES_TO_BUILD:        ${KIE_TOOLS_PACKAGE_NAMES_TO_BUILD[@]}"
echo "KIE_TOOLS_PACKAGES_PNPM_FILTER_STRING:   $KIE_TOOLS_PACKAGES_PNPM_FILTER_STRING"
echo "KIE_TOOLS_GIT_REMOTE_URL:                $KIE_TOOLS_GIT_REMOTE_URL"
echo "KIE_TOOLS_CLONE_DIR_PATH:                $KIE_TOOLS_CLONE_DIR_PATH"
echo "KIE_TOOLS_PATHS_INCLUDED_BY_DEFAULT:     $KIE_TOOLS_PATHS_INCLUDED_BY_DEFAULT"
echo ""

echo "[kie-tools-sparse-checkout] Cloning into $KIE_TOOLS_CLONE_DIR_PATH..."
git clone --filter=blob:none --no-checkout --depth 1 --branch $KIE_TOOLS_BRANCH $KIE_TOOLS_GIT_REMOTE_URL
cd $KIE_TOOLS_CLONE_DIR_PATH
git sparse-checkout init --cone
git checkout $KIE_TOOLS_BRANCH
git sparse-checkout set $KIE_TOOLS_PATHS_INCLUDED_BY_DEFAULT
echo ""

echo "[kie-tools-sparse-checkout] Installing scripts and root dependencies..."
pnpm bootstrap:root --frozen-lockfile
echo ""

echo "[kie-tools-sparse-checkout] Listing paths of packages to fetch for (${KIE_TOOLS_PACKAGE_NAMES_TO_BUILD[@]})..."
KIE_TOOLS_PACKAGE_PATHS_TO_FETCH=$(pnpm kie-tools--list-packages-dependencies ./repo "${KIE_TOOLS_PACKAGE_NAMES_TO_BUILD[@]}")
echo $KIE_TOOLS_PACKAGE_PATHS_TO_FETCH | xargs -n1
echo ""

echo "[kie-tools-sparse-checkout] Fetching packages..."
eval "git sparse-checkout set $KIE_TOOLS_PATHS_INCLUDED_BY_DEFAULT $KIE_TOOLS_PACKAGE_PATHS_TO_FETCH"
echo ""

echo "[kie-tools-sparse-checkout] Installing packages dependencies..."
eval "pnpm bootstrap:packages $KIE_TOOLS_PACKAGES_PNPM_FILTER_STRING --frozen-lockfile"
echo ""

echo "[kie-tools-sparse-checkout] Building packages with 'build:dev'..."
eval "pnpm $KIE_TOOLS_PACKAGES_PNPM_FILTER_STRING build:dev"
echo ""

echo "[kie-tools-sparse-checkout] Formatting changes..."
pnpm pretty-quick
echo ""

echo "[kie-tools-sparse-checkout] Git status..."
git status
echo ""

echo "[kie-tools-sparse-checkout] Done."
