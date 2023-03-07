#!/bin/bash
# Copyright 2023 Red Hat, Inc. and/or its affiliates
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

# runs the e2e locally
# You must have minikube installed

export OPERATOR_IMAGE_NAME=localhost/kogito-serverless-operator:0.0.1
eval "$(minikube -p minikube docker-env)"
make container-build BUILDER=docker IMG="${OPERATOR_IMAGE_NAME}"

make test-e2e
