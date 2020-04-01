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

# Script with utils for CRDs resources.

# Apply CRDs by replacing existing resources or applying if they didn't exist previously.
#   Example: apply_crds my_path
function apply_crds(){
  deploy_folder=${1}
  files=($deploy_folder/crds/*_crd.yaml)
  for file in ${files[@]}
  do
    echo "Replace crds file ${file}"
    oc replace -f ${file}
    if [ "$?" != 0 ]; then
      echo "crd from file '${file}' may not exist yet in cluster, try to simply apply it"
      oc apply -f ${file}
    fi
  done
}

# Download CRDs from a URL into a folder.
#   Example: download_remote_crds my_path https://path/to/crds.
function download_remote_crds(){
  deploy_folder=${1}
  url=${2}
  echo "Download crd files into '${deploy_folder}' from ${url}"

  mkdir -p "${deploy_folder}/crds"
  files=(deploy/crds/*_crd.yaml)
  for file in ${files[@]}
  do
    filename=${file##*/}
    curl -k -o "${deploy_folder}/crds/${filename}" "${url}/crds/${filename}"
  done
}