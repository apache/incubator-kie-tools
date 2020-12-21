#!/bin/sh
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


# build and deploy the operator into Openshift using our quay account
# 0. Deploy CRDs
# 1. Deploy Roles and Service Account
# 2. Deploy the operator on Openshift

SCRIPT_DIR=`dirname $0`
IMAGE=$1
TMP_FOLDER=tmp

mkdir -p $TMP_FOLDER
rm -rf $TMP_FOLDER/*

# Apply all
sed "s|image: quay.io/kiegroup/.*|image: $IMAGE|g" kogito-operator.yaml > $TMP_FOLDER/kogito-operator.yaml
kubectl apply -f $TMP_FOLDER/kogito-operator.yaml

# Clean Up
rm -rf $TMP_FOLDER/*