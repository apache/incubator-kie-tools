// Copyright 2023 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package v1alpha08

import (
	"strconv"

	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// ConfigurationSpecType is used to define the enum values of the supported types for ConfigurationSpec
type ConfigurationSpecType string

const (
	// PropertyConfigurationSpec ...
	PropertyConfigurationSpec ConfigurationSpecType = "property"
	// ConfigMapConfigurationSpec ...
	ConfigMapConfigurationSpec ConfigurationSpecType = "configmap"
	// SecretConfigurationSpec ...
	SecretConfigurationSpec ConfigurationSpecType = "secret"
)

// ConfigurationSpec represents a generic configuration specification
type ConfigurationSpec struct {
	// Type represents the type of configuration, ie: property, configmap, secret, ...
	Type ConfigurationSpecType `json:"type"`
	// Value a reference to the object for this configuration (syntax may vary depending on the `Type`)
	Value corev1.ObjectReference `json:"value"`
}

const (
	// SonataFlowPlatformKind is the Kind name of the SonataFlowPlatform CR
	SonataFlowPlatformKind string = "SonataFlowPlatform"
)

// PlatformCluster is the kind of orchestration cluster the platform is installed into
// +kubebuilder:validation:Enum=kubernetes;openshift
type PlatformCluster string

const (
	// PlatformClusterOpenShift is used when targeting an OpenShift cluster
	PlatformClusterOpenShift PlatformCluster = "openshift"
	// PlatformClusterKubernetes is used when targeting a Kubernetes cluster
	PlatformClusterKubernetes PlatformCluster = "kubernetes"
)

// RegistrySpec provides the configuration for the container registry
type RegistrySpec struct {
	// if the container registry is insecure (ie, http only)
	Insecure bool `json:"insecure,omitempty"`
	// the URI to access
	Address string `json:"address,omitempty"`
	// the secret where credentials are stored
	Secret string `json:"secret,omitempty"`
	// the configmap which stores the Certificate Authority
	CA string `json:"ca,omitempty"`
	// the registry organization
	Organization string `json:"organization,omitempty"`
}

type BuildStrategy string

const (
	// OperatorBuildStrategy uses the operator builder to perform the workflow build
	// E.g. on Minikube or Kubernetes the container-builder strategies
	OperatorBuildStrategy BuildStrategy = "operator"
	// PlatformBuildStrategy uses the cluster to perform the build.
	// E.g. on OpenShift, BuildConfig.
	PlatformBuildStrategy BuildStrategy = "platform"

	// In the future we can have "custom" which will delegate the build to an external actor provided by the administrator
	// See https://issues.redhat.com/browse/KOGITO-9084
)

type BuildPlatformTemplate struct {
	// a base image that can be used as base layer for all images.
	// It can be useful if you want to provide some custom base image with further utility software
	BaseImage string `json:"baseImage,omitempty"`
	// how much time to wait before time out the build process
	Timeout *metav1.Duration `json:"timeout,omitempty"`
	// BuildStrategy to use to build workflows in the platform.
	// Usually, the operator elect the strategy based on the platform.
	// Note that this field might be read only in certain scenarios.
	BuildStrategy BuildStrategy `json:"buildStrategy,omitempty"`
	// TODO: add a link to the documentation where the user can find more info about this field
	// BuildStrategyOptions additional options to add to the build strategy.
	BuildStrategyOptions map[string]string `json:"buildStrategyOptions,omitempty"`
	// Registry the registry where to publish the built image
	Registry RegistrySpec `json:"registry,omitempty"`
}

// GetTimeout returns the specified duration or a default one
func (b *BuildPlatformTemplate) GetTimeout() metav1.Duration {
	if b.Timeout == nil {
		return metav1.Duration{}
	}
	return *b.Timeout
}

// IsOptionEnabled return whether the BuildStrategyOptions is enabled or not
func (b *BuildPlatformTemplate) IsOptionEnabled(option string) bool {
	if enabled, ok := b.BuildStrategyOptions[option]; ok {
		res, err := strconv.ParseBool(enabled)
		if err != nil {
			return false
		}
		return res
	}
	return false
}

func (b *BuildPlatformTemplate) IsOptionEmpty(option string) bool {
	if v, ok := b.BuildStrategyOptions[option]; ok {
		return len(v) == 0
	}
	return false
}

// SonataFlowPlatformSpec defines the desired state of SonataFlowPlatform
type SonataFlowPlatformSpec struct {
	// BuildTemplate specify how to build the Workflow. It's used as a template for the SonataFlowBuild
	BuildTemplate BuildTemplate `json:"build,omitempty"`
	// BuildPlatform specify how is the platform where we want to build the Workflow
	BuildPlatform BuildPlatformTemplate `json:"platform,omitempty"`
	// Configuration list of configuration properties to be attached to all the Workflow built from this Platform
	Configuration ConfigurationSpec `json:"configuration,omitempty"`
	// DevBaseImage Base image to run the Workflow in dev mode instead of the operator's default.
	// Optional, used for the dev profile only
	DevBaseImage string `json:"devBaseImage,omitempty"`
}

// PlatformPhase is the phase of a Platform
type PlatformPhase string

const (
	// PlatformPhaseNone when the SonataFlowPlatform does not exist
	PlatformPhaseNone PlatformPhase = ""
	// PlatformPhaseCreating when the SonataFlowPlatform is under creation process
	PlatformPhaseCreating PlatformPhase = "Creating"
	// PlatformPhaseWarming when the SonataFlowPlatform is warming (ie, creating Kaniko cache)
	PlatformPhaseWarming PlatformPhase = "Warming"
	// PlatformPhaseReady when the SonataFlowPlatform is ready
	PlatformPhaseReady PlatformPhase = "Ready"
	// PlatformPhaseError when the SonataFlowPlatform had some error (see Conditions)
	PlatformPhaseError PlatformPhase = "Error"
	// PlatformPhaseDuplicate when the SonataFlowPlatform is duplicated
	PlatformPhaseDuplicate PlatformPhase = "Duplicate"
)

// PlatformConditionType defines the type of condition
type PlatformConditionType string

// PlatformCondition describes the state of a resource at a certain point.
type PlatformCondition struct {
	// TODO: the Type can't be Kubernetes or OpenShift, but the actual condition like "Ready". See the Conditions implementation in the workflow.
	// TODO: also, we already have the `Cluster` field for that matter.
	// TODO: see https://issues.redhat.com/browse/KOGITO-9218

	// Type of platform condition (i.e. Kubernetes, OpenShift).
	Type PlatformConditionType `json:"type"`
	// Status of the condition, one of True, False, Unknown.
	Status corev1.ConditionStatus `json:"status"`
	// The last time this condition was updated.
	LastUpdateTime metav1.Time `json:"lastUpdateTime,omitempty"`
	// Last time the condition transitioned from one status to another.
	LastTransitionTime metav1.Time `json:"lastTransitionTime,omitempty"`
	// The reason for the condition's last transition.
	Reason string `json:"reason,omitempty"`
	// A human-readable message indicating details about the transition.
	Message string `json:"message,omitempty"`
}

// SonataFlowPlatformStatus defines the observed state of SonataFlowPlatform
type SonataFlowPlatformStatus struct {
	// Cluster what kind of cluster you're running (ie, plain Kubernetes or OpenShift)
	Cluster PlatformCluster `json:"cluster,omitempty"`
	// ObservedGeneration is the most recent generation observed for this Platform.
	ObservedGeneration int64 `json:"observedGeneration,omitempty"`
	// Phase defines in what phase the Platform is found
	Phase PlatformPhase `json:"phase,omitempty"`
	// Conditions which are the conditions met (particularly useful when in ERROR phase)
	Conditions []PlatformCondition `json:"conditions,omitempty"`
	// Version the operator version controlling this Platform
	Version string `json:"version,omitempty"`
	// Info generic information related to the build
	Info map[string]string `json:"info,omitempty"`
}

// SonataFlowPlatform is the Schema for the sonataflowplatforms API
// +k8s:deepcopy-gen:interfaces=k8s.io/apimachinery/pkg/runtime.Object
// +kubebuilder:object:root=true
// +kubebuilder:object:generate=true
// +kubebuilder:subresource:status
// +kubebuilder:resource:shortName={"sfp", "sfplatform", "sfplatforms"}
// +kubebuilder:printcolumn:name="Cluster",type=string,JSONPath=`.status.cluster`
// +kubebuilder:printcolumn:name="Phase",type=string,JSONPath=`.status.phase`
// +kubebuilder:printcolumn:name="Ready",type=string,JSONPath=`.status.phase=='Ready'`
type SonataFlowPlatform struct {
	metav1.TypeMeta   `json:",inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`

	Spec   SonataFlowPlatformSpec   `json:"spec,omitempty"`
	Status SonataFlowPlatformStatus `json:"status,omitempty"`
}

// SonataFlowPlatformList contains a list of SonataFlowPlatform
// +k8s:deepcopy-gen:interfaces=k8s.io/apimachinery/pkg/runtime.Object
// +kubebuilder:object:root=true
// +kubebuilder:object:generate=true
type SonataFlowPlatformList struct {
	metav1.TypeMeta `json:",inline"`
	metav1.ListMeta `json:"metadata,omitempty"`
	Items           []SonataFlowPlatform `json:"items"`
}

func init() {
	SchemeBuilder.Register(&SonataFlowPlatform{}, &SonataFlowPlatformList{})
}
