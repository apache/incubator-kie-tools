#!/bin/bash

script_dir_path="$(cd $(dirname "${BASH_SOURCE[0]}") && pwd)"
source ${script_dir_path}/env_test.sh $@

echo "---- Pulling image ${image_full_tag} ----"
docker pull ${image_full_tag}

echo "---- Run jBang test for image ${image_id} ----"
tests/shell/run.sh ${community_image_id} ${image_full_tag}
