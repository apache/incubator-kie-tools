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

script_dir_path=`dirname "${BASH_SOURCE[0]}"`
source ${script_dir_path}/env.sh

old_version=$(getOperatorVersion)
new_version=$1

if [ -z "$new_version" ]; then
  echo "Please inform the new version. Use X.X.X"
  exit 1
fi

sed -i "s/$old_version/$new_version/g" cmd/kogito/version/version.go README.md version/version.go config/manager/kustomization.yaml Makefile

make vet

# replace in csv file
sed -i "s|replaces: kogito-operator.*|replaces: kogito-operator.v$(getLatestOlmReleaseVersion)|g" "$(getCsvFile)"
sed -i "s/$old_version/$new_version/g" "$(getCsvFile)"

make bundle

# rewrite test default config, all other configuration into the file will be overridden
${script_dir_path}/update_test_config.sh

echo "Version bumped from $old_version to $new_version"
