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

getKubeSystemPodStatusConditions() { 
  kubectl get pods $1 -n kube-system -o json | jq '.items[].status.conditions[]'
}

getKubeSystemPodReadyStatus() { 
  echo $(kubectl get pods $1 -n kube-system -o json | jq -r '.items[].status.conditions[] | select(.type == "Ready") | .status') 
}

waitKubeSystemForPodReady() {
  local selector=${1}
  local timeout_time=${2:-60s}

  export -f getKubeSystemPodStatusConditions
  export -f getKubeSystemPodReadyStatus

  echo "Wait for Kube System pod with selector '${selector}' and timeout ${timeout_time}"

  timeout ${timeout_time} bash -c "getKubeSystemPodStatusConditions '${selector}' && while [[ \"$(getKubeSystemPodReadyStatus "${selector}")\" != "True" ]] ; do sleep 2 && getKubeSystemPodStatusConditions '${selector}'; done"
}