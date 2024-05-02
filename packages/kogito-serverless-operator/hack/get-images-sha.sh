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

#
# This script fetches the sha256 of images built on Brew and tagged into a specific Brew tag.
# As an input it requires the OpenShift Serverless Logic produt version.
#
# Example to run:
# $ ./get-images-sha.sh 1.29
#
# Note: brewkoji package is required.
#

imagesBrewPackageName=("openshift-serverless-1-logic-rhel8-operator-container")

if ! command -v brew > /dev/null;
then
    echo "brew command not available in the system, please install brewkoji package"
    exit 1
fi

if [ $# -eq 0 ];
then
    echo "$0: Missing the OpenShift Serverless Logic version input"
    exit 1
fi

oslVersion=$1
brewTag="openshift-serverless-${oslVersion}-rhel-8-container-candidate"
for brewPackageName in ${imagesBrewPackageName[@]}; do
    echo "Finding latest Brew build for package ${brewPackageName}"
    brewBuild=$(brew latest-build ${brewTag} ${brewPackageName} | tail -n1 | cut -d ' ' -f1)
    echo "Found Brew build: ${brewBuild}"
    imageSha=$(brew buildinfo "${brewBuild}" | awk -F'Extra: ' '{print $2}' | tr \' \" | sed 's|False|\"false\"|g' | sed 's|True|\"true\"|g' | sed 's|None|\"\"|g' | jq -r '.image.index.pull[0]'| cut -d "@" -f2)
    echo "Image sha: ${imageSha}"
    echo "---"
done
