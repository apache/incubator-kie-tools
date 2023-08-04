#!/bin/bash

script_dir_path="$(cd $(dirname "${BASH_SOURCE[0]}") && pwd)"
source ${script_dir_path}/env_test.sh $@

echo "---- Pulling image ${image_full_tag} ----"
docker pull ${image_full_tag}

echo "---- Run behave test for image ${image_id} ----"
cekit --descriptor ${image_descriptor_filename} test --image ${image_full_tag} behave
