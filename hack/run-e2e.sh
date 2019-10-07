#!/bin/bash
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


# runs end-2-end tests for the operator into a given namespace
namespace=$1
tag=$2
native=$3
maven_mirror=$4

if [ -z "$namespace" ]; then
  echo "Please inform the namespace where the tests will run"
  exit 1
fi

if [ -z "$tag" ]; then
  echo "-------- tag is empty, assuming default from the Operator"
fi

echo "-------- Running e2e tests with namespace=${namespace}, tag=${tag}, native=${native} and maven_mirror=${maven_mirror}"

# creates the namespace
oc create namespace ${namespace}
# gives permissions
oc create -f deploy/role.yaml -n ${namespace}
oc create -f deploy/service_account.yaml -n ${namespace}
oc create -f deploy/role_binding.yaml -n ${namespace}

# performs the test
DEBUG=true KOGITO_IMAGE_TAG=${tag} NATIVE=${native} MAVEN_MIRROR_URL=${maven_mirror} operator-sdk test local ./test/e2e --namespace ${namespace} --up-local --debug --go-test-flags "-timeout 30m"

# clean up
oc delete namespace ${namespace}