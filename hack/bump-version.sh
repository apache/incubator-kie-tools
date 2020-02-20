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


old_version=$1
new_version=$2

if [ -z "$old_version" ]; then
    echo "Please inform the old version. Use X.X.X"
    exit 1
fi


if [ -z "$new_version" ]; then
    echo "Please inform the new version. Use X.X.X"
    exit 1
fi

sed -i "s/$old_version/$new_version/g" cmd/kogito/version/version.go README.md version/version.go deploy/operator.yaml deploy/olm-catalog/kogito-operator/kogito-operator.package.yaml hack/go-build.sh hack/go-vet.sh .osdk-scorecard.yaml
operator-sdk generate csv --csv-version "$new_version" --from-version "$old_version"  --update-crds --operator-name kogito-operator

echo "Version bumped from $old_version to $new_version"