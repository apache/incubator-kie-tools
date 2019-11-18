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

BASEDIR=$(pwd)
KOGITO_CMD_DIR="cmd/kogito"

function packTemplateFiles(){
  cd "${BASEDIR}"/"${KOGITO_CMD_DIR}" && packr2 -v
  cd "${BASEDIR}"
}

function cleanTemplateFiles(){
  cd "${BASEDIR}"/"${KOGITO_CMD_DIR}" && packr2 clean -v
  cd "${BASEDIR}"
}

release=$1
version=$2

if ! hash packr2 2>/dev/null; then
  go get -u github.com/gobuffalo/packr/v2/packr2
fi

if [ "$release" = "true" ]; then
  if [ -z "$version" ]; then
    echo "Please inform the version name"
    exit 1
  fi
  rm -rf build/_output/release
  mkdir -p build/_output/release
  echo "--- Building Kogito CLI ${version}"

  declare -a os=("linux" "darwin" "windows")
  arch="amd64"

  for i in "${os[@]}"
  do
    rm -rf build/_output/bin/kogito

    packTemplateFiles
    CGO_ENABLED=0 GOOS="${i}" GOARCH="${arch}" go build -v -a -o build/_output/bin/kogito github.com/kiegroup/kogito-cloud-operator/cmd/kogito
    if [ $? -ne 0 ]; then
      echo "Failed to build for OS ${i} and Architecture ${arch}"
      cleanTemplateFiles
      exit 1
    fi
    cleanTemplateFiles

    if [ "${i}" = "windows" ]; then
      zip -j "build/_output/release/kogito-${version}-${i}-${arch}.zip" build/_output/bin/kogito
      if [ $? -ne 0 ]; then
        exit 1
      fi
    else
      tar -czvf "build/_output/release/kogito-${version}-${i}-${arch}.tar.gz" -C build/_output/bin kogito
      if [ $? -ne 0 ]; then
        exit 1
      fi
    fi
  done
  echo "--- Finishing building Kogito CLI ${version}"
else
  packTemplateFiles
  CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -v -a -o build/_output/bin/kogito github.com/kiegroup/kogito-cloud-operator/cmd/kogito
  cleanTemplateFiles
fi
