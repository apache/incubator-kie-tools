#!/bin/bash
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

set -e

script_dir_path=$(dirname "${BASH_SOURCE[0]}")
source "${script_dir_path}"/env.sh

imageName=$(pnpm build-env sontaflowOperator.registry)/$(pnpm build-env sontaflowOperator.account)/$(pnpm build-env sontaflowOperator.name)
imageTag=$(pnpm build-env sontaflowOperator.tag)
version=$(pnpm build-env sontaflowOperator.version)

targetSonataflowBuilderImage=$(pnpm build-env sontaflowOperator.sonataflowBuilderImage)
targetSonataflowDevModeImage=$(pnpm build-env sontaflowOperator.sonataflowDevModeImage)

if [ -z "${version}" ]; then
  echo "Please inform the new version"
  exit 1
fi

newMajorMinorVersion=${version%.*}

targetSonataflowOperatorImage="${imageName}:${imageTag}"

echo "Set new version to ${version} (majorMinor = ${newMajorMinorVersion}, imageName:imageTag = ${targetSonataflowOperatorImage})"

node -p "require('replace-in-file').sync({ from: /\bVERSION\ \?=.*\b/g, to: 'VERSION ?= ${version}', files: ['./Makefile'] });"
node -p "require('replace-in-file').sync({ from: /\bREDUCED_VERSION\ \?=.*\b/g, to: 'REDUCED_VERSION ?= ${newMajorMinorVersion}', files: ['./Makefile'] });"
node -p "require('replace-in-file').sync({ from: /\bIMAGE_TAG\ \?=.*\b/g, to: 'IMAGE_TAG ?= ${imageTag}', files: ['./Makefile'] });"
node -p "require('replace-in-file').sync({ from: /\bIMAGE_TAG_BASE\ \?=.*\b/g, to: 'IMAGE_TAG_BASE ?= ${imageName}', files: ['./Makefile'] });"

node -p "require('replace-in-file').sync({ from: /\bnewTag:.*\b/g, to: 'newTag: ${version}', files: ['./config/manager/kustomization.yaml'] });"
node -p "require('replace-in-file').sync({ from: /\bnewName:.*\b/g, to: 'newName: ${imageName}', files: ['./config/manager/kustomization.yaml'] });"

node -p "require('replace-in-file').sync({ from: /\bversion: .*\b/g, to: 'version: ${version}', files: ['./images/bundle.yaml'] });"
node -p "require('replace-in-file').sync({ from: /\bversion: .*\b/g, to: 'version: ${version}', files: ['./images/manager.yaml'] });"

# Update sonataflow-* images
node -p "require('replace-in-file').sync({ from: /docker\.io\/apache\/incubator-kie-sonataflow-builder:[\w\.]*/g, to: '${targetSonataflowBuilderImage}', files: ['**/*.yaml', '**/*.containerfile', '**/*.dockerfile', '**/*Dockerfile', '**/*.go'] });"
node -p "require('replace-in-file').sync({ from: /docker\.io\/apache\/incubator-kie-sonataflow-devmode:[\w\.]*/g, to: '${targetSonataflowDevModeImage}', files: ['**/*.yaml', '**/*.containerfile', '**/*.dockerfile', '**/*Dockerfile', '**/*.go'] });"
node -p "require('replace-in-file').sync({ from: /docker\.io\/apache\/incubator-kie-sonataflow-operator:[\w\.]*/g, to: '${targetSonataflowOperatorImage}', files: ['**/*.yaml', '**/*.containerfile', '**/*.dockerfile', '**/*Dockerfile', '**/*.go'] });"
node -p "require('replace-in-file').sync({ from: /sonataflow-operator-system\/sonataflow-operator:[\w\.]*/g, to: 'sonataflow-operator-system/sonataflow-operator:${imageTag}', files: ['**/*.yaml'] });"

node -p "require('replace-in-file').sync({ from: /\bOperatorVersion = .*/g, to: 'OperatorVersion = \"${version}\"', files: ['version/version.go'] });"
node -p "require('replace-in-file').sync({ from: /\btagVersion = .*/g, to: 'tagVersion = \"${imageTag}\"', files: ['version/version.go'] });"
node -p "require('replace-in-file').sync({ from: /\bcontainerImage:.*\b/g, to: 'containerImage: ${targetSonataflowOperatorImage}', files: ['$(getCsvFile)'] });"

make generate-all
make vet

echo "Version bumped to ${new_version}"
