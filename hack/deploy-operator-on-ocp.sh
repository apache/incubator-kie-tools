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
OPERATOR_NAME=kogito-operator
TMP_FOLDER=tmp

mkdir -p $TMP_FOLDER
rm -rf $TMP_FOLDER/*

echo "Deploying Operator $OPERATOR_NAME"

# CRDs
source $SCRIPT_DIR/crds-utils.sh
apply_crds "deploy"

# Service Account
oc create -f deploy/service_account.yaml

# Roles
oc create -f deploy/role.yaml
oc create -f deploy/role_binding.yaml

# Operator
sed "s/name: kogito-operator/name: $OPERATOR_NAME/g" deploy/operator.yaml > $TMP_FOLDER/operator.yaml
sed "s|image: quay.io/kiegroup/.*|image: $IMAGE|g" $TMP_FOLDER/operator.yaml > $TMP_FOLDER/operator-tmp.yaml
oc create -f $TMP_FOLDER/operator-tmp.yaml

# Clean Up
rm -rf $TMP_FOLDER/*