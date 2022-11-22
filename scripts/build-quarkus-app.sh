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
quarkus_version="${2}"

if [ -z ${quarkus_version} ]; then
    echo "Please provide the quarkus version"
    exit 1
fi

case ${image_name} in
    "kogito-swf-builder")        ;;
    *)
        echo "${image_name} is not a quarkus app image, exiting..."
        exit 0
        ;;
esac


script_dir_path=$(cd `dirname "${BASH_SOURCE[0]}"`; pwd -P)

target_tmp_dir="/tmp/build/kogito-swf-builder"
build_target_dir="/tmp/kogito-swf-builder"
mvn_local_repo="/tmp/temp_maven/kogito-swf-builder"

rm -rf ${target_tmp_dir} && mkdir -p ${target_tmp_dir}
rm -rf ${build_target_dir} && mkdir -p ${build_target_dir}
mkdir -p ${mvn_local_repo}

. ${script_dir_path}/setup-maven.sh "${build_target_dir}"/settings.xml

echo "Copy current maven repo to maven context local repo ${mvn_local_repo}"
cp -r ${HOME}/.m2/repository/* "${mvn_local_repo}"

set -x

echo "Create quarkus project to path ${build_target_dir}"
cd ${build_target_dir}
mvn -U "${MAVEN_OPTIONS}" \
    io.quarkus.platform:quarkus-maven-plugin:"${quarkus_version}":create \
    -Dmaven.repo.local=${mvn_local_repo} \
    -DprojectGroupId="org.acme" \
    -DprojectArtifactId="serverless-workflow-project" \
    -DprojectVersionId="1.0.0-SNAPSHOT" \
    -DplatformVersion="${quarkus_version}" \
    -Dextensions="quarkus-kubernetes,kogito-quarkus-serverless-workflow,kogito-addons-quarkus-knative-eventing"

echo "Build quarkus app"
cd "serverless-workflow-project"
mvn ${MAVEN_OPTIONS} -U clean install -DskipTests -Dmaven.repo.local=${mvn_local_repo} -Dquarkus.container-image.build=false

cd ${build_target_dir}
echo "Zip and copy scaffold project"
cd ${build_target_dir}/
zip -r kogito-swf-builder-quarkus-app.zip serverless-workflow-project/ 
cp -v kogito-swf-builder-quarkus-app.zip ${target_tmp_dir}/
echo "Zip and copy maven repo"
cd ${mvn_local_repo}
zip -r kogito-swf-builder-maven-repo.zip *
cp -v kogito-swf-builder-maven-repo.zip ${target_tmp_dir}/
