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


OP_VERSION=$(grep -m 1 'Version =' ./pkg/version/version.go) && OP_VERSION=$(echo ${OP_VERSION#*=} | tr -d '"')

echo "Operator version is ${OP_VERSION}"

### Fetching the latest released kogito-cloud-operator on the OperatorHub
tempfolder=$(mktemp -d)
git clone https://github.com/operator-framework/community-operators/ "${tempfolder}"
LATEST_RELEASED_OLM_VERSION=$(cd ${tempfolder}/community-operators/kogito-operator && for i in $(ls -d */); do echo ${i%%/}; done | sort -V | tail -1)

echo "Latest released OLM version is ${LATEST_RELEASED_OLM_VERSION}"
rm -rf ${tempfolder}