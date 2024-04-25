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

# Parameters:
#   1 - image name - can't  be empty.
#   2 - git target branch - defaults to main
#   3 - git target uri - defaults to https://github.com/apache/incubator-kie-kogito-apps.git

# fast fail
set -e
set -o pipefail

KOGITO_APPS_REPO_NAME="incubator-kie-kogito-apps"
KOGITO_APPS_FOLDER_NAME="kogito-apps"

# Read entries before sourcing
imageName="${1}"
gitBranch="${2:-main}"
gitUri="${3:-https://github.com/apache/${KOGITO_APPS_REPO_NAME}.git}"
contextDir=""
shift $#

script_dir_path=$(cd `dirname "${BASH_SOURCE[0]}"`; pwd -P)

export NODE_OPTIONS="${NODE_OPTIONS} --max_old_space_size=4096"
APPS_MAVEN_OPTIONS="-Dquarkus.package.type=fast-jar -Dquarkus.build.image=false"
# used for all-in-one image
extended_context=""

# Fix taken from https://github.com/apache/incubator-kie-kogito-apps/pull/1762
if [ ! -z "${CYPRESS_BINARY_URL}" ]; then
    export CYPRESS_INSTALL_BINARY="${CYPRESS_BINARY_URL}/cypress-9.7.0.zip"
    echo "Setting 'CYPRESS_INSTALL_BINARY' variable to ${CYPRESS_INSTALL_BINARY}"
fi

case ${imageName} in
    "kogito-data-index-ephemeral")
        contextDir="data-index/data-index-service/data-index-service-inmemory"
        ;;
    "kogito-data-index-postgresql")
        contextDir="data-index/data-index-service/data-index-service-postgresql"
        ;;
    "kogito-jobs-service-ephemeral")
        contextDir="jobs-service/jobs-service-inmemory"
        ;;
    "kogito-jobs-service-postgresql")
        contextDir="jobs-service/jobs-service-postgresql"
        ;;
    "kogito-jobs-service-allinone")
        extended_context="-all-in-one"
        contextDir="jobs-service/jobs-service-inmemory"
        contextDir="${contextDir} jobs-service/jobs-service-postgresql"
        ;;
    "kogito-jit-runner")
        contextDir="jitexecutor/jitexecutor-runner"
        ;;
    *)
        echo "${imageName} is not a supporting service image or can't be built from sources, exiting..."
        exit 0
        ;;
esac

for ctx in ${contextDir}; do
    target_tmp_dir="/tmp/build/$(basename ${ctx})${extended_context}"
    build_target_dir="/tmp/$(basename ${ctx})${extended_context}"
    mvn_local_repo="/tmp/temp_maven/$(basename ${ctx})${extended_context}"

    rm -rf ${target_tmp_dir} && mkdir -p ${target_tmp_dir}
    rm -rf ${build_target_dir} && mkdir -p ${build_target_dir}
    mkdir -p ${mvn_local_repo}

    . ${script_dir_path}/setup-maven.sh "${build_target_dir}"/settings.xml
    MAVEN_OPTIONS="${MAVEN_OPTIONS} ${APPS_MAVEN_OPTIONS}"

    if stat ${HOME}/.m2/repository/ &> /dev/null; then
        echo "Copy current maven repo to maven context local repo ${mvn_local_repo}"
        cp -r ${HOME}/.m2/repository/* "${mvn_local_repo}"
    fi

    cd ${build_target_dir}
    echo "Using branch/tag ${gitBranch}, checking out. Temporary build dir is ${build_target_dir} and target dist is ${target_tmp_dir}"

    KOGITO_APPS_DIR=${build_target_dir}/${KOGITO_APPS_FOLDER_NAME}
    if [ ! -d "${KOGITO_APPS_DIR}" ]; then
        git_command="git clone --single-branch --branch ${gitBranch} --depth 1 ${gitUri} ${KOGITO_APPS_DIR}"
        echo "cloning ${KOGITO_APPS_REPO_NAME} with the following git command: ${git_command}"
        eval ${git_command}
    fi
    cd ${KOGITO_APPS_DIR} && echo "working dir `pwd`"
    echo "Got MAVEN_OPTIONS = ${MAVEN_OPTIONS}"
    mvn_command="mvn -am -pl ${ctx} package ${MAVEN_OPTIONS} -Dmaven.repo.local=${mvn_local_repo} -Dquarkus.container-image.build=false"
    echo "Building component(s) ${contextDir} with the following maven command [${mvn_command}]"
    export YARN_CACHE_FOLDER=/tmp/cache/yarn/${ctx} # Fix for building yarn apps in parallel
    export CYPRESS_CACHE_FOLDER=/tmp/cache/cypress/${ctx} # https://docs.cypress.io/guides/getting-started/installing-cypress#Advanced
    eval ${mvn_command}
    cd ${ctx}/target
    cp -vr quarkus-app ${target_tmp_dir}/
    cd -
done
