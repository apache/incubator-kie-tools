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

// Dependency represents a Maven dependency.
type Dependency struct {
	GroupId    string
	ArtifactId string
	Version    string
	Type       string
	Scope      string
}

var KogitoBomDependency = Dependency{
	GroupId:    "org.kie.kogito",
	ArtifactId: "kogito-bom",
	Version:    KogitoVersion,
	Type:       "pom",
	Scope:      "import",
}

// KogitoDependencies defines the set of dependencies to be added to the pom.xml
// of created and converted Quarkus projects.
var KogitoDependencies = []Dependency{
	{GroupId: "org.kie", ArtifactId: "kie-addons-quarkus-knative-eventing"},
	{GroupId: "org.kie", ArtifactId: "kie-addons-quarkus-process-management"},
	{GroupId: "org.kie", ArtifactId: "kie-addons-quarkus-source-files"},
	{GroupId: "org.kie", ArtifactId: "kogito-addons-quarkus-data-index-inmemory"},
	{GroupId: "org.kie", ArtifactId: "kogito-addons-quarkus-jobs-service-embedded"},
	{GroupId: "org.apache.kie.sonataflow", ArtifactId: "sonataflow-quarkus"},
	{GroupId: "org.apache.kie.sonataflow", ArtifactId: "sonataflow-quarkus-devui", Version: "${kie.tooling.version}"},
}

// requared crds for sonataflow
var SonataflowCRDs = []string{"sonataflows.sonataflow.org", "sonataflowbuilds.sonataflow.org", "sonataflowplatforms.sonataflow.org"}
var KnativeCoreServingCRDs = []string{"images.caching.internal.knative.dev", "certificates.networking.internal.knative.dev", "configurations.serving.knative.dev", "clusterdomainclaims.networking.internal.knative.dev", "domainmappings.serving.knative.dev", "ingresses.networking.internal.knative.dev", "metrics.autoscaling.internal.knative.dev", "podautoscalers.autoscaling.internal.knative.dev", "revisions.serving.knative.dev", "routes.serving.knative.dev", "services.serving.knative.dev", "serverlessservices.networking.internal.knative.dev"}

// OLM CatalogSources
var OLMCatalogSourcesMap = map[string]string{"operatorhubio-catalog": "olm", "community-operators": "openshift-marketplace"}

var SonataFlowOperatorName = "sonataflow-operator"


const (
	QuarkusMavenPlugin                          = "quarkus-maven-plugin"
	QuarkusKubernetesExtension                  = "quarkus-kubernetes"
	QuarkusResteasyJacksonExtension             = "quarkus-resteasy-jackson"
	QuarkusContainerImageJib                    = "quarkus-container-image-jib"
	SmallryeHealth                              = "smallrye-health"
	QuarkusContainerImageDocker                 = "quarkus-container-image-docker"
	KogitoQuarkusServerlessWorkflowExtension    = "sonataflow-quarkus"
	KogitoAddonsQuarkusKnativeEventingExtension = "kie-addons-quarkus-knative-eventing"
	KogitoQuarkusServerlessWorkflowDevUi        = "sonataflow-quarkus-devui"
	KogitoAddonsQuarkusSourceFiles              = "kie-addons-quarkus-source-files"
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
	ApplicationSecretProperties = "secret.properties"
	ManifestServiceFilesKind = "SonataFlow"

	DockerInternalPort = "8080/tcp"
	// VolumeBindPath The :z is to let docker know that the volume content can be shared between containers(SELinux)
	VolumeBindPathSELinux = "/home/kogito/serverless-workflow-project/src/main/resources:z"
	VolumeBindPath        = "/home/kogito/serverless-workflow-project/src/main/resources"

	DashboardsDefaultDirName = "dashboards"
)
