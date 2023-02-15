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
set -e

default_operator_sdk_version=v1.25.0

if [[ -z ${OPERATOR_SDK_VERSION} ]]; then
    OPERATOR_SDK_VERSION=$default_operator_sdk_version
fi

GOPATH=$(go env GOPATH)

should_install=false
if [[ $(command -v operator-sdk) ]]; then
  echo "---> operator-sdk is already installed. Checking the version."
  operator_sdk_version=$(operator-sdk version | awk -F',' '{print $1}' | awk -F\" '{print $2}')
  echo "---> operator-sdk installed version = ${operator_sdk_version}. Expected = ${OPERATOR_SDK_VERSION}"
  if [ "${operator_sdk_version}" != "${OPERATOR_SDK_VERSION}" ]; then
    echo "---> operator-sdk is not of the expected version. It will be re-installed."
    should_install=true
  fi
else
  echo "---> operator-sdk not found. It will be installed."
  should_install=true
fi

if [ "${should_install}" = "true" ]; then
  # get the arch and os
  arch=$(uname -m)
  case $(uname -m) in
    "x86_64") arch="amd64"; ;;
    "aarch64") arch="arm64"; ;;
  esac
  os=$(uname | awk '{print tolower($0)}')
  echo "---> Installing operator-sdk (OS ${os} Architecture ${arch} in \$GOPATH/bin/"
  mkdir -p "$GOPATH"/bin
  curl -L https://github.com/operator-framework/operator-sdk/releases/download/"${OPERATOR_SDK_VERSION}"/operator-sdk_"${os}"_"${arch}" -o "$GOPATH"/bin/operator-sdk
  chmod +x "$GOPATH"/bin/operator-sdk
fi

##For verification
operator_sdk_version=$(operator-sdk version | awk -F',' '{print $1}' | awk -F\" '{print $2}')
echo "---> Using operator-sdk version ${operator_sdk_version}"
if [ "${operator_sdk_version}" != "${OPERATOR_SDK_VERSION}" ]; then
  echo "ERROR: After installation, operator-sdk is with version ${operator_sdk_version} but should be ${OPERATOR_SDK_VERSION}. Please check your PATH so that \$GOPATH/bin/ is prior."
  exit 1
fi