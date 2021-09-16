// Copyright 2020 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package v1beta1

import (
	"github.com/kiegroup/kogito-operator/apis"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/resource"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// KogitoBuildSpec defines the desired state of KogitoBuild.
type KogitoBuildSpec struct {

	// Sets the type of build that this instance will handle:
	//
	// Binary - takes an uploaded binary file already compiled and creates a Kogito service image from it.
	//
	// RemoteSource - pulls the source code from a Git repository, builds the binary and then the final Kogito service image.
	//
	// LocalSource - takes an uploaded resource file such as DRL (rules), DMN (decision) or BPMN (process), builds the binary and the final Kogito service image.
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Build Type"
	// +kubebuilder:validation:Enum=Binary;RemoteSource;LocalSource
	Type api.KogitoBuildType `json:"type"`

	// DisableIncremental indicates that source to image builds should NOT be incremental. Defaults to false.
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Disable Incremental Builds"
	// +operator-sdk:csv:customresourcedefinitions:type=spec,xDescriptors="urn:alm:descriptor:com.tectonic.ui:booleanSwitch"
	// +optional
	DisableIncremental bool `json:"disableIncremental,omitempty"`

	// Environment variables used during build time.
	// +listType=atomic
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Build Env Variables"
	// +optional
	Env []corev1.EnvVar `json:"env,omitempty"`

	// Information about the git repository where the Kogito Service source code resides.
	//
	// Ignored for binary builds.
	// +optional
	GitSource GitSource `json:"gitSource,omitempty"`

	// Which runtime Kogito service base image to use when building the Kogito service.
	// If "BuildImage" is set, this value is ignored by the operator.
	// Default value: quarkus.
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Runtime"
	// +optional
	// +kubebuilder:validation:Enum=quarkus;springboot
	Runtime api.RuntimeType `json:"runtime,omitempty"`

	// WebHooks secrets for source to image builds based on Git repositories (Remote Sources).
	// +listType=atomic
	// +optional
	WebHooks []WebHookSecret `json:"webHooks,omitempty"`

	// Native indicates if the Kogito Service built should be compiled to run on native mode when Runtime is Quarkus (Source to Image build only).
	//
	// For more information, see https://www.graalvm.org/docs/reference-manual/aot-compilation/.
	// +optional
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Native Build"
	// +operator-sdk:csv:customresourcedefinitions:type=spec,xDescriptors="urn:alm:descriptor:com.tectonic.ui:booleanSwitch"
	Native bool `json:"native,omitempty"`

	// Resources Requirements for builder pods.
	// +optional
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	// +operator-sdk:csv:customresourcedefinitions:type=spec,xDescriptors="urn:alm:descriptor:com.tectonic.ui:resourceRequirements"
	Resources corev1.ResourceRequirements `json:"resources,omitempty"`

	// Maven Mirror URL to be used during source-to-image builds (Local and Remote) to considerably increase build speed.
	// +optional
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Maven Mirror URL"
	// +operator-sdk:csv:customresourcedefinitions:type=spec,xDescriptors="urn:alm:descriptor:com.tectonic.ui:text"
	MavenMirrorURL string `json:"mavenMirrorURL,omitempty"`

	// Image used to build the Kogito Service from source (Local and Remote).
	//
	// If not defined the operator will use image provided by the Kogito Team based on the "Runtime" field.
	//
	// Example: "quay.io/kiegroup/kogito-jvm-builder:latest".
	//
	// On OpenShift an ImageStream will be created in the current namespace pointing to the given image.
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Build Image"
	// +operator-sdk:csv:customresourcedefinitions:type=spec,xDescriptors="urn:alm:descriptor:com.tectonic.ui:text"
	// +optional
	BuildImage string `json:"buildImage,omitempty"`

	// Image used as the base image for the final Kogito service. This image only has the required packages to run the application.
	//
	// For example: quarkus based services will have only JVM installed, native services only the packages required by the OS.
	//
	// If not defined the operator will use image provided by the Kogito Team based on the "Runtime" field.
	//
	// Example: "quay.io/kiegroup/kogito-jvm-builder:latest".
	//
	// On OpenShift an ImageStream will be created in the current namespace pointing to the given image.
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Base Image"
	// +operator-sdk:csv:customresourcedefinitions:type=spec,xDescriptors="urn:alm:descriptor:com.tectonic.ui:text"
	// +optional
	RuntimeImage string `json:"runtimeImage,omitempty"`

	// Set this field targeting the desired KogitoRuntime when this KogitoBuild instance has a different name than the KogitoRuntime.
	//
	// By default this KogitoBuild instance will generate a final image named after its own name (.metadata.name).
	//
	// On OpenShift, an ImageStream will be created causing a redeployment on any KogitoRuntime with the same name.
	// On Kubernetes, the final image will be pushed to the KogitoRuntime deployment.
	//
	// If you have multiple KogitoBuild instances (let's say BinaryBuildType and Remote Source), you might need that both target the same KogitoRuntime.
	// Both KogitoBuilds will update the same ImageStream or generate a final image to the same KogitoRuntime deployment.
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Target kogito Runtime"
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	// +optional
	TargetKogitoRuntime string `json:"targetKogitoRuntime,omitempty"`

	// Artifact contains override information for building the Maven artifact (used for Local Source builds).
	//
	// You might want to override this information when building from decisions, rules or process files.
	// In this scenario the Kogito Images will generate a new Java project for you underneath.
	// This information will be used to generate this project.
	// +optional
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Final Artifact"
	Artifact Artifact `json:"artifact,omitempty"`

	// If set to true will print the logs for downloading/uploading of maven dependencies. Defaults to false.
	// +optional
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Enable Maven Download Output"
	// +operator-sdk:csv:customresourcedefinitions:type=spec,xDescriptors="urn:alm:descriptor:com.tectonic.ui:booleanSwitch"
	EnableMavenDownloadOutput bool `json:"enableMavenDownloadOutput,omitempty"`
}

// AddResourceRequest adds new resource request. Works also on an uninitialized Requests field.
func (k *KogitoBuildSpec) AddResourceRequest(name, value string) {
	if k.Resources.Requests == nil {
		k.Resources.Requests = corev1.ResourceList{}
	}

	k.Resources.Requests[corev1.ResourceName(name)] = resource.MustParse(value)
}

// AddResourceLimit adds new resource limit. Works also on an uninitialized Limits field.
func (k *KogitoBuildSpec) AddResourceLimit(name, value string) {
	if k.Resources.Limits == nil {
		k.Resources.Limits = corev1.ResourceList{}
	}

	k.Resources.Limits[corev1.ResourceName(name)] = resource.MustParse(value)
}

// GetType ...
func (k *KogitoBuildSpec) GetType() api.KogitoBuildType {
	return k.Type
}

// SetType ...
func (k *KogitoBuildSpec) SetType(buildType api.KogitoBuildType) {
	k.Type = buildType
}

// IsDisableIncremental ...
func (k *KogitoBuildSpec) IsDisableIncremental() bool {
	return k.DisableIncremental
}

// SetDisableIncremental ...
func (k *KogitoBuildSpec) SetDisableIncremental(disableIncremental bool) {
	k.DisableIncremental = disableIncremental
}

// GetEnv ...
func (k *KogitoBuildSpec) GetEnv() []corev1.EnvVar {
	return k.Env
}

// SetEnv ...
func (k *KogitoBuildSpec) SetEnv(env []corev1.EnvVar) {
	k.Env = env
}

// GetGitSource ...
func (k *KogitoBuildSpec) GetGitSource() api.GitSourceInterface {
	return &k.GitSource
}

// SetGitSource ...
func (k *KogitoBuildSpec) SetGitSource(gitSource api.GitSourceInterface) {
	if newGitSource, ok := gitSource.(*GitSource); ok {
		k.GitSource = *newGitSource
	}
}

// GetRuntime ...
func (k *KogitoBuildSpec) GetRuntime() api.RuntimeType {
	return k.Runtime
}

// SetRuntime ...
func (k *KogitoBuildSpec) SetRuntime(runtime api.RuntimeType) {
	k.Runtime = runtime
}

// GetWebHooks ...
func (k *KogitoBuildSpec) GetWebHooks() []api.WebHookSecretInterface {
	webHooks := make([]api.WebHookSecretInterface, len(k.WebHooks))
	for i, v := range k.WebHooks {
		webHooks[i] = api.WebHookSecretInterface(v)
	}
	return webHooks
}

// SetWebHooks ...
func (k *KogitoBuildSpec) SetWebHooks(webhooks []api.WebHookSecretInterface) {
	var newWebHooks []WebHookSecret
	for _, webHook := range webhooks {
		if newWebHook, ok := webHook.(WebHookSecret); ok {
			newWebHooks = append(newWebHooks, newWebHook)
		}
	}
	k.WebHooks = newWebHooks
}

// IsNative ...
func (k *KogitoBuildSpec) IsNative() bool {
	return k.Native
}

// SetNative ...
func (k *KogitoBuildSpec) SetNative(native bool) {
	k.Native = native
}

// GetResources ...
func (k *KogitoBuildSpec) GetResources() corev1.ResourceRequirements {
	return k.Resources
}

// SetResources ...
func (k *KogitoBuildSpec) SetResources(resources corev1.ResourceRequirements) {
	k.Resources = resources
}

// GetMavenMirrorURL ...
func (k *KogitoBuildSpec) GetMavenMirrorURL() string {
	return k.MavenMirrorURL
}

// SetMavenMirrorURL ...
func (k *KogitoBuildSpec) SetMavenMirrorURL(mavenMirrorURL string) {
	k.MavenMirrorURL = mavenMirrorURL
}

// GetBuildImage ...
func (k *KogitoBuildSpec) GetBuildImage() string {
	return k.BuildImage
}

// SetBuildImage ...
func (k *KogitoBuildSpec) SetBuildImage(buildImage string) {
	k.BuildImage = buildImage
}

// GetRuntimeImage ...
func (k *KogitoBuildSpec) GetRuntimeImage() string {
	return k.RuntimeImage
}

// SetRuntimeImage ...
func (k *KogitoBuildSpec) SetRuntimeImage(runtime string) {
	k.RuntimeImage = runtime
}

// GetTargetKogitoRuntime ...
func (k *KogitoBuildSpec) GetTargetKogitoRuntime() string {
	return k.TargetKogitoRuntime
}

// SetTargetKogitoRuntime ....
func (k *KogitoBuildSpec) SetTargetKogitoRuntime(targetRuntime string) {
	k.TargetKogitoRuntime = targetRuntime
}

// GetArtifact ...
func (k *KogitoBuildSpec) GetArtifact() api.ArtifactInterface {
	return &k.Artifact
}

// SetArtifact ...
func (k *KogitoBuildSpec) SetArtifact(artifact api.ArtifactInterface) {
	if newArtifact, ok := artifact.(*Artifact); ok {
		k.Artifact = *newArtifact
	}
}

// IsEnableMavenDownloadOutput ...
func (k *KogitoBuildSpec) IsEnableMavenDownloadOutput() bool {
	return k.EnableMavenDownloadOutput
}

// SetEnableMavenDownloadOutput ...
func (k *KogitoBuildSpec) SetEnableMavenDownloadOutput(enableMavenDownloadOutput bool) {
	k.EnableMavenDownloadOutput = enableMavenDownloadOutput
}

// KogitoBuildStatus defines the observed state of KogitoBuild.
// +k8s:openapi-gen=true
type KogitoBuildStatus struct {
	// +listType=atomic
	// History of conditions for the resource
	// +operator-sdk:csv:customresourcedefinitions:type=status
	// +operator-sdk:csv:customresourcedefinitions:type=status,xDescriptors="urn:alm:descriptor:io.kubernetes.conditions"
	Conditions *[]metav1.Condition `json:"conditions"`
	// +operator-sdk:csv:customresourcedefinitions:type=status
	// +operator-sdk:csv:customresourcedefinitions:type=status,displayName="Latest Build"
	LatestBuild string `json:"latestBuild,omitempty"`
	// History of builds
	// +operator-sdk:csv:customresourcedefinitions:type=status
	// +operator-sdk:csv:customresourcedefinitions:type=status,displayName="Builds"
	Builds Builds `json:"builds"`
}

// GetConditions ...
func (k *KogitoBuildStatus) GetConditions() *[]metav1.Condition {
	return k.Conditions
}

// SetConditions ...
func (k *KogitoBuildStatus) SetConditions(conditions *[]metav1.Condition) {
	k.Conditions = conditions
}

// GetLatestBuild ...
func (k *KogitoBuildStatus) GetLatestBuild() string {
	return k.LatestBuild
}

// SetLatestBuild ...
func (k *KogitoBuildStatus) SetLatestBuild(latestBuild string) {
	k.LatestBuild = latestBuild
}

// GetBuilds ...
func (k *KogitoBuildStatus) GetBuilds() api.BuildsInterface {
	return &k.Builds
}

// SetBuilds ...
func (k *KogitoBuildStatus) SetBuilds(builds api.BuildsInterface) {
	if newBuilds, ok := builds.(*Builds); ok {
		k.Builds = *newBuilds
	}
}

// Builds ...
// +k8s:openapi-gen=true
type Builds struct {
	// Builds are being created.
	// +listType=set
	New []string `json:"new,omitempty"`
	// Builds are about to start running.
	// +listType=set
	Pending []string `json:"pending,omitempty"`
	// Builds are running.
	// +listType=set
	Running []string `json:"running,omitempty"`
	// Builds have executed and succeeded.
	// +listType=set
	Complete []string `json:"complete,omitempty"`
	// Builds have executed and failed.
	// +listType=set
	Failed []string `json:"failed,omitempty"`
	// Builds have been prevented from executing by an error.
	// +listType=set
	Error []string `json:"error,omitempty"`
	// Builds have been stopped from executing.
	// +listType=set
	Cancelled []string `json:"cancelled,omitempty"`
}

// GetNew ...
func (b *Builds) GetNew() []string {
	return b.New
}

// SetNew ...
func (b *Builds) SetNew(newBuilds []string) {
	b.New = newBuilds
}

// GetPending ...
func (b *Builds) GetPending() []string {
	return b.Pending
}

// SetPending ...
func (b *Builds) SetPending(pendingBuilds []string) {
	b.Pending = pendingBuilds
}

// GetRunning ...
func (b *Builds) GetRunning() []string {
	return b.Running
}

// SetRunning ...
func (b *Builds) SetRunning(runningBuilds []string) {
	b.Running = runningBuilds
}

// GetComplete ...
func (b *Builds) GetComplete() []string {
	return b.Complete
}

// SetComplete ...
func (b *Builds) SetComplete(completeBuilds []string) {
	b.Complete = completeBuilds
}

// GetFailed ...
func (b *Builds) GetFailed() []string {
	return b.Failed
}

// SetFailed ...
func (b *Builds) SetFailed(failedBuilds []string) {
	b.Failed = failedBuilds
}

// GetError ...
func (b *Builds) GetError() []string {
	return b.Error
}

// SetError ...
func (b *Builds) SetError(errorBuilds []string) {
	b.Error = errorBuilds
}

// GetCancelled ...
func (b *Builds) GetCancelled() []string {
	return b.Cancelled
}

// SetCancelled ...
func (b *Builds) SetCancelled(cancelled []string) {
	b.Cancelled = cancelled
}

// +kubebuilder:object:root=true
// +k8s:openapi-gen=true
// +genclient
// +kubebuilder:resource:path=kogitobuilds,scope=Namespaced
// +kubebuilder:subresource:status
// +kubebuilder:printcolumn:name="Type",type="string",JSONPath=".spec.type",description="Type of this build instance"
// +kubebuilder:printcolumn:name="Runtime",type="string",JSONPath=".spec.runtime",description="Runtime used to build the service"
// +kubebuilder:printcolumn:name="Native",type="boolean",JSONPath=".spec.native",description="Indicates it's a native build"
// +kubebuilder:printcolumn:name="Maven URL",type="string",JSONPath=".spec.mavenMirrorURL",description="URL for the proxy Maven repository"
// +kubebuilder:printcolumn:name="Kogito Runtime",type="string",JSONPath=".spec.targetKogitoRuntime",description="Target KogitoRuntime for this build"
// +kubebuilder:printcolumn:name="Git Repository",type="string",JSONPath=".spec.gitSource.uri",description="Git repository URL (RemoteSource builds only)"
// +operator-sdk:csv:customresourcedefinitions:resources={{ImageStream,image.openshift.io/v1," A Openshift Image Stream"}}
// +operator-sdk:csv:customresourcedefinitions:resources={{BuildConfig,build.openshift.io/v1," A Openshift Build Config"}}
// +operator-sdk:csv:customresourcedefinitions:displayName="Kogito Build"

// KogitoBuild handles how to build a custom Kogito service in a Kubernetes/OpenShift cluster.
type KogitoBuild struct {
	metav1.TypeMeta   `json:",inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`

	Spec   KogitoBuildSpec   `json:"spec,omitempty"`
	Status KogitoBuildStatus `json:"status,omitempty"`
}

// GetSpec provide spec of Kogito Build
func (k *KogitoBuild) GetSpec() api.KogitoBuildSpecInterface {
	return &k.Spec
}

// GetStatus provide status of Kogito Build
func (k *KogitoBuild) GetStatus() api.KogitoBuildStatusInterface {
	return &k.Status
}

// +kubebuilder:object:root=true

// KogitoBuildList contains a list of KogitoBuild.
type KogitoBuildList struct {
	metav1.TypeMeta `json:",inline"`
	metav1.ListMeta `json:"metadata,omitempty"`
	// +listType=atomic
	Items []KogitoBuild `json:"items"`
}

func init() {
	SchemeBuilder.Register(&KogitoBuild{}, &KogitoBuildList{})
}
