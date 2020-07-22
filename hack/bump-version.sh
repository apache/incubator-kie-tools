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

source ./hack/export-version.sh

old_version=${OP_VERSION}
new_version=$1
release=$2
no_release_branch=$3

if [ -z "$new_version" ]; then
    echo "Please inform the new version. Use X.X.X"
    exit 1
fi

sed -i "s/$old_version/$new_version/g" cmd/kogito/version/version.go README.md version/version.go deploy/operator.yaml deploy/olm-catalog/kogito-operator/kogito-operator.package.yaml deploy/olm-catalog/kogito-operator/custom-subscription-example.yaml hack/go-build.sh hack/go-vet.sh .osdk-scorecard.yaml
operator-sdk generate csv --apis-dir ./pkg/apis/apps/v1alpha1 --verbose --csv-version "$new_version" --from-version "$old_version" --update-crds --operator-name kogito-operator
sed -i "s|operated-by: kogito-operator.*|operated-by: kogito-operator.${new_version}|g" deploy/olm-catalog/kogito-operator/manifests/kogito-operator.clusterserviceversion.yaml

# rewrite test default config, all other configuration into the file will be overridden
test_config_file="test/.default_config"
current_branch=`git branch --show-current`

if [ "${release}" = "true" ]; then
    image_version=`echo "${new_version}" | awk -F. '{print \$1"."\$2}'`
    if [ "${no_release_branch}" = "true" ]; then
        branch="master"
    else
        branch="${image_version}.x"
    fi
else
    image_version="latest"
    branch="master"
fi

sed -i "s|tests.build-image-version=.*|tests.build-image-version=${image_version}|g" ${test_config_file}
sed -i "s|tests.services-image-version=.*|tests.services-image-version=${image_version}|g" ${test_config_file}
sed -i "s|tests.runtime-application-image-version=.*|tests.runtime-application-image-version=${image_version}|g" ${test_config_file}
sed -i "s|tests.examples-ref=.*|tests.examples-ref=${branch}|g" ${test_config_file}

echo "Version bumped from $old_version to $new_version"