#!/usr/bin/env bash
set -e

script_dir_path="$(cd $(dirname "${BASH_SOURCE[0]}") && pwd)"

source "${script_dir_path}"/logging.sh

if [ "${SCRIPT_DEBUG}" = "true" ] ; then
    set -x
    export MAVEN_ARGS_APPEND="${MAVEN_ARGS_APPEND} -X --batch-mode" 
    log_info "Script debugging is enabled, allowing bash commands and their arguments to be printed as they are executed"
    printenv
fi

source "${script_dir_path}"/configure-jvm-mvn.sh

"${MAVEN_HOME}"/bin/mvn -B -s "${MAVEN_SETTINGS_PATH}" \
  -nsu \
  io.quarkus.platform:quarkus-maven-plugin:"${QUARKUS_PLATFORM_VERSION}":create ${QUARKUS_CREATE_ARGS} \
  -DprojectGroupId="${PROJECT_GROUP_ID}" \
  -DprojectArtifactId="${PROJECT_ARTIFACT_ID}" \
  -DprojectVersionId="${PROJECT_VERSION}" \
  -DplatformVersion="${QUARKUS_PLATFORM_VERSION}" \
  -Dextensions="${QUARKUS_EXTENSIONS}"

cd "${PROJECT_ARTIFACT_ID}"

# Fix as we cannot rely on Quarkus platform
# Should be removed once https://issues.redhat.com/browse/KOGITO-9120 is implemented
if [ ! -z ${KOGITO_VERSION} ]; then
    echo "Replacing Kogito Platform BOM with version ${KOGITO_VERSION}"
    # [ ]* -> is a regexp pattern to match any number of spaces
    pattern_1="[ ]*<groupId>.*<\/groupId>"
    pattern_2="[ ]*<artifactId>quarkus-kogito-bom<\/artifactId>\n"
    pattern_3="[ ]*<version>.*<\/version>\n"
    complete_pattern="$pattern_1\n$pattern_2$pattern_3"

    replace_1="        <groupId>org.kie.kogito<\/groupId>\n"
    replace_2="        <artifactId>kogito-bom<\/artifactId>\n"
    replace_3="        <version>${KOGITO_VERSION}<\/version>\n"
    complete_replace="$replace_1$replace_2$replace_3"

    sed -i.bak -e "/$pattern_1/{
        N;N;N
        s/$complete_pattern/$complete_replace/
        }" pom.xml
fi

"${MAVEN_HOME}"/bin/mvn -B ${MAVEN_ARGS_APPEND} \
  -nsu \
  -s "${MAVEN_SETTINGS_PATH}" \
  -DskipTests \
  -Dquarkus.container-image.build=false \
   clean install
