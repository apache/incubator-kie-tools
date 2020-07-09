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

default_operator_sdk_version=v0.18.2

if [[ -z ${OPERATOR_SDK_VERSION} ]]; then
    OPERATOR_SDK_VERSION=$default_operator_sdk_version
fi

GOPATH=$(go env GOPATH)

if [[ $(which operator-sdk) ]]; then
  echo "---> operator-sdk is already installed. Please make sure it is the required ${OPERATOR_SDK_VERSION} version before proceeding"
else
  echo "---> operator-sdk not found, installing it in \$GOPATH/bin/"
  curl -L https://github.com/operator-framework/operator-sdk/releases/download/$OPERATOR_SDK_VERSION/operator-sdk-$OPERATOR_SDK_VERSION-x86_64-linux-gnu -o "$GOPATH"/bin/operator-sdk
  chmod +x "$GOPATH"/bin/operator-sdk
fi

##For verification
operator-sdk version