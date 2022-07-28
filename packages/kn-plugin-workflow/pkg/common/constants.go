/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package common

type DependenciesVersion struct {
	QuarkusVersion string
	KogitoVersion  string
}

const (
	// Extensions
	QUARKUS_KUBERNETES_EXTENSION                     = "io.quarkus:quarkus-kubernetes"
	QUARKUS_RESTEASY_REACTIVE_JACKSON_EXTENSION      = "io.quarkus:quarkus-resteasy-reactive-jackson"
	QUARKUS_CONTAINER_IMAGE_JIB                      = "io.quarkus:quarkus-container-image-jib"
	QUARKUS_CONTAINER_IMAGE_DOCKER                   = "io.quarkus:quarkus-container-image-docker"
	KOGITO_QUARKUS_SERVERLESS_WORKFLOW_EXTENSION     = "org.kie.kogito:kogito-quarkus-serverless-workflow"
	KOGITO_ADDONS_QUARKUS_KNATIVE_EVENTING_EXTENSION = "org.kie.kogito:kogito-addons-quarkus-knative-eventing"

	// Versions
	JAVA_VERSION        = 11
	MAVEN_MAJOR_VERSION = 3
	MAVEN_MINOR_VERSION = 8

	// Default values
	DEFAULT_REGISTRY = "quay.io"
	DEFAULT_TAG      = "latest"

	// Filenames
	WORKFLOW_CONFIG_YML = "workflow.config.yml"
	WORKFLOW_SW_JSON    = "workflow.sw.json"
	WORKFLOW_DOCKERFILE = "Dockerfile.workflow"

	// Docker
	// build-arg
	DOCKER_BUILD_ARG_WORKFLOW_FILE            = "workflow_file"
	DOCKER_BUILD_ARG_EXTENSIONS               = "extensions"
	DOCKER_BUILD_ARG_WORKFLOW_NAME            = "workflow_name"
	DOCKER_BUILD_ARG_CONTAINER_IMAGE_REGISTRY = "container_registry"
	DOCKER_BUILD_ARG_CONTAINER_IMAGE_GROUP    = "container_group"
	DOCKER_BUILD_ARG_CONTAINER_IMAGE_NAME     = "container_name"
	DOCKER_BUILD_ARG_CONTAINER_IMAGE_TAG      = "container_tag"
)
