#!/usr/bin/env bash
set -e

script_dir_path="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

image_name=$1
image_tag=$2

if [ -z "${image_name}" ]; then
  echo "Please provide the image id to test"
  exit 1
fi

if [ -z "${image_tag}" ]; then
  echo "Please provide the container image full tag (ie 'registry/namespace/image:version')"
  exit 1
fi

export TEST_IMAGE="${image_tag}"
export OUTPUT_DIR="${script_dir_path}/../../target/shell/${image_name}"
export TESTS_SCRIPT_DIR_PATH="${script_dir_path}/${image_name}"

echo "image_name=${image_name}"
echo "TEST_IMAGE=${TEST_IMAGE}"
echo "OUTPUT_DIR=${OUTPUT_DIR}"
echo "TESTS_SCRIPT_DIR_PATH=${TESTS_SCRIPT_DIR_PATH}"

if [ -d  "${script_dir_path}/${image_name}" ]; then
  curl -Ls https://sh.jbang.dev | bash -s - "${TESTS_SCRIPT_DIR_PATH}/src/RunTests.java"
else
  echo "No shell test to run for image ${image_name}"
fi