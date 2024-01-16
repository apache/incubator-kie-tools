/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package metadata

const (
	QuarkusMavenPlugin                          = "quarkus-maven-plugin"
	QuarkusKubernetesExtension                  = "quarkus-kubernetes"
	QuarkusResteasyJacksonExtension             = "quarkus-resteasy-jackson"
	QuarkusContainerImageJib                    = "quarkus-container-image-jib"
	SmallryeHealth                              = "smallrye-health"
	QuarkusContainerImageDocker                 = "quarkus-container-image-docker"
	KogitoQuarkusServerlessWorkflowExtension    = "kogito-quarkus-serverless-workflow"
	KogitoAddonsQuarkusKnativeEventingExtension = "kogito-addons-quarkus-knative-eventing"
	KogitoQuarkusServerlessWorkflowDevUi        = "kogito-quarkus-serverless-workflow-devui"
	KogitoAddonsQuarkusSourceFiles              = "kogito-addons-quarkus-source-files"
	KogitoDataIndexInMemory                     = "kogito-addons-quarkus-data-index-inmemory"

	JavaVersion       = 11
	MavenMajorVersion = 3
	MavenMinorVersion = 8

	DefaultTag     = "latest"
	WorkflowSwJson = "workflow.sw.json"
	WorkflowSwYaml = "workflow.sw.yaml"

	OperatorName       = "sonataflow-operator-system"
	OperatorManagerPod = "sonataflow-operator-controller-manager"

	YAMLExtension         = ".yaml"
	YMLExtension          = ".yml"
	JSONExtension         = ".json"
	YAMLSWExtension       = "sw.yaml"
	YMLSWExtension        = "sw.yml"
	JSONSWExtension       = "sw.json"
	ApplicationProperties = "application.properties"

	ManifestServiceFilesKind = "SonataFlow"

	DockerInternalPort = "8080/tcp"
	// VolumeBindPath The :z is to let docker know that the volume content can be shared between containers(SELinux)
	VolumeBindPath = "/home/kogito/serverless-workflow-project/src/main/resources:z"

	DashboardsDefaultDirName = "dashboards"
)
