#!/bin/sh
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

set -x
set -e

# cleanup temporary files
cleanup () {
    echo "Cleaning up"
    rm -rf target
    rm -rf src/main/resources/postgresql
    rm -rf tmp
    rm -f src/main/cekit/modules/kogito-postgres-db-migration-deps/sonataflow-db-migrator-runner.jar
}

# Script variables with default values. These values will be updated by hack/bump-version.sh, don't change it.
OPERATOR_VERSION=999.0.0 # comes from version.go
DDL_VERSION=10.0.999-SNAPSHOT
DDL_FILE=kogito-ddl-10.0.999-20240806.011718-23-db-scripts.zip
DDL_BASE_URL=https://repository.apache.org/content/groups/snapshots/org/kie/kogito/kogito-ddl
CEKIT_BUILDER=podman

# Use specific variable values, if passed
for arg in "$@"; do
   case "$arg" in
      OPERATOR_VERSION=*) OPERATOR_VERSION="${arg#*=}" ;;
      DDL_VERSION=*) DDL_VERSION="${arg#*=}" ;;
      DDL_FILE=*) DDL_FILE="${arg#*=}" ;;
      DDL_BASE_URL=*) DDL_BASE_URL="${arg#*=}" ;;
      CEKIT_BUILDER=*) CEKIT_BUILDER="${arg#*=}" ;;
   esac
done

DDL_URL=$DDL_BASE_URL/$DDL_VERSION/$DDL_FILE

printf "Variables being used for the image: \n OPERATOR_VERSION: %s, \n DDL_VERSION: %s, \n DDL_FILE: %s, \n DDL_BASE_URL: %s, \n CEKIT_BUILDER=%s\n\n" "$OPERATOR_VERSION" "$DDL_VERSION" "$DDL_FILE" "$DDL_BASE_URL" "$CEKIT_BUILDER"

# Start with cleanup
cleanup "$OPERATOR_VERSION"

# Get Data Index/ Jobs Service DDL Files
mkdir -p tmp
# Change the variables below, as needed
wget "$DDL_URL"
mv "$DDL_FILE" tmp
cd tmp || exit
unzip "$DDL_FILE"
mv ./postgresql ../src/main/resources
cd .. || exit

# Create an Uber jar
mvn package -Dquarkus.package.jar.type=uber-jar
cp target/sonataflow-db-migrator-"$OPERATOR_VERSION"-runner.jar src/main/cekit/modules/kogito-postgres-db-migration-deps/sonataflow-db-migrator-runner.jar

# Build the container image
cd src/main/cekit || exit
cekit -v build "$CEKIT_BUILDER"

# Cleanup
cd ../../.. || exit
cleanup "$OPERATOR_VERSION"