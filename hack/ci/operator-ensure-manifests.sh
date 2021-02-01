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

# Script responsible for ensuring and correcting manifests as needed.
set -e

source ./hack/ci/ensure-image.sh

tempfolder=$(mktemp -d)
echo "Temporary folder is ${tempfolder}"

version=$(getOperatorVersion)
latest_released_version=$(getLatestOlmReleaseVersion)

git clone https://github.com/operator-framework/community-operators.git "${tempfolder}"
mkdir  "${tempfolder}/community-operators/kogito-operator/${version}/"
cp -r bundle/manifests/*.yaml "${tempfolder}/community-operators/kogito-operator/${version}/"


#replace image in target CSV
sed -i  "s|${OPERATOR_IMAGE}|${KIND_IMAGE}|g"  "${tempfolder}/community-operators/kogito-operator/${version}/kogito-operator.clusterserviceversion.yaml"
#
##ensure correct replace field is there
sed -i "s|replace.*|replaces: kogito-operator.v${latest_released_version}|g" "${tempfolder}/community-operators/kogito-operator/${version}/kogito-operator.clusterserviceversion.yaml"
#
sed -i "s|${latest_released_version}|${version}|g" "${tempfolder}/community-operators/kogito-operator/kogito-operator.package.yaml"
#
echo "---> verify CSV updates"
cat "${tempfolder}/community-operators/kogito-operator/${version}/kogito-operator.clusterserviceversion.yaml" | grep "${KIND_IMAGE}"
cat "${tempfolder}/community-operators/kogito-operator/${version}/kogito-operator.clusterserviceversion.yaml" | grep replaces

echo "---> Verify package.yaml updates"
cat "${tempfolder}/community-operators/kogito-operator/kogito-operator.package.yaml"