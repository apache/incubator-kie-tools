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

default_cluster_name="operator-test"

if [[ -z ${CLUSTER_NAME} ]]; then
    CLUSTER_NAME=$default_cluster_name
fi

source ./hack/export-version.sh
docker images
echo "---> Loading Operator Image into Kind"
kind load docker-image quay.io/kiegroup/kogito-cloud-operator:"${OP_VERSION}" --name ${CLUSTER_NAME}

node_name=$(kubectl get nodes -o jsonpath="{.items[0].metadata.name}")
echo "---> Checking internal loaded images on node ${node_name}"
docker exec "${node_name}" crictl images