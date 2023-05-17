#!/bin/bash
# Copyright 2020 Red Hat, Inc. and/or its affiliates
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
set -e

script_dir_path=$(dirname "${BASH_SOURCE[0]}")
source "${script_dir_path}"/env.sh

imageTag='quay.io/kiegroup/kogito-serverless-operator'
# shellcheck disable=SC2034
old_version=$(getOperatorVersion)
latest_version=$(getOperatorLatestVersion)
new_version=$1

if [ -z "${new_version}" ]; then
  echo "Please inform the new version"
  exit 1
fi

imageSuffix=$(if [[ "${new_version}" == *snapshot ]]; then echo '-nightly'; else echo ''; fi)

oldMajorMinorVersion=${old_version%.*}
newMajorMinorVersion=${new_version%.*}
if [ "${old_version}" = "${latest_version}" ]; then
  oldMajorMinorVersion='latest'
fi

echo "Set new version to ${new_version} (nightly = ${snapshot}, majorMinor = ${majorMinor})"

sed -i "s|^VERSION ?=.*|VERSION ?= ${new_version}|g" Makefile
sed -i "s|newTag:.*|newTag: ${new_version}|g" config/manager/kustomization.yaml

sed -i "s|IMAGE_TAG_BASE ?=.*|IMAGE_TAG_BASE ?= ${imageTag}${imageSuffix}|g" Makefile
sed -i "s|newName:.*|newName: ${imageTag}${imageSuffix}|g" config/manager/kustomization.yaml

# Update kogito-swf-* images
find . -name "*.yaml" -exec sed -i "s|quay.io/kiegroup/kogito-swf-builder.*:${oldMajorMinorVersion}|quay.io/kiegroup/kogito-swf-builder${imageSuffix}:${newMajorMinorVersion}|" {} +
sed -i "s|quay.io/kiegroup/kogito-swf-builder.*:${oldMajorMinorVersion}|quay.io/kiegroup/kogito-swf-builder${imageSuffix}:${newMajorMinorVersion}|" Dockerfile

find . -name "*.yaml" -exec sed -i "s|quay.io/kiegroup/kogito-swf-devmode.*:${oldMajorMinorVersion}|quay.io/kiegroup/kogito-swf-devmode${imageSuffix}:${newMajorMinorVersion}|" {} +
sed -i "s|quay.io/kiegroup/kogito-swf-devmode.*:${oldMajorMinorVersion}|quay.io/kiegroup/kogito-swf-devmode${imageSuffix}:${newMajorMinorVersion}|" Dockerfile

sed -i -r "s|OperatorVersion =.*|OperatorVersion = \"${new_version}\"|g" version/version.go

make generate-all
make vet

echo "Version bumped to ${new_version}"
