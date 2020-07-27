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


source ./hack/export-version.sh

OUTPUT="${PWD}/build/_output/operatorhub"

echo "---> Output dir is set to ${OUTPUT}"
# clean up
rm -rf "${OUTPUT}"

mkdir -p "${OUTPUT}"

# copying all the kogito dependent operator to be added at manifests root.
rm -rf ~/operators/
git clone https://github.com/operator-framework/community-operators.git ~/operators/
cp -r ~/operators/community-operators/strimzi-kafka-operator "${OUTPUT}"
cp -r ~/operators/community-operators/keycloak-operator "${OUTPUT}"
cp -r ~/operators/community-operators/infinispan/ "${OUTPUT}"
cp -r ~/operators/community-operators/kogito-operator/ "${OUTPUT}"
rm -rf ~/operators/

cp -r "deploy/olm-catalog/kogito-operator/${OP_VERSION}/" "${OUTPUT}/kogito-operator/"
cp "deploy/olm-catalog/kogito-operator/kogito-operator.package.yaml"  "${OUTPUT}/kogito-operator/"

echo "---> Manifest files in the output directory for OLM verification"
ls -l "${OUTPUT}"
echo "---> Manifest files in kogito-operator directory for OLM verification"
ls -l "${OUTPUT}/kogito-operator/"