#!/bin/bash
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

# Use older addlicense library until we migrate to Go 1.16? Newest version is incompatible with Go 1.14.
command -v  addlicense > /dev/null || go get -modfile=go.tools.mod -u github.com/google/addlicense@99ebc9c9db7bceb8623073e894533b978d7b7c8a

addlicense -c "Red Hat, Inc. and/or its affiliates" -l=apache cmd hack api controllers core internal meta version client