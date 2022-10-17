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
	BUILDER_NAMESPACE_DEFAULT     = "kogito-builder"
	BUILDER_IMG_NAME_DEFAULT      = "platform"
	BUILDER_RESOURCE_NAME_DEFAULT = "Dockerfile"
	DEFAULT_IMAGES_TAG            = ":latest"

	DEFAULT_BUILDER_RESOURCE_NAME_KEY = "DEFAULT_BUILDER_RESOURCE_NAME"
	DEFAULT_WORKFLOW_EXTENSION_KEY    = "DEFAULT_WORKFLOW_EXTENSION"
	DEFAULT_REGISTRY_REPO_KEY         = "DEFAULT_REGISTRY_REPO"
	DEFAULT_KANIKO_SECRET_KEY         = "DEFAULT_KANIKO_SECRET_DEFAULT" // #gitleaks:allow
)
