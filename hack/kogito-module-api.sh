#!/bin/bash
# Copyright 2021 Red Hat, Inc. and/or its affiliates
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

flag=$1

print_usage() {
  echo "This script handles Kogito API module manipulation with Operator SDK"
  echo "Must run from the project root"
  echo "USAGE: ./kogito-api.sh [--disable|--enable]"
  echo "--disable disables the Kogito API module"
  echo "--enable  enables the Kogito API module"
}

disable() {
  sed -i '/github.com\/kiegroup\/kogito-operator\/apis v/d' ./go.mod
  mv ./apis/go.mod apis/go.mod.bkp 2>/dev/null
  true
}

enable() {
  disable
  mv ./apis/go.mod.bkp apis/go.mod
  go mod tidy
}

case $flag in
"--disable")
  disable
  ;;
"--enable")
  enable
  ;;
*)
  echo "ERROR: Wrong flag!"
  print_usage
  ;;
esac
