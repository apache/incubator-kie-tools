#!/usr/bin/env bash

set -e

_BUILDER=${BUILD_ENGINE:-docker}

script_dir_path="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

${_BUILDER} build --build-arg=SCRIPT_DEBUG=${SCRIPT_DEBUG:-false} "${script_dir_path}"/resources -t quay.io/kiegroup/swf-test:latest

container_name="swf-test-$(echo $RANDOM | md5sum | head -c 5; echo;)"
${_BUILDER} run -d --name ${container_name} -p 8080:8080 quay.io/kiegroup/swf-test:latest

set -x
set +e
export _BUILDER; export container_name && timeout 20s bash -c 'result="not started"; while [[ "$result" != "healthy" ]]; \
 do sleep 2 && result=$(${_BUILDER} inspect -f {{.State.Health.Status}} ${container_name}); echo "status: $result"; done'
status_code=$?
echo "Got status from timeout -> ${status_code}"
set -e
if [[ "${status_code}" != 0 ]]; then
  echo "ERROR: Container status: $(${_BUILDER} inspect -f {{.State.Health.Status}} ${container_name})"
  ${_BUILDER} logs ${container_name}
else
  curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"workflowdata" : {"name": "John", "language": "English"}}' http://localhost:8080/jsongreet
  status_code=$?
fi

${_BUILDER} kill ${container_name}

exit ${status_code}