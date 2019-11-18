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
maven_mirror=$3
image=$4
tests=$5

if [ -z "$namespace" ]; then
  echo "Please inform the namespace where the tests will run"
  exit 1
fi

# creates the namespace
oc create namespace "${namespace}"

if [ -z "$tag" ]; then
  echo "-------- tag is empty, assuming default from the Operator"
fi

if [ -n "$image" ]; then
  echo "-------- image is set, using Kogito operator image ${image}"
  # operator permissions are created by operator-sdk
  E2E_PARAMS="${E2E_PARAMS} --image ${image}"
else
  echo "-------- using local operator code for testing"

  # gives permissions
  oc create -f deploy/role.yaml -n "${namespace}"
  oc create -f deploy/service_account.yaml -n "${namespace}"
  oc create -f deploy/role_binding.yaml -n "${namespace}"

  E2E_PARAMS="${E2E_PARAMS} --up-local"
fi

echo "-------- Running e2e tests with namespace=${namespace}, tag=${tag}, maven_mirror=${maven_mirror} and image=${image}"

# performs the test
DEBUG=true KOGITO_IMAGE_TAG=${tag} MAVEN_MIRROR_URL=${maven_mirror} TESTS=${tests} operator-sdk test local ./test/e2e "$E2E_PARAMS" --namespace "${namespace}" --debug --verbose --go-test-flags "-timeout 120m"

# clean up
oc delete namespace "${namespace}"
