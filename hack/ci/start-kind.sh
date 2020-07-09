#!/bin/bash
# Copyright 2020 Red Hat, Inc. and/or its affiliates
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

#Make sure kubectl is installed before proceeding
which kubectl > /dev/null || (echo "kubectl is not installed. Please install it before proceeding exiting...." && exit 1)

#Make sure docker is installed before proceeding
which docker > /dev/null || (echo "docker is not installed. Please install it before proceeding exiting...." && exit 1)

#make sure kind is installed before proceeding
which kind > /dev/null || (echo "kind is not installed. Please install it before proceeding exiting...." && exit 1)

default_cluster_name="operator-test"

if [[ -z ${CLUSTER_NAME} ]]; then
    CLUSTER_NAME=$default_cluster_name
fi
if [[ $(kind get clusters | grep ${CLUSTER_NAME}) ]]; then
  echo "---> Cluster ${CLUSTER_NAME} already present"
else
  echo "---> Provisioning new cluster"
  kind create cluster  --name ${CLUSTER_NAME} --wait 1m
fi

echo "---> Checking KIND cluster conditions"
kubectl get nodes -o wide
kubectl get pods --all-namespaces -o wide