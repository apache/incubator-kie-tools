#!/bin/bash
# Copyright 2021 Red Hat, Inc. and/or its affiliates
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


set -e
source ./hack/ci/operator-ensure-manifests.sh

export OP_TEST_PRETEST_CUSTOM_SCRIPT=${PWD}/hack/ci/olm-pretest.sh

echo "\n=======> Pretest script path set to ${OP_TEST_PRETEST_CUSTOM_SCRIPT}"

cd "${tempfolder}"

bash <(curl -sL https://cutt.ly/WhkV76k) all  community-operators/kogito-operator/"${version}"