#!/usr/bin/env bash
# Parameters:
#   1 - image name - can't  be empty.
#   2 - git target branch - defaults to main
#   3 - git target uri - defaults to https://github.com/kiegroup/kogito-apps.git

# fast fail
set -e
set -o pipefail

# Read entries before sourcing
image_name="${1}"
quarkus_platform_version="${2}"

quarkus_extensions='quarkus-kubernetes,kogito-quarkus-serverless-workflow,kogito-addons-quarkus-knative-eventing,smallrye-health'

if [ -z ${quarkus_platform_version} ]; then
    echo "Please provide the quarkus version"
    exit 1
fi

case ${image_name} in
    "kogito-swf-builder")        ;;
    "kogito-swf-devmode")        
        quarkus_extensions="${quarkus_extensions},kogito-quarkus-serverless-workflow-devui,kogito-addons-quarkus-source-files"
        ;;
    *)
        echo "${image_name} is not a quarkus app image, exiting..."
        exit 0
        ;;
esac


script_dir_path=$(cd `dirname "${BASH_SOURCE[0]}"`; pwd -P)

target_tmp_dir="/tmp/build/${image_name}"
build_target_dir="/tmp/${image_name}"
mvn_local_repo="/tmp/temp_maven/${image_name}"

rm -rf ${target_tmp_dir} && mkdir -p ${target_tmp_dir}
rm -rf ${build_target_dir} && mkdir -p ${build_target_dir}
if [ "${CI}" = "true" ]; then
    # On CI we want to make sure we remove all artifacts from maven repo
    rm -rf ${mvn_local_repo}
fi
mkdir -p ${mvn_local_repo}

. ${script_dir_path}/setup-maven.sh "${build_target_dir}"/settings.xml

set -x
echo "Create quarkus project to path ${build_target_dir}"
cd ${build_target_dir}
mvn ${MAVEN_OPTIONS} \
    -Dmaven.repo.local=${mvn_local_repo} \
    -DprojectGroupId="org.acme" \
    -DprojectArtifactId="serverless-workflow-project" \
    -DprojectVersionId="1.0.0-SNAPSHOT" \
    -DplatformVersion="${quarkus_platform_version}" \
    -Dextensions="${quarkus_extensions}" \
    io.quarkus.platform:quarkus-maven-plugin:"${quarkus_platform_version}":create

echo "Build quarkus app"
cd "serverless-workflow-project"
# Quarkus version is enforced if some dependency pulled has older version of Quarkus set.
# This avoids to have, for example, Quarkus BOMs or other artifacts with multiple versions.
mvn ${MAVEN_OPTIONS} \
    -DskipTests \
    -Dmaven.repo.local=${mvn_local_repo} \
    -Dquarkus.container-image.build=false \
    clean install

cd ${build_target_dir}

#remove unnecessary files
rm -rfv serverless-workflow-project/target
rm -rfv serverless-workflow-project/src/main/resources/*
rm -rfv serverless-workflow-project/src/main/docker
rm -rfv serverless-workflow-project/.mvn/wrapper
rm -rfv serverless-workflow-project/mvnw*
rm -rfv serverless-workflow-project/src/test
rm -rfv serverless-workflow-project/*.bak

# Maven useless files
# Needed to avoid Maven to automatically re-download from original Maven repository ...
find ${mvn_local_repo} -name _remote.repositories -type f -delete
find ${mvn_local_repo} -name _maven.repositories -type f -delete
find ${mvn_local_repo} -name *.lastUpdated -type f -delete

echo "Zip and copy scaffold project"
zip -r ${image_name}-quarkus-app.zip serverless-workflow-project/ 
cp -v ${image_name}-quarkus-app.zip ${target_tmp_dir}/
echo "Zip and copy maven repo"
cd ${mvn_local_repo}
zip -r ${image_name}-maven-repo.zip *
cp -v ${image_name}-maven-repo.zip ${target_tmp_dir}/
