/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package v1alpha08

import (
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	"github.com/apache/incubator-kie-kogito-serverless-operator/api"
)

const (
	// SonataFlowPlatformKind is the Kind name of the SonataFlowPlatform CR
	SonataFlowPlatformKind string = "SonataFlowPlatform"
)

// SonataFlowPlatformSpec defines the desired state of SonataFlowPlatform
// +k8s:openapi-gen=true
type SonataFlowPlatformSpec struct {
	// Build Attributes for building workflows in the target platform
	// +optional
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Build"
	Build BuildPlatformSpec `json:"build,omitempty"`
	// DevMode Attributes for running workflows in devmode (immutable, no build required)
	// +optional
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="DevMode"
	DevMode DevModePlatformSpec `json:"devMode,omitempty"`
	// Services attributes for deploying supporting applications like Data Index.
	// Only workflows with the proper annotation will be configured to use these service(s).
	// `sonataflow.org/profile: prod`
	// +optional
	Services ServicesPlatformSpec `json:"services,omitempty"`
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
// +k8s:openapi-gen=true
type SonataFlowPlatformStatus struct {
	api.Status `json:",inline"`
	// Cluster what kind of cluster you're running (ie, plain Kubernetes or OpenShift)
	//+operator-sdk:csv:customresourcedefinitions:type=status,displayName="cluster"
	Cluster PlatformCluster `json:"cluster,omitempty"`
	// Version the operator version controlling this Platform
	//+operator-sdk:csv:customresourcedefinitions:type=status,displayName="version"
	Version string `json:"version,omitempty"`
	// Info generic information related to the build
	//+operator-sdk:csv:customresourcedefinitions:type=status,displayName="info"
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
// +operator-sdk:csv:customresourcedefinitions:resources={{Namespace,v1,"The Namespace controlled by the platform"}}
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
