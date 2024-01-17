#!/usr/bin/env bats
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


export KOGITO_HOME=$BATS_TMPDIR/kogito_home
mkdir -p "${KOGITO_HOME}"/launch

cp $BATS_TEST_DIRNAME/../../../kogito-logging/added/logging.sh "${KOGITO_HOME}"/launch/

# imports
source $BATS_TEST_DIRNAME/../../added/kogito-kubernetes-client.sh

@test "list_or_get_k8s_resource sanity check" {
    run list_or_get_k8s_resource "" "configmaps/my-config-map"

    echo "result= ${lines[@]}"

    [ "$status" -eq 0 ]
    [ "${lines[0]}" = "INFO --> [k8s-client] Trying to fetch Kubernetes API  for resource configmaps/my-config-map" ]
    [ "${lines[1]}" = "INFO --> [k8s-client] Not running on Kubernetes, skipping..." ]
}

@test "patch_json_k8s_resource sanity check" {
    run patch_json_k8s_resource "" "configmaps/my-config-map" "{ spec: {data: []}}"

    echo "result= ${lines[@]}"

    [ "$status" -eq 0 ]
    [ "${lines[0]}" = "INFO --> [k8s-client] Trying to patch resource configmaps/my-config-map in Kubernetes API " ]
    [ "${lines[1]}" = "INFO --> [k8s-client] Not running on Kubernetes, skipping..." ]
}