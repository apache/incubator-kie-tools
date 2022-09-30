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

const (
	QUARKUS_MAVEN_PLUGIN                             = "quarkus-maven-plugin"
	QUARKUS_KUBERNETES_EXTENSION                     = "quarkus-kubernetes"
	QUARKUS_RESTEASY_REACTIVE_JACKSON_EXTENSION      = "quarkus-resteasy-reactive-jackson"
	QUARKUS_CONTAINER_IMAGE_JIB                      = "quarkus-container-image-jib"
	QUARKUS_CONTAINER_IMAGE_DOCKER                   = "quarkus-container-image-docker"
	KOGITO_QUARKUS_SERVERLESS_WORKFLOW_EXTENSION     = "kogito-quarkus-serverless-workflow"
	KOGITO_ADDONS_QUARKUS_KNATIVE_EVENTING_EXTENSION = "kogito-addons-quarkus-knative-eventing"

	// Versions
	JAVA_VERSION        = 11
	MAVEN_MAJOR_VERSION = 3
	MAVEN_MINOR_VERSION = 8

	// Default values
	KN_WORKFLOW_NAME    string = "kn-workflow"
	DEFAULT_TAG         string = "latest"
	WORKFLOW_SW_JSON    string = "workflow.sw.json"
	WORKFLOW_DOCKERFILE string = "Dockerfile.workflow"

	// Docker
	// build-arg
	DOCKER_BUILD_ARG_WORKFLOW_FILE            string = "workflow_file"
	DOCKER_BUILD_ARG_EXTENSIONS               string = "extensions"
	DOCKER_BUILD_ARG_EXTENSIONS_LIST          string = "extensions-list"
	DOCKER_BUILD_ARG_WORKFLOW_NAME            string = "workflow_name"
	DOCKER_BUILD_ARG_CONTAINER_IMAGE_REGISTRY string = "container_registry"
	DOCKER_BUILD_ARG_CONTAINER_IMAGE_GROUP    string = "container_group"
	DOCKER_BUILD_ARG_CONTAINER_IMAGE_NAME     string = "container_name"
	DOCKER_BUILD_ARG_CONTAINER_IMAGE_TAG      string = "container_tag"
)
