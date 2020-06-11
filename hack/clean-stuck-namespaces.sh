#!/bin/bash
# Copyright 2020 Red Hat, Inc. and/or its affiliates
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


DIR=$(mktemp -d)

oc get namespaces | grep "Terminating" | awk -F " " '{print $1}' > ${DIR}/projects

while read project
do 
	echo "Stuck project ${project}"

    oc get infinispan -n "${project}" | grep -v "NAME" | awk -F " " '{print $1}' > ${DIR}/infinispan-instances
    while read infinispan
    do
        echo "Remove finalizer from infinispan ${infinispan} from project ${project}"

        oc get infinispan ${infinispan} -o yaml -n ${project} | grep -v "finalizer" > ${DIR}/infinispan
        oc replace -f ${DIR}/infinispan -n ${project}
    done < ${DIR}/infinispan-instances
done < ${DIR}/projects

echo "Projects deleted:"
cat ${DIR}/projects

# Cleanup
rm ${DIR}/projects
rm ${DIR}/infinispan-instances
rm ${DIR}/infinispan