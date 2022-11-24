#!/bin/bash

remove_operator=${1}

kubectl delete -f config/samples/sw.kogito_v1alpha08_kogitoserverlessworkflow.yaml -n kogito-workflows

if [ "${remove_operator}" = '-A' ] || [ "${remove_operator}" = '--all' ]; then
    echo 'Removing the operator from the cluster'

    kubectl delete namespace kogito-workflows
    make undeploy
fi