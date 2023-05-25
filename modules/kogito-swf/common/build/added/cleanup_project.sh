#!/usr/bin/env bash
set -e

#remove unnecessary files
echo 'Clean unnecessary files'
rm -rfv "${PROJECT_ARTIFACT_ID}"/target
rm -rfv "${PROJECT_ARTIFACT_ID}"/src/main/resources/*
rm -rfv "${PROJECT_ARTIFACT_ID}"/src/main/docker
rm -rfv "${PROJECT_ARTIFACT_ID}"/.mvn/wrapper
rm -rfv "${PROJECT_ARTIFACT_ID}"/mvnw*
rm -rfv "${PROJECT_ARTIFACT_ID}"/src/test
rm -rfv "${PROJECT_ARTIFACT_ID}"/*.bak

# Maven useless files
# Needed to avoid Maven to automatically re-download from original Maven repository ...
echo 'Clean Maven useless files'
find "${KOGITO_HOME}"/.m2/repository -name _remote.repositories -type f -delete
find "${KOGITO_HOME}"/.m2/repository -name _maven.repositories -type f -delete
find "${KOGITO_HOME}"/.m2/repository -name *.lastUpdated -type f -delete