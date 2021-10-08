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

script_dir_path=`dirname "${BASH_SOURCE[0]}"`
source ${script_dir_path}/rhpam-env.sh

SCRIPT_NAME=`basename $0`

function usage(){
  printf "Update the version of the operator."
  printf "\n"
  printf "\n${SCRIPT_NAME} [options]* version"
  printf "\n"
  printf "\nOptions:"
  printf "\n"
  printf "\n-h | --help\n\tPrint the usage of this script."
  printf "\n-r | --replaces\n\tOperator Version to replace in csv."
  printf "\n-b | --bundle-suffix\n\tBundle suffix to apply. Default is `1`."
  printf "\n"
  printf "\nExample:"
  printf "\n"
  printf "\n# Update the version to 7.12.0"
  printf "\n\n\t${SCRIPT_NAME} 7.12.0"
  printf "\n"
  printf "\n# Update the version to 7.12.0 with bundle suffix `2` and version 7.11.1 replaced"
  printf "\n\n\t${SCRIPT_NAME} 7.12.0 -r 7.11.1 -b 2"
  printf "\n"
  printf "\n"
}

function update_csv_file(){
  local csvFile=$1
  local versionToReplace=$2
  local bundleVersion="${new_version}-${bundle_version_suffix}"
  echo "Update ${csvFile} replacing ${versionToReplace} with bundle version ${bundleVersion} and replaces_version ${replaces_version}"
  sed -i "s/rhpam-kogito-operator\.v${versionToReplace}.*/rhpam-kogito-operator.v${bundleVersion}/g" "${csvFile}"
  sed -i "s/rhpam-kogito-operator\.${versionToReplace}.*/rhpam-kogito-operator.${bundleVersion}/g" "${csvFile}"
  sed -i "s/version: ${versionToReplace}.*/version: ${bundleVersion}/g" "${csvFile}"
  if [ ! -z $replaces_version ]; then
    sed -i "s|replaces: rhpam-kogito-operator.*|replaces: rhpam-kogito-operator.v${replaces_version}|g" "${csvFile}"
  fi
}

new_version=
replaces_version=
bundle_version_suffix='1'

while (( $# ))
do
case $1 in
  -r|--replaces)
    shift
    replaces_version=$1
  ;;
  -b|--bundle-suffix)
    shift
    bundle_version_suffix=$1
  ;;
  -h|--help)
    usage
    exit 0
  ;;
  *)
    new_version=$1
  ;;
esac
shift
done

if [ -z "${new_version}" ]; then
  echo "Please inform the new version. Use X.X.X"
  exit 1
fi

echo "Set version ${new_version} with bundle suffix ${bundle_version_suffix} and replacing version (if not empty) ${replaces_version}"

old_version=$(getOperatorVersion)

sed -i "s/${old_version}/${new_version}/g" README.md version/rhpam/version.go config/manager/rhpam/kustomization.yaml Makefile.rhpam rhpam-kogito-image*.yaml modules/**/module.yaml

make vet -f Makefile.rhpam

update_csv_file $(getCsvFile) ${old_version}

make bundle -f Makefile.rhpam

update_csv_file $(getBundleCsvFile) ${new_version}

echo "Version bumped from $old_version to $new_version"
