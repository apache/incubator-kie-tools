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


registry=$1

if [ -z "${registry}" ]; then
    registry="quay.io/${USERNAME}"
    echo "No registry given. Setting up default."
fi

img=${registry}/kogito-serverless-operator:local-test

echo "Using registry '${registry}'"
echo "Using image '${img}'"

kubectl create namespace sonataflow
kubectl create secret generic regcred --from-file=.dockerconfigjson=${HOME}/.docker/config.json --type=kubernetes.io/dockerconfigjson -n sonataflow

# make sure cekit is installed: https://docs.cekit.io/en/latest/handbook/installation/instructions.html
make container-build BUILDER=docker IMG="${img}"
make deploy IMG="${img}"

# shellcheck disable=SC2002
cat config/samples/sonataflow.org_v1alpha08_sonataflowplatform.yaml | sed "s|address: .*|address: ${registry}|g" | kubectl apply -n sonataflow -f -

sleep 10

kubectl apply -f config/samples/sonataflow.org_v1alpha08_sonataflowplatform.yaml -n sonataflow
