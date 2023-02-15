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
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	"github.com/kiegroup/container-builder/api"
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
	// KogitoServerlessPlatformKind is the Kind name of the KogitoServerlessPlatform CR
	KogitoServerlessPlatformKind string = "KogitoServerlessPlatform"
)

// PlatformCluster is the kind of orchestration cluster the platform is installed into
type PlatformCluster string

const (
	// PlatformClusterOpenShift is used when targeting an OpenShift cluster
	PlatformClusterOpenShift PlatformCluster = "OpenShift"
	// PlatformClusterKubernetes is used when targeting a Kubernetes cluster
	PlatformClusterKubernetes PlatformCluster = "Kubernetes"
)

// PlatformBuildStrategy specifies how the Build should be executed.
// It will trigger a Kaniko pod that will take care of producing the expected image containing the Quarkus/Kogito runtime.
// +kubebuilder:validation:Enum=pod
type PlatformBuildStrategy string

const (
	// BuildStrategyPod performs the build in a `Pod` (will schedule a new builder ephemeral `Pod` which will take care of the build action).
	BuildStrategyPod PlatformBuildStrategy = "pod"
)

// PlatformBuildStrategies is a list of strategies allowed for the build
var PlatformBuildStrategies = []PlatformBuildStrategy{
	BuildStrategyPod,
}

// BuildPublishStrategies the list of all available publish strategies
var BuildPublishStrategies = []api.PlatformBuildPublishStrategy{
	api.PlatformBuildPublishStrategyKaniko,
}

// KogitoServerlessPlatformSpec defines the desired state of KogitoServerlessPlatform
type KogitoServerlessPlatformSpec struct {
	// Cluster what kind of cluster you're running (ie, plain Kubernetes or OpenShift)
	Cluster PlatformCluster `json:"cluster,omitempty"`
	// Build specify how to build the Workflow
	Build api.BuildSpec `json:"build,omitempty"`
	// BuildPlatform specify how is the platform where we want to build the Workflow
	BuildPlatform api.PlatformBuildSpec `json:"platform,omitempty"`
	// Configuration list of configuration properties to be attached to all the Workflow built from this Platform
	Configuration ConfigurationSpec `json:"configuration,omitempty"`
}

// PlatformPhase is the phase of a Platform
type PlatformPhase string

const (
	// PlatformPhaseNone when the KogitoServerlessPlatform does not exist
	PlatformPhaseNone PlatformPhase = ""
	// PlatformPhaseCreating when the KogitoServerlessPlatform is under creation process
	PlatformPhaseCreating PlatformPhase = "Creating"
	// PlatformPhaseWarming when the KogitoServerlessPlatform is warming (ie, creating Kaniko cache)
	PlatformPhaseWarming PlatformPhase = "Warming"
	// PlatformPhaseReady when the KogitoServerlessPlatform is ready
	PlatformPhaseReady PlatformPhase = "Ready"
	// PlatformPhaseError when the KogitoServerlessPlatform had some error (see Conditions)
	PlatformPhaseError PlatformPhase = "Error"
	// PlatformPhaseDuplicate when the KogitoServerlessPlatform is duplicated
	PlatformPhaseDuplicate PlatformPhase = "Duplicate"
)

// PlatformConditionType defines the type of condition
type PlatformConditionType string

// PlatformCondition describes the state of a resource at a certain point.
type PlatformCondition struct {
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

// KogitoServerlessPlatformStatus defines the observed state of KogitoServerlessPlatform
type KogitoServerlessPlatformStatus struct {
	KogitoServerlessPlatformSpec `json:",inline"`
	// ObservedGeneration is the most recent generation observed for this Platform.
	ObservedGeneration int64 `json:"observedGeneration,omitempty"`
	// Phase defines in what phase the Platform is found
	Phase PlatformPhase `json:"phase,omitempty"`
	// Conditions which are the conditions met (particularly useful when in ERROR phase)
	Conditions []PlatformCondition `json:"conditions,omitempty"`
	// Version the Kogito Serverless operator version controlling this Platform
	Version string `json:"version,omitempty"`
	// Info generic information related to the build of Kogito Serverless operator
	Info map[string]string `json:"info,omitempty"`
}

// +k8s:deepcopy-gen:interfaces=k8s.io/apimachinery/pkg/runtime.Object
// +kubebuilder:object:root=true
// +kubebuilder:object:generate=true
// +kubebuilder:subresource:status
// KogitoServerlessPlatform is the Schema for the kogitoserverlessplatforms API
type KogitoServerlessPlatform struct {
	metav1.TypeMeta   `json:",inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`

	Spec   KogitoServerlessPlatformSpec   `json:"spec,omitempty"`
	Status KogitoServerlessPlatformStatus `json:"status,omitempty"`
}

// KogitoServerlessPlatformList contains a list of KogitoServerlessPlatform
// +k8s:deepcopy-gen:interfaces=k8s.io/apimachinery/pkg/runtime.Object
// +kubebuilder:object:root=true
// +kubebuilder:object:generate=true
type KogitoServerlessPlatformList struct {
	metav1.TypeMeta `json:",inline"`
	metav1.ListMeta `json:"metadata,omitempty"`
	Items           []KogitoServerlessPlatform `json:"items"`
}

func init() {
	SchemeBuilder.Register(&KogitoServerlessPlatform{}, &KogitoServerlessPlatformList{})
}
