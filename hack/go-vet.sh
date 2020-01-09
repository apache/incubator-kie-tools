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


if [[ -z ${CI} ]]; then
    ./hack/go-mod.sh
    operator-sdk generate k8s
    operator-sdk generate openapi
    # removing from to not jeopardize with jobs service sped --from-version 0.6.0
    operator-sdk olm-catalog gen-csv --csv-version 0.7.0-rc1 --update-crds --operator-name kogito-operator
fi
go vet ./...