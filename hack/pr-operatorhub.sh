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


# prepare the package for the operatorhub.io to push our changes there
# 0. make sure that the operator is ok (e2e)
# 1. run this script
# 2. push the results of build/_output/operatorhub/ to https://github.com/operator-framework/community-operators/tree/master/community-operators/kogito-cloud-operator

version=$1
output="build/_output/operatorhub/"

if [ -z "$version" ]; then
    echo "Please inform the release version. Use X.X.X"
    exit 1
fi

if ! hash operator-courier 2>/dev/null; then
  pip3 install operator-courier
fi

# clean up
rm -rf "${output}*"
mkdir -p ${output}

# will run unit tests and generate the ultimate source for the OLM catalog
make test

# copy the generated files
cp "deploy/olm-catalog/kogito-cloud-operator/${version}/"*.yaml $output
cp deploy/olm-catalog/kogito-cloud-operator/kogito-cloud-operator.package.yaml $output

# basic verification
operator-courier verify --ui_validate_io $output

# now it's your turn to push the application and the image to quay:
# https://github.com/operator-framework/community-operators/blob/master/docs/testing-operators.md#push-to-quayio

# then push it to operatorhub
# https://github.com/operator-framework/community-operators/pulls
