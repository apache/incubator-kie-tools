#!/usr/bin/env bash
set -e

BUILD_OUTPUT="${KOGITO_HOME}"/build_output/

mkdir -p "${BUILD_OUTPUT}"

echo "Zip and copy scaffold project"
zip -r kogito-swf-quarkus-app.zip "${PROJECT_ARTIFACT_ID}"/ 
cp -v kogito-swf-quarkus-app.zip "${BUILD_OUTPUT}"

echo "Zip and copy maven repo"
cd "${KOGITO_HOME}"/.m2/repository/
zip -r kogito-swf-maven-repo.zip *
cp -v kogito-swf-maven-repo.zip "${BUILD_OUTPUT}"