#!/bin/bash
# Copyright 2020 Red Hat, Inc. and/or its affiliates
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

echo "Cleaning up CRDs to run the scorecard tool"
for crd in deploy/crds/*_crd.yaml; do
  [[ -e "$crd" ]] || break
  oc delete -f "$crd"
done

echo "Creating namespace for Scorecard"
oc create namespace scorecard
oc project scorecard

echo "Running scorecard"
operator-sdk scorecard
exit_code=$?

echo "Scorecard finished with code ${exit_code}"

echo "Cleaning up before leaving"
oc delete namespace scorecard

echo "Reappling CRDs"
for crd in deploy/crds/*_crd.yaml; do
  [[ -e "$crd" ]] || break
  oc apply -f "$crd"
done

echo "Bye"
exit ${exit_code}
