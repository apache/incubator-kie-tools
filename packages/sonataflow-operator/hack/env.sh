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

CSV_DIR="config/manifests/bases"
BUNDLE_DIR="bundle/manifests"

getOperatorVersion() {
  local version=$(grep -m 1 'operatorVersion = ' api/version/version.go | awk -F\" '{print $2}')
  echo "${version}"
}

getOperatorImageName() {
  local image_name=$(grep -m 1 'IMAGE_TAG_BASE ?=' Makefile | awk -F= '{print $2}' | tr -d ' ')
  echo "${image_name}"
}

getCsvFile() {
  echo "${CSV_DIR}/sonataflow-operator.clusterserviceversion.yaml"
}

getBundleCsvFile() {
  echo "${BUNDLE_DIR}/sonataflow-operator.clusterserviceversion.yaml"
}

DEPENDENT_CRDS_KEYS=(grafana hyperfoil infinispan kafka keycloak mongodb) # This list may need a revision
DEPENDENT_SENSITIVE_CRDS_KEYS=(prometheus)

getAllDependentCrds() {
  for crdKey in ${DEPENDENT_CRDS_KEYS[*]}
  do
    for crd in $(getDependentCrds ${crdKey})
    do
      echo "$crd"
    done
  done

  if [ "$1" = "all" ]
  then
    for crdKey in ${DEPENDENT_SENSITIVE_CRDS_KEYS[*]}
    do
      for crd in $(getDependentCrds ${crdKey})
      do
        echo "$crd"
      done
    done
  fi
}

getDependentCrds() {
  oc get crds | grep $1 | awk -F' ' '{print $1}'
}

# get_and_clean_cluster_resources namespace resourceName
get_and_clean_resources() {
  clean_resources $1 $2 "$(oc get $2 -n $1 | grep -v NAME | awk '{print $1}')"
}

# clean_cluster_resources namespace resourceName {list of resources}
clean_resources() {
  for resourceName in $3
  do
    echo "Delete $2 ${resourceName} in namespace $1"
    oc delete $2 ${resourceName} -n $1
  done
}

# get_and_clean_cluster_resources resourceName
get_and_clean_cluster_resources() {
  clean_cluster_resources $1 "$(oc get $1 | grep -v NAME | awk '{print $1}')"
}

# clean_cluster_resources resourceName {list of resources}
clean_cluster_resources() {
  for resourceName in $2
  do
    echo "Delete cluster $1 ${resourceName}"
    oc delete $1 ${resourceName} --timeout=30s
  done
}