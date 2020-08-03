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
source ./hack/ci/operator-ensure-manifests.sh
source ./hack/export-version.sh

default_cluster_name="operator-test"

if [[ -z ${CLUSTER_NAME} ]]; then
    CLUSTER_NAME=$default_cluster_name
fi

CATALOG_IMAGE="operatorhubio-catalog:temp"
OP_PATH="community-operators/kogito-operator"
INSTALL_MODE="SingleNamespace"
OPERATOR_TESTING_IMAGE="quay.io/operator-framework/operator-testing:latest"

if [ -z ${KUBECONFIG} ]; then
    KUBECONFIG=${HOME}/.kube/config
    echo "---> KUBECONFIG environment variable not set, defining to:"
    ls -la ${KUBECONFIG}
fi


echo "---> Updating All CSV files to imagePullPolicy: Never"
find "${OUTPUT}/kogito-operator/" -name '*.clusterserviceversion.yaml' -type f -print0 | xargs -0 sed -i 's/imagePullPolicy: Always/imagePullPolicy: Never/g'
echo "---> Resulting imagePullPolicy on manifest files"
grep -rn imagePullPolicy ${OUTPUT}/kogito-operator
echo "---> Building temporary catalog Image"
docker build --build-arg PERMISSIVE_LOAD=false -f ./hack/ci/operatorhubio-catalog.Dockerfile -t ${CATALOG_IMAGE} .
echo "---> Loading Catalog Image into Kind"
kind load docker-image ${CATALOG_IMAGE} --name ${CLUSTER_NAME}

# running tests
docker pull ${OPERATOR_TESTING_IMAGE}
docker run --network=host --rm \
    -v ${KUBECONFIG}:/root/.kube/config:z \
    -v ${OUTPUT}/:/community-operators:z ${OPERATOR_TESTING_IMAGE} \
    operator.test --no-print-directory \
    OP_PATH=${OP_PATH} VERBOSE=true NO_KIND=0 CATALOG_IMAGE=${CATALOG_IMAGE} INSTALL_MODE=${INSTALL_MODE}
