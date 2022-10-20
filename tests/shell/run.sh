#!/usr/bin/env bash
set -e

script_dir_path="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

image_name=$1

if [ -z "${image_name}" ]; then
  echo "Please provide the image name"
  exit 1
fi

if [ -d  "${script_dir_path}/${image_name}" ]; then
  "${script_dir_path}/${image_name}"/run.sh
else
  echo "No shell test to run for image ${image_name}"
fi