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
	DEFAULT_TAG = "latest"

	// Filenames
	WORKFLOW_CONFIG_YML = "workflow.config.yml"
	WORKFLOW_SW_JSON    = "workflow.sw.json"
)
