#!/bin/bash
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
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

imageSuffix=$(if [[ "${new_version}" == 0.0.0 ]]; then echo '-nightly'; else echo ''; fi)

oldMajorMinorVersion=${old_version%.*}
newMajorMinorVersion=${new_version%.*}
if [ "${old_version}" = "${latest_version}" ]; then
  oldMajorMinorVersion='latest'
fi
if [ "${new_version}" = "${latest_version}" ]; then
  newMajorMinorVersion='latest'
fi

echo "Set new version to ${new_version} (img_suffix = '${imageSuffix}', majorMinor = ${newMajorMinorVersion})"


node -p "require('replace-in-file').sync({ from: /\bVERSION\ \?=.*\b/g, to: 'VERSION ?= ${new_version}', files: ['./Makefile'] });"
node -p "require('replace-in-file').sync({ from: /\bREDUCED_VERSION\ \?=.*\b/g, to: 'REDUCED_VERSION ?= ${newMajorMinorVersion}', files: ['./Makefile'] });"
node -p "require('replace-in-file').sync({ from: /\bIMAGE_TAG_BASE\ \?=.*\b/g, to: 'IMAGE_TAG_BASE ?= ${imageTag}${imageSuffix}', files: ['./Makefile'] });"

node -p "require('replace-in-file').sync({ from: /\bnewTag:.*\b/g, to: 'newTag: ${new_version}', files: ['./config/manager/kustomization.yaml'] });"
node -p "require('replace-in-file').sync({ from: /\bnewName:.*\b/g, to: 'newName: ${imageTag}${imageSuffix}', files: ['./config/manager/kustomization.yaml'] });"

node -p "require('replace-in-file').sync({ from: /\bversion: .*\b/g, to: 'version: ${new_version}', files: ['./images/bundle.yaml'] });"
node -p "require('replace-in-file').sync({ from: /\bversion: .*\b/g, to: 'version: ${new_version}', files: ['./images/manager.yaml'] });"

# Update kogito-swf-* images
node -p "require('replace-in-file').sync({ from: 'quay.io/kiegroup/kogito-swf-builder${imageSuffix}:${oldMajorMinorVersion}', to: 'quay.io/kiegroup/kogito-swf-builder${imageSuffix}:${newMajorMinorVersion}', files: ['**/*.yaml', '**/*.containerfile', '**/*.dockerfile', '**/*.Dockerfile', '**/*.go'] });"
node -p "require('replace-in-file').sync({ from: 'quay.io/kiegroup/kogito-swf-devmode${imageSuffix}:${oldMajorMinorVersion}', to: 'quay.io/kiegroup/kogito-swf-devmode${imageSuffix}:${newMajorMinorVersion}', files: ['**/*.yaml', '**/*.containerfile', '**/*.dockerfile', '**/*.Dockerfile', '**/*.go'] });"
node -p "require('replace-in-file').sync({ from: 'quay.io/kiegroup/kogito-serverless-operator${imageSuffix}:${oldMajorMinorVersion}', to: 'quay.io/kiegroup/kogito-serverless-operator${imageSuffix}:${newMajorMinorVersion}', files: ['**/*.yaml'] });"

node -p "require('replace-in-file').sync({ from: /\bOperatorVersion = .*/g, to: 'OperatorVersion = \"${new_version}\"', files: ['version/version.go'] });"
node -p "require('replace-in-file').sync({ from: /\bcontainerImage:.*\b/g, to: 'containerImage: ${imageTag}${imageSuffix}:${newMajorMinorVersion}', files: ['$(getCsvFile)'] });"

make generate-all
make vet

echo "Version bumped to ${new_version}"
