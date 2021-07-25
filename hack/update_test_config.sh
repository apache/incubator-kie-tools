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

source ./hack/env.sh

current_major_minor=$(echo "$(getOperatorVersion)" | awk -F. '{print $1"."$2}')

test_image_version=${current_major_minor}
test_branch="${test_image_version}.x"

# Check the branch
git branch | grep \* | cut -d ' ' -f2 | grep ${current_major_minor}
if [ $? -ne 0 ]; then
  echo "no release branch"
  test_branch="main"
fi

# Check the version
getOperatorVersion | grep -v snapshot
if [ $? -ne 0 ]; then
  echo "no release"

  if [ "${test_branch}" = "main" ]; then
    test_image_version="latest"
  fi  
  test_branch="nightly-${test_branch}"
fi

echo "Set test config with image version ${test_image_version} and branch ${test_branch}"
sed -i "s|tests.build-image-version=.*|tests.build-image-version=${test_image_version}|g" ${TEST_CONFIG_FILE}
sed -i "s|tests.services-image-version=.*|tests.services-image-version=${test_image_version}|g" ${TEST_CONFIG_FILE}
sed -i "s|tests.runtime-application-image-version=.*|tests.runtime-application-image-version=${test_image_version}|g" ${TEST_CONFIG_FILE}
sed -i "s|tests.examples-ref=.*|tests.examples-ref=${test_branch}|g" ${TEST_CONFIG_FILE}