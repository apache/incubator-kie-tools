#!/bin/bash
# Copyright 2022 Red Hat, Inc. and/or its affiliates
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

curl --output-dir dist-dev -LO https://github.com/openshift/okd/releases/download/$1/openshift-client-linux-$1.tar.gz
tar -C dist-dev -zxvf dist-dev/openshift-client-linux-$1.tar.gz oc
rm dist-dev/openshift-client-linux-$1.tar.gz
