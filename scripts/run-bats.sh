#!/usr/bin/env bash

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