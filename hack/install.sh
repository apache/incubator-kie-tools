#!/bin/env bash
# Copyright 2019 Red Hat, Inc. and/or its affiliates
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

EXIT=0
FILE_FOUND=0

if [ -z "${NAMESPACE}" ]; then
  NAMESPACE="kogito"
fi

shopt -s nullglob
for file in deploy/{crds/*_crd.yaml,*.yaml}; do
  FILE_FOUND=1
  if ! kubectl apply -f "$file" -n "${NAMESPACE}"; then
    EXIT=1
    break # Don't try other files if one fails
  fi
done
shopt -u nullglob

if [[ FILE_FOUND -eq 0 ]]; then
  echo "No deployment files found" >&2
  EXIT=3
fi

exit ${EXIT}
