#!/usr/bin/env bash
# Parameters:
#   1 - Image name
#   2 - Quarkus platform group id
#   3 - Quarkus platform version
#   4 - Kogito version
# Note that all parameters are required and can't be empty

# fast fail
set -e
set -o pipefail

# Read entries before sourcing
image_name="${1}"
quarkus_platform_groupid="${2}"
quarkus_platform_version="${3}"
kogito_version="${KOGITO_VERSION:-${4}}"

# GAV of maven plugins to be injected in the plugin management section
maven_plugins_gav=("org.apache.maven.plugins:maven-resources-plugin:3.3.1" "org.apache.maven.plugins:maven-install-plugin:3.1.1" "org.apache.maven.plugins:maven-jar-plugin:3.3.0" "org.apache.maven.plugins:maven-clean-plugin:3.3.1")

# Properties to be replaced in the pom by the new versions
properties_with_versions=("compiler-plugin.version:3.11.0" "surefire-plugin.version:3.1.2")

# arch specific dependencies
quarkus_extensions_arch_specific="com.aayushatharva.brotli4j:native-linux-aarch64:1.8.0"
# common extensions used by the kogito-swf-builder and kogito-swf-devmode
quarkus_extensions="quarkus-kubernetes,smallrye-health,org.kie.kogito:kogito-quarkus-serverless-workflow:${kogito_version},org.kie.kogito:kogito-addons-quarkus-knative-eventing:${kogito_version},org.kie.kogito:kogito-addons-quarkus-microprofile-config-service-catalog:${kogito_version},org.kie.kogito:kogito-addons-quarkus-kubernetes:${kogito_version}"
# dev mode purpose extensions used only by the kogito-swf-devmode
kogito_swf_devmode_extensions="org.kie.kogito:kogito-quarkus-serverless-workflow-devui:${kogito_version},org.kie.kogito:kogito-addons-quarkus-source-files:${kogito_version},org.kie.kogito:kogito-addons-quarkus-process-management:${kogito_version},org.kie.kogito:kogito-addons-quarkus-jobs-service-embedded:${kogito_version},org.kie.kogito:kogito-addons-quarkus-data-index-inmemory:${kogito_version}"
# builder/prod extensitons used only by the kogito-swf-builder
kogito_swf_builder_extensions="org.kie.kogito:kogito-addons-quarkus-events-process:${kogito_version},org.kie.kogito:kogito-addons-quarkus-process-management:${kogito_version},org.kie.kogito:kogito-addons-quarkus-source-files:${kogito_version}"

if [ -z ${quarkus_platform_version} ]; then
    echo "Please provide the quarkus version"
    exit 1
fi

case ${image_name} in
    "kogito-swf-builder")
        quarkus_extensions="${quarkus_extensions},${kogito_swf_builder_extensions},${quarkus_extensions_arch_specific}"
        ;;
    "kogito-swf-devmode")
        quarkus_extensions="${quarkus_extensions},${kogito_swf_devmode_extensions},${quarkus_extensions_arch_specific}"
        ;;
    *)
        echo "${image_name} is not a quarkus app image, exiting..."
        exit 0
        ;;
esac


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

if [ "${NIGHTLY}" = "true" ]; then
    # In case of a nightly, the Kogito/Drools artifacts are not deployed anywhere, so they need to be copied
    # inside the maven repo local folder
    cp -rp ${NIGHTLY_DEPLOY_FOLDER}/* ${mvn_local_repo}
fi

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
    "${quarkus_platform_groupid}":quarkus-maven-plugin:"${quarkus_platform_version}":create

# Fix as we cannot rely on Quarkus platform
# Should be removed once https://issues.redhat.com/browse/KOGITO-9120 is implemented
if [ ! -z ${kogito_version} ]; then
    echo "Replacing Kogito Platform BOM with version ${kogito_version}"
    # [ ]* -> is a regexp pattern to match any number of spaces
    pattern_1="[ ]*<groupId>.*<\/groupId>"
    pattern_2="[ ]*<artifactId>quarkus-kogito-bom<\/artifactId>\n"
    pattern_3="[ ]*<version>.*<\/version>\n"
    complete_pattern="$pattern_1\n$pattern_2$pattern_3"

    replace_1="        <groupId>org.kie.kogito<\/groupId>\n"
    replace_2="        <artifactId>kogito-bom<\/artifactId>\n"
    replace_3="        <version>${kogito_version}<\/version>\n"
    complete_replace="$replace_1$replace_2$replace_3"

    sed -i.bak -e "/$pattern_1/{
        N;N;N
        s/$complete_pattern/$complete_replace/
        }" serverless-workflow-project/pom.xml
fi


# Inject empty plugin management section if not present in the pom.xml
if ! grep -q "<pluginManagement>" "serverless-workflow-project/pom.xml"; then
    echo "Injecting empty plugin Management section as it does not exist in pom"
    pattern_1="[ ]*<build>"
    complete_pattern="$pattern_1"

    replace_1="  <\build>\n"
    replace_2="    <pluginManagement>\n"
    replace_3="      <plugins>\n"
    replace_4="      <\/plugins>\n"
    replace_5="    <\/pluginManagement>"
    complete_replace="$replace_1$replace_2$replace_3$replace_4$replace_5"

    sed -i.bak -e "/$pattern_1/{
        N;N;N
        s/$complete_pattern/$complete_replace/
        }" serverless-workflow-project/pom.xml
fi

# Inject maven plugins into plugin management section
for gav in ${maven_plugins_gav[@]}; do
    group_id=$(echo $gav | cut -f1 -d:)
    artifact_id=$(echo $gav | cut -f2 -d:)
    version=$(echo $gav | cut -f3 -d:)

    echo "Injecting ${gav} in plugin management section"
    pattern_1="[ ]*<pluginManagement>"
    pattern_2="[ ]*<plugins>"
    complete_pattern="$pattern_1\n$pattern_2"

    replace_1="    <pluginManagement>\n"
    replace_2="      <plugins>\n"
    replace_3="        <plugin>\n"
    replace_4="          <groupId>${group_id}<\/groupId>\n"
    replace_5="          <artifactId>${artifact_id}<\/artifactId>\n"
    replace_6="          <version>${version}<\/version>\n"
    replace_7="        <\/plugin>"
    complete_replace="$replace_1$replace_2$replace_3$replace_4$replace_5$replace_6$replace_7"

    sed -i.bak -e "/$pattern_1/{
        N;N;N
        s/$complete_pattern/$complete_replace/
        }" serverless-workflow-project/pom.xml
done

# Replace properties values by new values
for property_with_version in ${properties_with_versions[@]}; do
    property=$(echo $property_with_version | cut -f1 -d:)
    new_version=$(echo $property_with_version | cut -f2 -d:)

    echo "Replacing property ${property} with value ${new_version}"
    complete_pattern="[ ]*<${property}>.*<\/${property}>"
    complete_replace="    <${property}>${new_version}<\/${property}>"
    sed -i.bak "s/$complete_pattern/$complete_replace/g" serverless-workflow-project/pom.xml
done

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