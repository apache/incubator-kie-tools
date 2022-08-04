#!/bin/sh

KIE_TOOLS_FORK_USER=$1
GIT_REPO_BRANCH=$2
SRC_PKG_NAME=$3 #TODO: Make it work for more than one package name
GIT_REPO_REMOTE_URL="https://github.com/$KIE_TOOLS_FORK_USER/kie-tools"
GIT_REPO_CLONE_DIR='kie-tools'
ALWAYS_INCLUDED_DIRS='scripts repo docs'
git clone --filter=blob:none --no-checkout --depth 1 --branch $GIT_REPO_BRANCH $GIT_REPO_REMOTE_URL
cd $GIT_REPO_CLONE_DIR
git sparse-checkout init --cone
git checkout $GIT_REPO_BRANCH
git sparse-checkout set $ALWAYS_INCLUDED_DIRS
pnpm install-dependencies -F . --frozen-lockfile
echo "Discovering which packages to fetch for $SRC_PKG_NAME..."
PKG_DEPS=$(pnpm run --silent list-packages-dependencies repo $SRC_PKG_NAME)
echo $PKG_DEPS
eval "git sparse-checkout set $ALWAYS_INCLUDED_DIRS $PKG_DEPS"
ls -la packages
pnpm install-dependencies -F $SRC_PKG_NAME... -F . --frozen-lockfile && pnpm link-packages-with-self
pnpm -F "$SRC_PKG_NAME..." build:dev
pnpm pretty-quick
git status