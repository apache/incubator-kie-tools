#!/bin/env bash
# Copyright 2019 Red Hat, Inc. and/or its affiliates
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

coverage=$1

. ./hack/go-mod-env.sh

if [[ -z ${CI} ]]; then
    ./hack/go-vet.sh
    ./hack/go-fmt.sh
    ./hack/go-lint.sh
    ./hack/addheaders.sh
fi
setGoModEnv
# using p flag to not run cmd tests in parallel, causing problems during config file read
if [[ "${coverage}" == "true" ]]; then
  go test -coverprofile cp_cmd.out ./cmd/... -count=1 -p=1
  go test -coverprofile cp_pkg.out ./pkg/... -count=1
  go tool cover -html=cp_cmd.out
  go tool cover -html=cp_pkg.out
else
  go test ./cmd/... -count=1 -p=1
  go test ./pkg/... -count=1
fi
