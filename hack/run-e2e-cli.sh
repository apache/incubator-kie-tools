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
skip_build=$5

if [ -z "$namespace" ]; then
  echo "Please inform the namespace where the tests will run"
  exit 1
fi

if [ -z "$tag" ]; then
  echo "-------- tag is empty, assuming default from the Operator"
else
  tag=":${tag}"
fi

if [ "${native}" == "true" ]; then
  native="--native"
else
  native=""
fi

if [ "${skip_build}" == "false" ]; then
  echo "-------- Building CLI"
  make build-cli release=false
fi

# performs the test
echo "-------- Running e2e tests with namespace=${namespace}, tag=${tag}, native=${native} and maven_mirror=${maven_mirror}"

./build/_output/bin/kogito use-project ${namespace}
./build/_output/bin/kogito install operator #should exist on OCP 4, on OCP 3 will install it, so no error

echo "-------- Deploying the Kogito app"
./build/_output/bin/kogito deploy kogito-example https://github.com/kiegroup/kogito-examples --context-dir="drools-quarkus-example" --build-env MAVEN_MIRROR_URL="${maven_mirror}"  --image-s2i="${tag}" --image-runtime="${tag}" ${native}

count=0
echo "-------- Waiting for deployment to finish"
until desired=$(oc get dc/kogito-example -n ${namespace} | grep kogito-example | awk '{ print $4 }'); [ "${desired}" == "1" ]
  do
    if [ $count -eq 40 ]; then
      echo "-------- Failed to deploy the application within the time frame of ${count} minutes"
      exit 1
    fi
    count=$((count+1))
    sleep 60
    echo "-------- Time elapsed: ${count} minutes"
done

echo "-------- Deployment seems to be finished"
route=$(oc get route/kogito-example -n ${namespace} | grep http | awk '{ print $2 }')

echo "-------- Route is ${route}"
response=$(curl "http://${route}/hello")

if [ "$response" != "Mario is older than Mark" ]; then
  echo "Failed to deploy application, response: '${response}' not expected"
  exit 1
fi

echo "------- KogitoApp successful deployed, cleaning up"
./build/_output/bin/kogito delete-service kogito-example

echo "------- Waiting a couple seconds to finish the clean up"
sleep 5

echo "------- Checking if the resources were actually cleaned"
found=$(oc get dc/kogito-example -n ${namespace} 2> >(grep -m 1  -i notfound) | wc -l)
if [ "$found" != "1" ]; then
  echo "Failed to clean the application the DC stills in the cluster!"
  exit 1
fi

found=$(oc get bc/kogito-example -n ${namespace} 2> >(grep -m 1  -i notfound) | wc -l)
if [ "$found" != "1" ]; then
  echo "Failed to clean the application the BC stills in the cluster!"
  exit 1
fi

echo "------- e2e test ended successfully!"
