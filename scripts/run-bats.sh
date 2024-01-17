#!/usr/bin/env bash
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#


set -e

if [[ $(command -v ./bats/bin/bats) ]]; then    #skip if bats already installed else will install the bats
    echo "---> bats already available running tests"
else
    git clone https://github.com/bats-core/bats-core.git
    ./bats-core/install.sh bats
    rm -rf bats-core
fi

echo  "----> running bats on kogito-trusty-common"
./bats/bin/bats modules/kogito-trusty-common/tests/bats

echo "----> running bats on kogito-explainability"
./bats/bin/bats modules/kogito-explainability/tests/bats

echo "----> running bats on kogito-graalvm-scripts"
./bats/bin/bats modules/kogito-graalvm-scripts/common/tests/bats

echo "----> running bats on kogito-jobs-service-common"
./bats/bin/bats modules/kogito-jobs-service-common/tests/bats

echo "----> running bats on kogito-kubernetes-client"
./bats/bin/bats modules/kogito-kubernetes-client/tests/bats/

echo "----> running bats on kogito-management-console"
./bats/bin/bats modules/kogito-management-console/tests/bats/

echo "----> running bats on kogito-task-console"
./bats/bin/bats modules/kogito-task-console/tests/bats/

echo "----> running bats on kogito-trusty-ui"
./bats/bin/bats modules/kogito-trusty-ui/tests/bats/

echo "----> running bats on kogito-maven"
./bats/bin/bats modules/kogito-maven/tests/bats

echo "----> running bats on kogito-persistence"
./bats/bin/bats modules/kogito-persistence/tests/bats

echo "----> running bats on kogito-s2i-core"
./bats/bin/bats modules/kogito-s2i-core/tests/bats

echo "----> running bats on kogito-swf-builder"
./bats/bin/bats modules/kogito-swf/common/scripts/tests/bats