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

imageName=$(pnpm build-env sonataFlowOperator.registry)/$(pnpm build-env sonataFlowOperator.account)/$(pnpm build-env sonataFlowOperator.name)
imageTag=$(pnpm build-env sonataFlowOperator.buildTag)
platformTag=$(pnpm build-env sonataFlowOperator.platformTag)
version=$(pnpm build-env sonataFlowOperator.version)

if [ -z "${version}" ]; then
  echo "Please inform the new version"
  exit 1
fi

newMajorMinorVersion=${version%.*}

targetSonataflowOperatorImage="${imageName}:${imageTag}"

echo "Set new version to ${version} (majorMinor = ${newMajorMinorVersion}, imageName:imageTag = ${targetSonataflowOperatorImage})"

node -p "require('replace-in-file').sync({ from: /\bnewTag:.*\b/g, to: 'newTag: ${version}', files: ['./config/manager/kustomization.yaml'] });"
node -p "require('replace-in-file').sync({ from: /\bnewName:.*\b/g, to: 'newName: ${imageName}', files: ['./config/manager/kustomization.yaml'] });"

node -p "require('replace-in-file').sync({ from: /\bversion: .*\b/g, to: 'version: ${version}', files: ['./images/bundle.yaml'] });"
node -p "require('replace-in-file').sync({ from: /\bversion: .*\b/g, to: 'version: ${version}', files: ['./images/manager.yaml'] });"

node -p "require('replace-in-file').sync({ from: /\boperatorVersion = .*/g, to: 'operatorVersion = \"${version}\"', files: ['api/version/version.go'] });"
node -p "require('replace-in-file').sync({ from: /\bplatformTagVersion = .*/g, to: 'platformTagVersion = \"${platformTag}\"', files: ['api/version/version.go'] });"

echo "Version bumped to ${version}"
