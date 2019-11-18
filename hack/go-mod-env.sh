#!/bin/sh
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


# If inside GOPATH/src, set GO111MODULE=on
setGoModEnv() {
  pwdPath=$(pwd -P 2>/dev/null || env PWD= pwd)
  goPath=$(go env GOPATH)
  cd "$goPath" || exit
  goPath=$(pwd -P 2>/dev/null || env PWD= pwd)
  cd "$pwdPath" || exit
  if [ "${pwdPath#"$goPath"}" != "${pwdPath}" ]; then
    export GO111MODULE=on
  fi
}
