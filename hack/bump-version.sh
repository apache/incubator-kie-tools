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
new_version=$1

if [ -z "${new_version}" ]; then
  echo "Please inform the new version. Use X.X.X"
  exit 1
fi

snapshot=$(if [[ "${new_version}" == *snapshot ]]; then echo 'true'; else echo 'false'; fi)

echo "Set new version to ${new_version} (set nightly image tag ? ${snapshot})"

sed -i "s|^VERSION ?=.*|VERSION ?= ${new_version}|g" Makefile
sed -i "s|newTag:.*|newTag: ${new_version}|g" config/manager/kustomization.yaml

if [ "${snapshot}" = 'true' ]; then
  imageTag="${imageTag}-nightly"
fi
sed -i "s|IMAGE_TAG_BASE ?=.*|IMAGE_TAG_BASE ?= ${imageTag}|g" Makefile
sed -i "s|newName:.*|newName: ${imageTag}|g" config/manager/kustomization.yaml

make vet
make bundle

echo "Version bumped to ${new_version}"
