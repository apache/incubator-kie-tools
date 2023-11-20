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

DIR=$(mktemp -d)

oc get namespaces | grep "Terminating" | awk -F " " '{print $1}' > ${DIR}/projects

while read project
do 
	echo "Stuck project ${project}"

    for resource in $(getAllDependentCrds all)
    do
      oc get $resource -n "${project}" | grep -v "NAME" | awk -F " " '{print $1}' > ${DIR}/$resource-instances
      while read instance
      do
          echo "Remove finalizer from $resource ${instance} from project ${project}"

          oc patch $resource ${instance} -n ${project} -p '{"metadata":{"finalizers":[]}}' --type=merge
      done < ${DIR}/$resource-instances
      rm ${DIR}/$resource-instances
    done
done < ${DIR}/projects

echo "Projects deleted:"
cat ${DIR}/projects

# Cleanup
rm ${DIR}/projects