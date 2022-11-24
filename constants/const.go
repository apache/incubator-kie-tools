/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package constants

const (
	BUILDER_CM_NAME               = "kogito-serverless-operator-builder-config"
	BUILDER_RESOURCE_NAME_DEFAULT = "Dockerfile"
	DEFAULT_IMAGES_TAG            = ":latest"

	DEFAULT_BUILDER_RESOURCE_NAME_KEY = "DEFAULT_BUILDER_RESOURCE_NAME"
	DEFAULT_WORKFLOW_EXTENSION_KEY    = "DEFAULT_WORKFLOW_EXTENSION"

	DEFAULT_KOGITO_EXPLANG = "jq"

	DEFAULT_KAKIKO_PVC_SIZE = "1Gi"

	//TODO Move this constants into the kogito-builder defaults
	VERSION = "v0.8"

	DEFAULT_KANIKOCACHE_PVC_NAME = "kogito-kaniko-cache-pv"

	CUSTOM_NS_KEY          = "build-namespace"
	CUSTOM_REG_CRED_KEY    = "registry-secret"
	CUSTOM_REG_ADDRESS_KEY = "registry-address"
)
