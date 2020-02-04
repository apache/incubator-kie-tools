#!/bin/env bash
# Copyright 2019 Red Hat, Inc. and/or its affiliates
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

SCRIPT_NAME=`basename $0`

function usage(){
  printf "Build the operator."
  printf "\n"
  printf "\n${SCRIPT_NAME} [options]*"
  printf "\n"
  printf "\nOptions:"
  printf "\n"
  printf "\n-h | --help\n\tPrint the usage of this script."
  printf "\n--image_registry {IMAGE_REGISTRY}\n\tSet the Image Registry."
  printf "\n--image_name {IMAGE_NAME}\n\tSet the Image Name."
  printf "\n--image_tag {IMAGE_TAG}\n\tSet the Image Tag."
  printf "\n--image_builder {IMAGE_BUILDER}\n\tSet the Image Builder to build the Operator image (docker [default], podman or buildah)."
  printf "\n"
}

. ./hack/go-mod-env.sh

# Default values
REPO=https://github.com/kiegroup/kogito-cloud-operator
BRANCH=master
REGISTRY=quay.io/kiegroup
IMAGE=kogito-cloud-operator
TAG=0.8.0
TAR=${BRANCH}.tar.gz
URL=${REPO}/archive/${TAR}
CFLAGS="--redhat --build-tech-preview"
BUILDER=

while (( $# ))
do
case $1 in
  --image_registry)
    shift
    if [[ ! ${1} =~ ^-.* ]] && [[ ! -z "${1}" ]]; then export REGISTRY="${1}"; shift; fi
  ;;
  --image_name)
    shift
    if [[ ! ${1} =~ ^-.* ]] && [[ ! -z "${1}" ]]; then export IMAGE="${1}"; shift; fi
  ;;
  --image_tag)
    shift
    if [[ ! ${1} =~ ^-.* ]] && [[ ! -z "${1}" ]]; then export TAG="${1}"; shift; fi
  ;;
  --image_builder)
    shift
    if [[ ! ${1} =~ ^-.* ]] && [[ ! -z "${1}" ]]; then export BUILDER="${1}"; shift; fi
  ;;
  -h|--help)
    usage
    exit 0
  ;;
  *)
    echo "Unknown arguments: ${1}"
    usage
    exit 1
  ;;
esac
done

setGoModEnv
go generate ./...
if [[ -z ${CI} ]]; then
    ./hack/go-test.sh
    BUILD_PARAMS=""
    if [[ ! -z ${BUILDER} ]]; then BUILD_PARAMS="${BUILD_PARAMS} --image-builder ${BUILDER}"; fi
    operator-sdk build ${REGISTRY}/${IMAGE}:${TAG} ${BUILD_PARAMS}
    if [[ ${1} == "rhel" ]]; then
        if [[ ${LOCAL} != true ]]; then
            CFLAGS+=" --build-engine=osbs --build-osbs-target=??"
            if [[ ${2} == "release" ]]; then
                CFLAGS+=" --build-osbs-release"
            fi
        fi
        wget -q ${URL} -O ${TAR}
        MD5=$(md5sum ${TAR} | awk {'print $1'})
        rm ${TAR}

        echo "${CFLAGS}"
        cekit build "${CFLAGS}" \
            --overrides "{'artifacts': [{'name': 'kogito-operator.tar.gz', 'md5': '${MD5}', 'url': '${URL}'}]}"
    fi
else
    CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -v -a -o build/_output/bin/kogito-cloud-operator github.com/kiegroup/kogito-cloud-operator/cmd/manager
fi
