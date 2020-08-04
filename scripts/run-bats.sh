#!/bin/bash

set -e

if [[ $(command -v ./bats/bin/bats) ]]; then    #skip if bats already installed else will install the bats
    echo "---> bats already available running tests"
else
    git clone https://github.com/bats-core/bats-core.git
    ./bats-core/install.sh bats
    rm -rf bats-core
fi 

echo "----> running bats on kogito-data-index"
./bats/bin/bats modules/kogito-data-index/tests/bats

echo  "----> running bats on kogito-trusty"
./bats/bin/bats modules/kogito-trusty/tests/bats

echo "----> running bats on kogito-explainability"
./bats/bin/bats modules/kogito-explainability/tests/bats

echo "----> running bats on kogito-graalvm-scripts"
./bats/bin/bats modules/kogito-graalvm-scripts/tests/bats

echo "----> running bats on kogito-jobs-service"
./bats/bin/bats modules/kogito-jobs-service/tests/bats

echo "----> running bats on kogito-kubernetes-client"
./bats/bin/bats modules/kogito-kubernetes-client/tests/bats/

echo "----> running bats on kogito-management-console"
./bats/bin/bats modules/kogito-management-console/tests/bats/

echo "----> running bats on kogito-maven"
./bats/bin/bats modules/kogito-maven/tests/bats

echo "----> running bats on kogito-persistence"
./bats/bin/bats modules/kogito-persistence/tests/bats

echo "----> running bats on kogito-s2i-core"
./bats/bin/bats modules/kogito-s2i-core/tests/bats