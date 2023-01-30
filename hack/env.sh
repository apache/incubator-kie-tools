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

CSV_DIR="config/manifests/bases"

getOperatorVersion() {
  local version=$(grep -m 1 'VERSION ?=' Makefile | awk -F= '{print $2}' | tr -d ' ')
  echo "${version}"
}

getOperatorImageName() {
  local image_name=$(grep -m 1 'IMAGE_TAG_BASE ?=' Makefile | awk -F= '{print $2}' | tr -d ' ')
  echo "${image_name}"
}

getCsvFile() {
  echo "${CSV_DIR}/kogito-serverless-operator.clusterserviceversion.yaml"
}
