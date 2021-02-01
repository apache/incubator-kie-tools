#!/bin/bash
# Copyright 2021 Red Hat, Inc. and/or its affiliates
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

source ./hack/env.sh

version=$(getOperatorVersion)
if [[ -n ${IMAGE} ]]; then
    OPERATOR_IMAGE=${IMAGE}
else
    OPERATOR_IMAGE=quay.io/kiegroup/kogito-cloud-operator:${version}
fi
echo "======> Using ${OPERATOR_IMAGE} make sure it exists in docker-daemon"
KIND_IMAGE=kind-registry:5000/kiegroup/kogito-cloud-operator
echo "=====> tagging image"
docker tag ${OPERATOR_IMAGE} ${KIND_IMAGE}

echo "======> verifying image exists"
docker images ${KIND_IMAGE}
