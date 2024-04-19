#!/bin/bash
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

script_dir_path=`dirname "${BASH_SOURCE[0]}"`
source ${script_dir_path}/env.sh

function clean_installed_operators() {
  echo "--- Clean installplans in $1"
  get_and_clean_resources $1 'installplans.operators.coreos.com'
  echo "--- Clean subscriptions in $1"
  get_and_clean_resources $1 'subscriptions.operators.coreos.com'
  echo "--- Clean clusterserviceversions in $1"
  get_and_clean_resources $1 'clusterserviceversions.operators.coreos.com'
}

clean_installed_operators 'openshift-operators'
clean_installed_operators 'operators'
