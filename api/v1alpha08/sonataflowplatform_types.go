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
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	"github.com/kiegroup/kogito-serverless-operator/api"
)

const (
	// SonataFlowPlatformKind is the Kind name of the SonataFlowPlatform CR
	SonataFlowPlatformKind string = "SonataFlowPlatform"
)

// SonataFlowPlatformSpec defines the desired state of SonataFlowPlatform
type SonataFlowPlatformSpec struct {
	// Attributes for building workflows in the target platform
	Build BuildPlatformSpec `json:"build,omitempty"`
	// Attributes for running workflows in devmode (immutable, no build required)
	DevMode DevModePlatformSpec `json:"devMode,omitempty"`
}

// PlatformCluster is the kind of orchestration cluster the platform is installed into
// +kubebuilder:validation:Enum=kubernetes;openshift
type PlatformCluster string

const (
	// PlatformClusterOpenShift is used when targeting an OpenShift cluster
	PlatformClusterOpenShift PlatformCluster = "openshift"
	// PlatformClusterKubernetes is used when targeting a Kubernetes cluster
	PlatformClusterKubernetes PlatformCluster = "kubernetes"
)

const (
	PlatformCreatingReason   = "Creating"
	PlatformWarmingReason    = "Warming"
	PlatformFailureReason    = "Failure"
	PlatformDuplicatedReason = "Duplicated"
)

// SonataFlowPlatformStatus defines the observed state of SonataFlowPlatform
type SonataFlowPlatformStatus struct {
	api.Status `json:",inline"`
	// Cluster what kind of cluster you're running (ie, plain Kubernetes or OpenShift)
	Cluster PlatformCluster `json:"cluster,omitempty"`
	// Version the operator version controlling this Platform
	Version string `json:"version,omitempty"`
	// Info generic information related to the build
	Info map[string]string `json:"info,omitempty"`
}

func (in *SonataFlowPlatformStatus) GetTopLevelConditionType() api.ConditionType {
	return api.SucceedConditionType
}

func (in *SonataFlowPlatformStatus) IsReady() bool {
	return in.GetTopLevelCondition().IsTrue()
}

func (in *SonataFlowPlatformStatus) GetTopLevelCondition() *api.Condition {
	return in.GetCondition(in.GetTopLevelConditionType())
}

func (in *SonataFlowPlatformStatus) Manager() api.ConditionsManager {
	return api.NewConditionManager(in, api.SucceedConditionType)
}

func (in *SonataFlowPlatformStatus) IsCreating() bool {
	cond := in.GetTopLevelCondition()
	return cond.IsFalse() && cond.Reason == PlatformCreatingReason
}

func (in *SonataFlowPlatformStatus) IsWarming() bool {
	cond := in.GetTopLevelCondition()
	return cond.IsFalse() && cond.Reason == PlatformWarmingReason
}

func (in *SonataFlowPlatformStatus) IsDuplicated() bool {
	cond := in.GetTopLevelCondition()
	return cond.IsFalse() && cond.Reason == PlatformDuplicatedReason
}

func (in *SonataFlowPlatformStatus) IsFailure() bool {
	cond := in.GetTopLevelCondition()
	return cond.IsFalse() && cond.Reason == PlatformFailureReason
}

// SonataFlowPlatform is the descriptor for the workflow platform infrastructure.
// +k8s:deepcopy-gen:interfaces=k8s.io/apimachinery/pkg/runtime.Object
// +kubebuilder:object:root=true
// +kubebuilder:object:generate=true
// +kubebuilder:subresource:status
// +kubebuilder:resource:shortName={"sfp", "sfplatform", "sfplatforms"}
// +kubebuilder:printcolumn:name="Cluster",type=string,JSONPath=`.status.cluster`
// +kubebuilder:printcolumn:name="Ready",type=string,JSONPath=`.status.conditions[?(@.type=='Succeed')].status`
// +kubebuilder:printcolumn:name="Reason",type=string,JSONPath=`.status.conditions[?(@.type=='Succeed')].reason`
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
