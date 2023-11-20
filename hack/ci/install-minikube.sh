#!/bin/bash
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

set -e

install_path=$1
default_minikube_version=v1.31.1

if [[ -z ${MINIKUBE_VERSION} ]]; then
  MINIKUBE_VERSION=$default_minikube_version
fi

download_path="${HOME}/.minikube/download/"

echo "---> Minikube version to install is ${MINIKUBE_VERSION}"

# get the arch and os
arch=$(uname -m)
case $(uname -m) in
"x86_64") arch="amd64" ;;
"aarch64") arch="arm64" ;;
esac
os=$(uname | awk '{print tolower($0)}')

if [ -e "${download_path}/minikube-${os}-${arch}" ]; then
  echo "---> Minikube ${MINIKUBE_VERSION} (OS ${os} Architecture ${arch}) already exists in '${download_path}', skipping downloading"
else
  mkdir -p "${download_path}"
  cd "${download_path}"
  echo "---> Downloading minikube ${MINIKUBE_VERSION} (OS ${os} Architecture ${arch}) to ${download_path}"
  curl -LO "https://storage.googleapis.com/minikube/releases/${MINIKUBE_VERSION}/minikube-${os}-${arch}"
  cd -
fi

if [ -z "${install_path}" ]; then
  install_path="${HOME}/runner/bin"
  [[ "${os}" == "darwin" ]]; install_path="${HOME}/runner/bin"
fi

echo "---> Ensuring minikube installation at ${install_path}"

mkdir -p "${install_path}"
chmod +x "${install_path}"
cp "${download_path}/minikube-${os}-${arch}" "${install_path}/minikube"
