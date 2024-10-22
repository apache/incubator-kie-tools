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

# Directory where the DB Migrator Jar will be dropped
OUTPUT_DIR=/tmp/kogito-db-migrator-tool
TARGET_JAR_FILE=./target/sonataflow-db-migrator-0.0.0-runner.jar
DDL_DIR=./src/main/resources/postgresql

rm -rf $OUTPUT_DIR
mkdir -p $OUTPUT_DIR

./get-kogito-ddl-scripts.sh

if [ "$1" = "true" ]; then
    echo "Using dev version:"
    mvn clean install -DskipTests -Dquarkus.package.jar.type=uber-jar 
else
    echo "Using prod version:"
    mvn clean install -DskipTests=$(build-env tests.run --not) -Dmaven.test.failure.ignore=$(build-env tests.ignoreFailures) -Dquarkus.package.jar.type=uber-jar
fi

cp $TARGET_JAR_FILE $OUTPUT_DIR

# As the jar is built and ddl files are included, cleanup by deleting the ddl files
rm -rf $DDL_DIR
