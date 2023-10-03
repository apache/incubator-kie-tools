#!/bin/env bash
# Copyright 2019 Red Hat, Inc. and/or its affiliates
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


VERSION=$1

if [ -z "${VERSION}" ]; then
    VERSION=$(curl -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/kiegroup/kogito-operator/releases | python -c "import sys, json; print(json.load(sys.stdin)[0]['tag_name'])")
fi

echo "....... Installing Kogito Operator ${VERSION} ......."

declare url="https://github.com/kiegroup/kogito-operator/releases/download/${VERSION}/kogito-operator.yaml"

if [ -z "${NAMESPACE}" ]; then
  kubectl apply -f "${url}" -n "${NAMESPACE}"
else
  kubectl apply -f "${url}"
fi
