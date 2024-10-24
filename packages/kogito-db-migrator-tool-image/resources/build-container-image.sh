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

CEKIT_BUILDER=docker
SOURCE_FILE=/tmp/kogito-db-migrator-tool/sonataflow-db-migrator-0.0.0-runner.jar
TARGET_FILE=./resources/modules/kogito-postgres-db-migration-deps/sonataflow-db-migrator-runner.jar
CEKIT_DESCRIPTOR_FILE=build/kogito-db-migrator-tool-image.yaml

echo "The file: $SOURCE_FILE found and will be used to build image."
rm -f $TARGET_FILE
cp $SOURCE_FILE $TARGET_FILE

# Build the container image
cekit --descriptor $CEKIT_DESCRIPTOR_FILE -v build "$CEKIT_BUILDER"
cd .. || exit
rm -f $TARGET_FILE