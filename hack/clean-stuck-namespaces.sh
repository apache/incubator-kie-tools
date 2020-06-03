#!/bin/bash

DIR=$(mktemp -d)

oc get projects | grep "Terminating" | awk -F " " '{print $1}' > ${DIR}/projects

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