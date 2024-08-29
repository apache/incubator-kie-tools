#!/bin/bash
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
set -e

script_dir_path=$(dirname "${BASH_SOURCE[0]}")
source "${script_dir_path}"/env.sh

imageTag='docker.io/apache/incubator-kie-sonataflow-operator'
# shellcheck disable=SC2034
old_version=$(getImageTagVersion)
old_operator_version=$(getOperatorVersion)
new_version=$1
quarkus_version=$2

if [ -z "${new_version}" ]; then
  echo "Please inform the new version"
  exit 1
fi

if [ -z "${quarkus_version}" ]; then
  quarkus_version="3.13.0"
  echo "Warning: The Quarkus version is not supplied, defaulting to version: $quarkus_version"
fi

oldMajorMinorVersion=${old_version%.*}
newMajorMinorVersion=${new_version%.*}

echo "Set new version to ${new_version} (majorMinor = ${newMajorMinorVersion})"

sed -i "s|version: ${old_version}|version: ${new_version}|g" images/manager.yaml

sed -i "s|^VERSION ?=.*|VERSION ?= ${new_version}|g" Makefile
sed -i "s|^REDUCED_VERSION ?=.*|REDUCED_VERSION ?= ${newMajorMinorVersion}|g" Makefile
sed -i "s|newTag:.*|newTag: ${new_version}|g" config/manager/kustomization.yaml

sed -i "s|IMAGE_TAG_BASE ?=.*|IMAGE_TAG_BASE ?= ${imageTag}|g" Makefile
sed -i "s|newName:.*|newName: ${imageTag}|g" config/manager/kustomization.yaml

sed -i -r "s|operatorVersion =.*|operatorVersion = \"${new_version}\"|g" version/version.go

sed -i "s|containerImage:.*|containerImage: ${imageTag}:${newMajorMinorVersion}|g" $(getCsvFile)

# Begin: Sonataflow DB Migrator tool
sed -i "s|OPERATOR_VERSION=${old_operator_version}|OPERATOR_VERSION=${new_version}|g" images/tools/sonataflow-db-migrator/build-container-image.sh
sed -i "s|OPERATOR_VERSION=${old_operator_version}|OPERATOR_VERSION=${new_version}|g" images/tools/sonataflow-db-migrator/src/main/cekit/modules/kogito-postgres-db-migration-deps/migration.sh
sed -i "s|<version>${old_operator_version}</version>|<version>${new_version}</version>|g" images/tools/sonataflow-db-migrator/pom.xml
sed -i "s|<quarkus.platform.version>.*</quarkus.platform.version>|<quarkus.platform.version>${quarkus_version}</quarkus.platform.version>|g" images/tools/sonataflow-db-migrator/pom.xml
# End: Sonataflow DB Migrator tool

make generate-all
make vet

echo "Version bumped to ${new_version}"
