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


. ./hack/go-mod-env.sh

REPO=https://github.com/kiegroup/kogito-cloud-operator
BRANCH=master
REGISTRY=quay.io/kiegroup
IMAGE=kogito-cloud-operator
TAG=0.7.0-rc4
TAR=${BRANCH}.tar.gz
URL=${REPO}/archive/${TAR}
CFLAGS="--redhat --build-tech-preview"

setGoModEnv
go generate ./...
if [[ -z ${CI} ]]; then
    ./hack/go-test.sh
    operator-sdk build ${REGISTRY}/${IMAGE}:${TAG}
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
    CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -mod=vendor -v -a -o build/_output/bin/kogito-cloud-operator github.com/kiegroup/kogito-cloud-operator/cmd/manager
fi
