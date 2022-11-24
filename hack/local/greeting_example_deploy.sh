#!/bin/bash

registry=$1

if [ -z ${registry} ]; then
    registry="quay.io/${USERNAME}"
    echo "No registry given. Setting up default."
fi

img=${registry}/kogito-serverless-operator:local-test

echo "Using registry '${registry}'"
echo "Using image '${img}'"

kubectl create namespace kogito-workflows
kubectl create secret generic regcred --from-file=.dockerconfigjson=${HOME}/.docker/config.json --type=kubernetes.io/dockerconfigjson -n kogito-workflows

make docker-build docker-push IMG=${img}
make deploy IMG=${img}

cat config/samples/sw.kogito_v1alpha08_kogitoserverlessplatform.yaml | sed "s|address: .*|address: ${registry}|g" | kubectl apply -n kogito-workflows -f -

sleep 10

kubectl apply -f config/samples/sw.kogito_v1alpha08_kogitoserverlessworkflow.yaml -n kogito-workflows
