// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//   http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package v1alpha08

import (
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api"
)

const (
	// SonataFlowClusterPlatformKind is the Kind name of the SonataFlowClusterPlatform CR
	SonataFlowClusterPlatformKind string = "SonataFlowClusterPlatform"
	PlatformNotFoundReason        string = "PlatformNotFound"
)

// SonataFlowClusterPlatformSpec defines the desired state of SonataFlowClusterPlatform
type SonataFlowClusterPlatformSpec struct {
	// PlatformRef defines which existing SonataFlowPlatform's supporting services should be used cluster-wide.
	//+operator-sdk:csv:customresourcedefinitions:type=spec,displayName="PlatformRef"
	PlatformRef SonataFlowPlatformRef `json:"platformRef"`
	// Capabilities defines which platform capabilities should be applied cluster-wide. If nil, defaults to `capabilities.workflows["services"]`
	//+operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Capabilities"
	Capabilities *SonataFlowClusterPlatformCapSpec `json:"capabilities,omitempty"`
}

// SonataFlowClusterPlatformCapSpec defines which platform capabilities should be applied cluster-wide
type SonataFlowClusterPlatformCapSpec struct {
	// Workflows defines which platform capabilities should be applied to workflows cluster-wide.
	Workflows []WorkFlowCapability `json:"workflows,omitempty"`
}

// +kubebuilder:validation:Enum=services
type WorkFlowCapability string

// SonataFlowPlatformRef defines which existing SonataFlowPlatform's supporting services should be used cluster-wide.
type SonataFlowPlatformRef struct {
	// Name of the SonataFlowPlatform
	//+operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Platform_Name"
	Name string `json:"name"`
	// Namespace of the SonataFlowPlatform
	//+operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Platform_NS"
	Namespace string `json:"namespace"`
}

// SonataFlowClusterPlatformStatus defines the observed state of SonataFlowClusterPlatform
type SonataFlowClusterPlatformStatus struct {
	api.Status `json:",inline"`
	// Version the operator version controlling this ClusterPlatform
	//+operator-sdk:csv:customresourcedefinitions:type=status,displayName="version"
	Version string `json:"version,omitempty"`
}

func (in *SonataFlowClusterPlatformStatus) GetTopLevelConditionType() api.ConditionType {
	return api.SucceedConditionType
}

func (in *SonataFlowClusterPlatformStatus) IsReady() bool {
	return in.GetTopLevelCondition().IsTrue()
}

func (in *SonataFlowClusterPlatformStatus) GetTopLevelCondition() *api.Condition {
	return in.GetCondition(in.GetTopLevelConditionType())
}

func (in *SonataFlowClusterPlatformStatus) Manager() api.ConditionsManager {
	return api.NewConditionManager(in, api.SucceedConditionType)
}

func (in *SonataFlowClusterPlatformStatus) IsDuplicated() bool {
	cond := in.GetTopLevelCondition()
	return cond.IsFalse() && cond.Reason == PlatformDuplicatedReason
}

// SonataFlowClusterPlatform is the Schema for the sonataflowclusterplatforms API
// +kubebuilder:object:root=true
// +kubebuilder:subresource:status
// +kubebuilder:resource:scope=Cluster
// +kubebuilder:printcolumn:name="Platform_Name",type=string,JSONPath=`.spec.platformRef.name`
// +kubebuilder:printcolumn:name="Platform_NS",type=string,JSONPath=`.spec.platformRef.namespace`
// +kubebuilder:printcolumn:name="Ready",type=string,JSONPath=`.status.conditions[?(@.type=='Succeed')].status`
// +kubebuilder:printcolumn:name="Reason",type=string,JSONPath=`.status.conditions[?(@.type=='Succeed')].reason`
// +operator-sdk:csv:customresourcedefinitions:resources={{SonataFlowPlatform,sonataflow.org/v1alpha08,"A SonataFlow Platform"}}
// +operator-sdk:csv:customresourcedefinitions:displayName="SonataFlowClusterPlatform"
type SonataFlowClusterPlatform struct {
	metav1.TypeMeta   `json:",inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`

	Spec   SonataFlowClusterPlatformSpec   `json:"spec,omitempty"`
	Status SonataFlowClusterPlatformStatus `json:"status,omitempty"`
}

//+kubebuilder:object:root=true

// SonataFlowClusterPlatformList contains a list of SonataFlowClusterPlatform
type SonataFlowClusterPlatformList struct {
	metav1.TypeMeta `json:",inline"`
	metav1.ListMeta `json:"metadata,omitempty"`
	Items           []SonataFlowClusterPlatform `json:"items"`
}

func init() {
	SchemeBuilder.Register(&SonataFlowClusterPlatform{}, &SonataFlowClusterPlatformList{})
}
