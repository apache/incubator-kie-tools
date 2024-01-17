#!/usr/bin/env bash
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

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

