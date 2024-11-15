#!/bin/bash
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

# runs the e2e locally
# You must have minikube installed
MINIKUBE_PROFILE=${1:-minikube}
SKIP_IMG_BUILD=${2:-false}
TEST_LABELS=${3:-"flows-ephemeral"} # possible values are flows-ephemeral, flows-persistence, platform, cluster
SKIP_UNDEPLOY=${4:-false}

# Emoticons and enhanced messages
echo "🚀 Using minikube profile ${MINIKUBE_PROFILE}"
export OPERATOR_IMAGE_NAME=localhost/kogito-serverless-operator:0.0.1

# Check if the minikube registry addon is enabled
if ! minikube addons list | grep -q "registry.*enabled"; then
  echo "🔌 Enabling minikube registry addon..."
  minikube addons enable registry
else
  echo "✅  Minikube registry addon is already enabled."
fi

# clean up previous runs, hiding logs
echo "🧹 Cleaning up previous test namespaces..."
kubectl get namespaces -o name | awk -F/ '/^namespace\/test/ {print $2}' | xargs kubectl delete namespace &> /dev/null

if [ "${SKIP_UNDEPLOY}" = false ]; then
  echo "🧹 Cleaning up previous test resources namespace..."
  kubectl delete namespace e2e-resources &> /dev/null
  echo "🧹 Undeploying previous instances..."
  make undeploy ignore-not-found=true &> /dev/null
fi

# Image build process
if [ "${SKIP_IMG_BUILD}" = "false" ]; then
  # Check if cekit is available
  if ! command -v cekit &> /dev/null; then
    echo "❌  cekit command not found. Please install cekit before proceeding." >&2
    exit 1
  fi
  echo "📦 Installing required Python packages for cekit..."
  if ! pip install -r ./images/requirements.txt &> /dev/null; then
    echo "❌  Failed to install required Python packages. Please check your requirements file." >&2
    exit 1
  fi
  echo "🔨 Building operator image..."
  eval "$(minikube -p "${MINIKUBE_PROFILE}" docker-env)"
  if ! make container-build BUILDER=docker IMG="${OPERATOR_IMAGE_NAME}" ; then
    echo "❌  Failure: Failed to build image, exiting." >&2
    exit 1
  fi
else
  echo "⏩  Skipping operator image build..."
fi

# Deploy and run tests, keeping logs visible for tests only
echo "🚀 Deploying operator..."
make deploy IMG="${OPERATOR_IMAGE_NAME}" &> /dev/null
echo "🧪 Running e2e tests with label ${TEST_LABELS}..."
make test-e2e label="${TEST_LABELS}"
