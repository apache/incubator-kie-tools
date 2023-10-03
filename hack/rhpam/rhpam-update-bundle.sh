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


set -e

script_dir_path=`dirname "${BASH_SOURCE[0]}"`
source ${script_dir_path}/rhpam-env.sh

# update opreator name.version
sed -i "s/rhpam-kogito-operator.v$(getOperatorVersion)/rhpam-kogito-operator.$(getOperatorCsvVersion)/" $(getBundleCsvFile)

# update selector operated-by
sed -i "s/operated-by: rhpam-kogito-operator.0.0.0/operated-by: rhpam-kogito-operator.$(getOperatorCsvVersion)/" $(getBundleCsvFile)

# update the replaces field
sed -i "s/replaces: rhpam-kogito-operator.0.0.0/replaces: rhpam-kogito-operator.v$(getOperatorPriorCsvVersion)/" $(getBundleCsvFile)

# update csv version
sed -i "s/version: $(getOperatorVersion)/version: $(getOperatorCsvVersion)/" $(getBundleCsvFile)

# update operator image
sed -i  "s|registry.stage.redhat.io/rhpam-7/rhpam-kogito-rhel8-operator.*|${1}|g" $(getBundleCsvFile)

# replaces the IMAGE_REGISTRY registry value from stage to production
sed -i '/name: IMAGE_REGISTRY/{n;s/registry.stage.redhat.io/registry.redhat.io/}' $(getBundleCsvFile)
sed -i '/name: IMAGE_REGISTRY/{n;s/registry.stage.redhat.io/registry.redhat.io/}' rhpam-operator.yaml


# The script will update the containerImage inside CSV and remove the replaces field
if [[ -z ${1} ]]; then
    echo "No image given. Please provide the image to use in CSV"
    exit 1
fi

echo "Will update the bundle CSV with ${1} image"
echo "Bundle CSV updated"
