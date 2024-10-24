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

# Script variables with default values. These values will be updated by hack/bump-version.sh, don't change it.
DDL_VERSION=10.0.999-SNAPSHOT
DDL_FILE=kogito-ddl-10.0.999-20241018.012042-73-db-scripts.zip
DDL_BASE_URL=https://repository.apache.org/content/groups/snapshots/org/kie/kogito/kogito-ddl
TMP_DIR=./tmp
RESOURCES_DIR=src/main/resources
POSTGRESQL_DDL_DIR=postgresql

# cleanup temporary files
cleanup () {
    echo "Cleaning up"
    rm -rf ${RESOURCES_DIR}"/"${POSTGRESQL_DDL_DIR}
    rm -rf $TMP_DIR
}

# Use specific variable values, if passed
for arg in "$@"; do
   case "$arg" in
      DDL_VERSION=*) DDL_VERSION="${arg#*=}" ;;
      DDL_FILE=*) DDL_FILE="${arg#*=}" ;;
      DDL_BASE_URL=*) DDL_BASE_URL="${arg#*=}" ;;
   esac
done

DDL_URL=$DDL_BASE_URL/$DDL_VERSION/$DDL_FILE

printf "Variables being used for the DDL: \n DDL_VERSION: %s, \n DDL_FILE: %s, \n DDL_BASE_URL: %s \n\n" "$DDL_VERSION" "$DDL_FILE" "$DDL_BASE_URL"

# Start with cleanup
cleanup

# Get Data Index/ Jobs Service DDL Files
mkdir -p $TMP_DIR
# Change the variables below, as needed
wget "$DDL_URL"
mv "$DDL_FILE" $TMP_DIR
cd $TMP_DIR || exit
unzip "$DDL_FILE"
mv "./"${POSTGRESQL_DDL_DIR} "../"${RESOURCES_DIR}
cd .. || exit
rm -rf $TMP_DIR
