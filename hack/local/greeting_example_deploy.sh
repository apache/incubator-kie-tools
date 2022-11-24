#!/bin/bash
# Copyright 2022 Red Hat, Inc. and/or its affiliates
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


registry=$1

if [ -z ${registry} ]; then
    registry="quay.io/${USERNAME}"
    echo "No registry given. Setting up default."
fi

img=${registry}/kogito-serverless-operator:local-test

echo "Using registry '${registry}'"
echo "Using image '${img}'"

kubectl create namespace kogito-workflows
kubectl create secret generic regcred --from-file=.dockerconfigjson=${HOME}/.docker/config.json --type=kubernetes.io/dockerconfigjson -n kogito-workflows

make docker-build docker-push IMG=${img}
make deploy IMG=${img}

cat config/samples/sw.kogito_v1alpha08_kogitoserverlessplatform.yaml | sed "s|address: .*|address: ${registry}|g" | kubectl apply -n kogito-workflows -f -

sleep 10

kubectl apply -f config/samples/sw.kogito_v1alpha08_kogitoserverlessworkflow.yaml -n kogito-workflows
