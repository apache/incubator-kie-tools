#!/usr/bin/env bash
set -e

script_dir_path="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

image_name=$1
image_version=$2

if [ -z "${image_name}" ]; then
  echo "Please provide the image name"
  exit 1
fi

if [ -z "${image_version}" ]; then
  echo "Please provide the X.Y version"
  exit 1
fi

export IMAGE_VERSION=${image_version}

if [ -d  "${script_dir_path}/${image_name}" ]; then
  curl -Ls https://sh.jbang.dev | bash -s - "${script_dir_path}/${image_name}/RunTests.java" "${script_dir_path}/../../target/shell/${image_name}"
else
  echo "No shell test to run for image ${image_name}"
fi