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
TARGET_FILE=./cekit/modules/kogito-postgres-db-migration-deps/sonataflow-db-migrator-runner.jar

if [ -f "$SOURCE_FILE" ]; then
   echo "The file: $SOURCE_FILE found and will be used to build image."
   rm -f $TARGET_FILE
   cp $SOURCE_FILE $TARGET_FILE

   # Build the container image
   cd ./cekit || exit
   cekit -v build "$CEKIT_BUILDER"
   rm -f $TARGET_FILE
else
   echo "The file: $SOURCE_FILE not found. Please build kogito-db-migrator-tool package first before building the image."
   exit 1
fi