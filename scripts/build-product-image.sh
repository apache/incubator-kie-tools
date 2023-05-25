#!/usr/bin/env bash
# Simple usage: /bin/sh scripts/build-product-image.sh "build" ${IMAGE_NAME} ${BUILD_ENGINE}

ver=$(cekit --version )
ver=$((${ver//./} + 0))
if [ ${ver//./} -lt 379 ]; then
    echo "Using CEKit version $ver, Please use CEKit version 3.8.0 or greater."
    exit 10
fi

image="${2}"
if [ "x${image}" == "x" ]; then
    echo "image_name can't be empty.."
    exit 8
fi

BUILD_ENGINE="${3:-docker}"
CEKIT_CMD="cekit --verbose --redhat"

ACTION=${1}
case ${ACTION} in
    "build")
        echo "Using ${BUILD_ENGINE} build engine"
        ${CEKIT_CMD} --descriptor ${image_name}-image.yaml build ${BUILD_ENGINE}
    ;;

    "test")
        ${CEKIT_CMD} --descriptor ${image_name}-image.yaml test behave $3
    ;;
    *)
        echo "Please use build or test actions."
    ;;
esac

