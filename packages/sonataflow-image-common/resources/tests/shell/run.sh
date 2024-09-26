#!/usr/bin/env bash
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

set -e

script_dir_path="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

image_name=$1
image_tag=$2

if [ -z "${image_name}" ]; then
  echo "Please provide the image id to test"
  exit 1
fi

if [ -z "${image_tag}" ]; then
  echo "Please provide the container image full tag (ie 'registry/namespace/image:version')"
  exit 1
fi

export TEST_IMAGE="${image_tag}"
export OUTPUT_DIR="${script_dir_path}/../../target/shell/${image_name}"
export TESTS_SCRIPT_DIR_PATH="${script_dir_path}/${image_name}"

echo "image_name=${image_name}"
echo "TEST_IMAGE=${TEST_IMAGE}"
echo "OUTPUT_DIR=${OUTPUT_DIR}"
echo "TESTS_SCRIPT_DIR_PATH=${TESTS_SCRIPT_DIR_PATH}"

if [ -d  "${script_dir_path}/${image_name}" ]; then
  curl -Ls https://sh.jbang.dev | bash -s - "${TESTS_SCRIPT_DIR_PATH}/src/RunTests.java"
else
  echo "No shell test to run for image ${image_name}"
fi