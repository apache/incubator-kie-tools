#!/usr/bin/env bash
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#


# imports
source "${KOGITO_HOME}"/launch/logging.sh

# checks if this script is running on a Kubernetes environment
function is_running_on_kubernetes() {
    if [ -e /var/run/secrets/kubernetes.io/serviceaccount/token ]; then
        return 0
    else
        return 1
    fi
}

# lists or gets a resource kind in the given api within the namespace and return a JSON response
# ${1} api, e.g. "apis/apps" (for core APIs, use just "api")
# ${2} resource, e.g. "deployments/${resource_name}". To list a resource, don't specify the resource name
# ${3} labels (list only), restrict resources by these labels. e.g. app=myapp. Defaults to empty
# ${4} fields (list only), restrict resources by these fields. e.g. metadata.name=${resource_name}. see: https://kubernetes.io/docs/concepts/overview/working-with-objects/field-selectors/
# see: https://kubernetes.io/docs/reference/generated/kubernetes-api/v1.14/
# to parse this response, use jq: https://stedolan.github.io/jq/tutorial/
function list_or_get_k8s_resource() {
    local api="${1}"
    local resource="${2}"
    local labels="${3}"
    local fields="${4}"
    
    log_info "--> [k8s-client] Trying to fetch Kubernetes API ${api} for resource ${resource}"

    if is_running_on_kubernetes; then 
        namespace=$(cat /var/run/secrets/kubernetes.io/serviceaccount/namespace)
        token=$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)
        response=$(curl -s -w "%{http_code}" --cacert /var/run/secrets/kubernetes.io/serviceaccount/ca.crt \
            -H "Authorization: Bearer $token" \
            -H 'Accept: application/json' \
            "${KUBERNETES_SERVICE_PROTOCOL:-https}://${KUBERNETES_SERVICE_HOST:-kubernetes.default.svc}:${KUBERNETES_SERVICE_PORT:-443}/${api}/v1/namespaces/${namespace}/${resource}?labelSelector=${labels}\&fieldSelector=${fields}")
        log_info "${response}"
    else
        log_info "--> [k8s-client] Not running on Kubernetes, skipping..."
    fi
}

# sends a patch request to the Kubernetes API using JSON merge strategy
# ${1} api, e.g. "apis/apps" (for core APIs, leave in blank)
# ${2} resource, e.g. "deployments/${resource_name}"
# ${3} json_body file in JSON Patch format: http://jsonpatch.com/ Example: [ { "op": "replace", "path": "/metadata/annotations", "value" : {} }, {"op": "replace", "path": "/data", "value": {"file1.proto", "file2.proto"}  } ]
# see: https://kubernetes.io/docs/tasks/run-application/update-api-object-kubectl-patch/#use-a-json-merge-patch-to-update-a-deployment
function patch_json_k8s_resource() {
    local api="${1}"
    local resource="${2}"
    local file="${3}"
    
    log_info "--> [k8s-client] Trying to patch resource ${resource} in Kubernetes API ${api}"

    if is_running_on_kubernetes; then
        namespace=$(cat /var/run/secrets/kubernetes.io/serviceaccount/namespace)
        token=$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)
        response=$(curl --request PATCH -s -w "%{http_code}" --cacert /var/run/secrets/kubernetes.io/serviceaccount/ca.crt \
            -H "Authorization: Bearer $token" \
            -H 'Accept: application/json' \
            -H 'Content-Type: application/json-patch+json' \
            --data "@${file}" \
            "${KUBERNETES_SERVICE_PROTOCOL:-https}://${KUBERNETES_SERVICE_HOST:-kubernetes.default.svc}:${KUBERNETES_SERVICE_PORT:-443}/${api}/v1/namespaces/${namespace}/${resource}")
        echo "${response}"
    else
        log_info "--> [k8s-client] Not running on Kubernetes, skipping..."
    fi
}