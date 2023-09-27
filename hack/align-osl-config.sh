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

#
# This script will align the configuration files needed to generate the bundle manifests
# If someone will change one for the files that can be automatically aligned from the community to the osl version, executing
# this script will copy them from the community folder to the osl one.
#
# At the moment folders that contain files to be aligned are:
#  - config/manager (controller_manager_config.yaml, manager.yaml) -> config/manager/osl  (controller_manager_config.yaml, manager.yaml)
#
set -e

script_dir_path=$(dirname "${BASH_SOURCE[0]}")
config_dir_path=${script_dir_path}/../config

# List of directories containing an osl sub folder with files that need to be copied with community
dirs=("manager")
# List of files to be aligned with community for manager folder
manager=("controller_manager_config.yaml" "manager.yaml")

# Function to compare 2 files and in case of differences align the second one with the first
function compare_and_align {
  if ! cmp -s $1 $2; then
    echo "diff detected in $2"
    cp $1 $2
  fi
}

for dir in ${dirs[@]}; do
  typeset -n d=${dir}
  for file in ${d[@]}; do
    compare_and_align ${config_dir_path}/${dir}/${file} ${config_dir_path}/${dir}/osl/${file}
  done
done




