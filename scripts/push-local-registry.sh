#!/usr/bin/env bash
# Simple usage: /bin/sh scripts/push-local-registry.sh ${REGISTRY} ${SHORTENED_LATEST_VERSION} ${NS}

BUILD_ENGINE="docker"

registry=${REGISTRY:-{1}}
version=${2:-latest}
namespace=${3:-openshift}

if [ "${registry}x" == "x"  ]; then
    echo "No registry provided, please set the env REGISTRY or set it as parameter to this script"
    echo "Simple usage: /bin/sh scripts/push-local-registry.sh ${REGISTRY} ${SHORTENED_LATEST_VERSION} ${NS}"
    exit 1
fi
if [ "${version}" == "latest"  ]; then
    echo "No version provided, latest will be used"
fi
if [ "${namespace}" == "openshift"  ]; then
    echo "No namespace provided, images will be installed on openshift namespace"
fi

echo "Images version ${version} will be pushed to registry ${registry}"

while read image; do
    echo "tagging image ${image} to ${registry}/${namespace}/${image}:${version}"
    ${BUILD_ENGINE} tag quay.io/kiegroup/${image}:${version} ${registry}/${namespace}/${image}:${version}
    echo "Deleting imagestream ${image} if exists `oc delete oc -n ${namespace} ${image}`"
    ${BUILD_ENGINE} push ${registry}/${namespace}/${image}:${version}
done <<<$(python scripts/list-images.py)

