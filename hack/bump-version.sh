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

source ./hack/export-version.sh

CSV_DIR="config/manifests/bases/"

old_version=${OP_VERSION}
new_version=$1
release=$2
no_release_branch=$3

if [ -z "$new_version" ]; then
  echo "Please inform the new version. Use X.X.X"
  exit 1
fi

if [ -z "$release" ]; then
  release="false"
fi

if [ -z "$no_release_branch" ]; then
  no_release_branch="true"
fi


echo "Latest released OLM version = $LATEST_RELEASED_OLM_VERSION"

sed -i "s/$old_version/$new_version/g" cmd/kogito/version/version.go README.md pkg/version/version.go config/manager/kustomization.yaml Makefile

make vet


# replace in csv file
csv_file="${CSV_DIR}/kogito-operator.clusterserviceversion.yaml"
sed -i "s|replaces: kogito-operator.*|replaces: kogito-operator.v${LATEST_RELEASED_OLM_VERSION}|g" ${csv_file}
sed -i "s/$old_version/$new_version/g" ${csv_file}

make bundle

# rewrite test default config, all other configuration into the file will be overridden
test_config_file="test/.default_config"

image_version=$(echo "${new_version}" | awk -F. '{print $1"."$2}')
branch="${image_version}.x"
if [ "${no_release_branch}" = "true" ]; then
  branch="master"
  if [ "${release}" != "true" ]; then
    image_version="latest"
  fi
fi
echo "Set test config with image version ${image_version} and branch ${branch}"
sed -i "s|tests.build-image-version=.*|tests.build-image-version=${image_version}|g" ${test_config_file}
sed -i "s|tests.services-image-version=.*|tests.services-image-version=${image_version}|g" ${test_config_file}
sed -i "s|tests.runtime-application-image-version=.*|tests.runtime-application-image-version=${image_version}|g" ${test_config_file}
sed -i "s|tests.examples-ref=.*|tests.examples-ref=${branch}|g" ${test_config_file}

echo "Version bumped from $old_version to $new_version"
