#!/usr/bin/env bash
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

# Simple usage: /bin/sh scripts/push-local-registry.sh ${REGISTRY} ${SHORTENED_LATEST_VERSION} ${NS}

BUILD_ENGINE="docker"

registry=${REGISTRY:-{1}}
version=${2:-latest}
namespace=${3:-openshift}

if [ "${registry}x" == "x"  ]; then
    echo "No registry provided, please set the env REGISTRY or set it as parameter to this script"
    echo "Simple usage: /bin/sh scripts/push-local-registry.sh ${REGISTRY} ${SHORTENED_LATEST_VERSION} ${NS}"
    exit 1
fi
if [ "${version}" == "latest"  ]; then
    echo "No version provided, latest will be used"
fi
if [ "${namespace}" == "openshift"  ]; then
    echo "No namespace provided, images will be installed on openshift namespace"
fi

echo "Images version ${version} will be pushed to registry ${registry}"

while read image; do
    echo "tagging image ${image} to ${registry}/${namespace}/${image}:${version}"
    ${BUILD_ENGINE} tag quay.io/kiegroup/${image}:${version} ${registry}/${namespace}/${image}:${version}
    echo "Deleting imagestream ${image} if exists `oc delete oc -n ${namespace} ${image}`"
    ${BUILD_ENGINE} push ${registry}/${namespace}/${image}:${version}
done <<<$(python scripts/list-images.py)

