#!/usr/bin/env bash

script_dir_path="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

docker build "${script_dir_path}" -t quay.io/kiegroup/swf-test:latest

docker run -d -p 8080:8080 quay.io/kiegroup/swf-test:latest

container_name=$(docker ps -n 1 --format '{{.Names}}')
container_id=$(docker ps -n 1 --format '{{.ID}}')
echo "Got container name ${container_name}"
echo "Got container id ${container_id}"

set -x
CONTAINER_NAME="${container_name}" timeout 10s bash -c 'while [[ "$(docker inspect -f {{.State.Health.Status}} ${CONTAINER_NAME})" != "healthy" ]] ; do sleep 2 &&  docker inspect -f {{.State.Health.Status}} ${CONTAINER_NAME}; done'
status_code=$? 
if [[ "${status_code}" != 0 ]]; then
  echo "ERROR: Container status: $(docker inspect -f {{.State.Health.Status}} ${container_name})"
else
  curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"workflowdata" : {"name": "John", "language": "English"}}' http://localhost:8080/jsongreet
  status_code=$?
fi

docker kill ${container_id}

exit ${status_code}