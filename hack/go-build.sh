#!/bin/bash
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


function usage(){
  printf "Build the operator."
  printf "\n"
  printf "\n[options]*"
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

# include
source ./hack/go-mod-env.sh
source ./hack/export-version.sh

# Default values
REGISTRY=quay.io/kiegroup
IMAGE=kogito-cloud-operator
TAG=0.12.0
BINARY_OUTPUT=build/_output/bin/kogito-cloud-operator

setGoModEnv
go generate ./...

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

if [[ -z ${CUSTOM_IMAGE_TAG} ]]; then
    CUSTOM_IMAGE_TAG=${REGISTRY}/${IMAGE}:${TAG}
fi
if [[ -z ${BUILDER} ]]; then
    BUILDER=podman
fi

CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -v -a -o ${BINARY_OUTPUT} github.com/kiegroup/kogito-cloud-operator/cmd/manager

operator-sdk build "${CUSTOM_IMAGE_TAG}" --image-builder ${BUILDER}