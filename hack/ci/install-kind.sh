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


default_kind_version=v0.8.1

if [[ -z ${KIND_VERSION} ]]; then
    KIND_VERSION=$default_kind_version
fi

GOPATH=$(go env GOPATH)

if [[ $(which kind) ]]; then
  echo "---> kind is already installed. Please make sure it is the required ${KIND_VERSION} version before proceeding"
else
  echo "---> kind not found, installing it in \$GOPATH/bin/"
  curl -L https://kind.sigs.k8s.io/dl/$KIND_VERSION/kind-$(uname)-amd64 -o "$GOPATH"/bin/kind
  chmod +x "$GOPATH"/bin/kind
fi

#for verification
kind version