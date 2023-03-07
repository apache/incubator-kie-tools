// Copyright 2022 Red Hat, Inc. and/or its affiliates
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
	v1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	duckv1 "knative.dev/pkg/apis/duck/v1"

	"github.com/kiegroup/kogito-serverless-operator/api"
)

// KogitoServerlessWorkflowSpec defines the desired state of KogitoServerlessWorkflow
type KogitoServerlessWorkflowSpec struct {
	Constants   []Constant   `json:"conditions,omitempty"`
	Secrets     *[]v1.Secret `json:"secrets,omitempty"`
	Start       string       `json:"start"`
	Timeouts    []Timeout    `json:"timeouts,omitempty"`
	Errors      []Error      `json:"errors,omitempty"`
	KeepActive  bool         `json:"keepActive,omitempty"`
	Auth        Auth         `json:"auth,omitempty"`
	Events      *[]Event     `json:"events,omitempty"`
	Functions   []Function   `json:"functions,omitempty"`
	AutoRetries bool         `json:"autoRetries,omitempty"`
	Retries     Retry        `json:"retries,omitempty"`
	States      []State      `json:"states"`
}

var _ api.ConditionsReader = &KogitoServerlessWorkflowStatus{}

// KogitoServerlessWorkflowStatus defines the observed state of KogitoServerlessWorkflow
type KogitoServerlessWorkflowStatus struct {
	api.Status `json:",inline"`
	// +optional
	Address duckv1.Addressable           `json:"address,omitempty"`
	Applied KogitoServerlessWorkflowSpec `json:"applied,omitempty"`
	// keeps track of how many failure recovers a given workflow had so far
	RecoverFailureAttempts int `json:"recoverFailureAttempts,omitempty"`
}

func (s *KogitoServerlessWorkflowStatus) GetTopLevelConditionType() api.ConditionType {
	return api.RunningConditionType
}

func (s *KogitoServerlessWorkflowStatus) IsReady() bool {
	return s.GetTopLevelCondition().IsTrue()
}

func (s *KogitoServerlessWorkflowStatus) GetTopLevelCondition() *api.Condition {
	return s.GetCondition(s.GetTopLevelConditionType())
}

func (s *KogitoServerlessWorkflowStatus) Manager() api.ConditionsManager {
	return api.NewConditionManager(s, api.RunningConditionType, api.BuiltConditionType)
}

func (s *KogitoServerlessWorkflowStatus) IsWaitingForPlatform() bool {
	cond := s.GetCondition(api.RunningConditionType)
	return cond.IsFalse() && cond.Reason == api.WaitingForPlatformReason
}

func (s *KogitoServerlessWorkflowStatus) IsWaitingForDeployment() bool {
	cond := s.GetCondition(api.RunningConditionType)
	return cond.IsFalse() && cond.Reason == api.WaitingForDeploymentReason
}

func (s *KogitoServerlessWorkflowStatus) IsWaitingForBuild() bool {
	cond := s.GetCondition(api.RunningConditionType)
	return cond.IsFalse() && cond.Reason == api.WaitingForBuildReason
}

func (s *KogitoServerlessWorkflowStatus) IsBuildRunning() bool {
	cond := s.GetCondition(api.BuiltConditionType)
	return cond.IsUnknown() || (cond.IsFalse() && cond.Reason == api.BuildIsRunningReason)
}

// KogitoServerlessWorkflow is the Schema for the kogitoserverlessworkflows API
// +kubebuilder:object:root=true
// +kubebuilder:object:generate=true
// +kubebuilder:subresource:status
// +kubebuilder:resource:shortName={"ksw", "workflow"}
// +k8s:openapi-gen=true
// +kubebuilder:printcolumn:name="Profile",type=string,JSONPath=`.metadata.annotations.sw\.kogito\.kie\.org\/profile`
// +kubebuilder:printcolumn:name="Version",type=string,JSONPath=`.metadata.annotations.sw\.kogito\.kie\.org\/version`
// +kubebuilder:printcolumn:name="Address",type=string,JSONPath=`.status.address.url`
// +kubebuilder:printcolumn:name="Ready",type=string,JSONPath=`.status.conditions[?(@.type=='Running')].status`
// +kubebuilder:printcolumn:name="Reason",type=string,JSONPath=`.status.conditions[?(@.type=='Running')].reason`
type KogitoServerlessWorkflow struct {
	metav1.TypeMeta   `json:",inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`

	Spec   KogitoServerlessWorkflowSpec   `json:"spec,omitempty"`
	Status KogitoServerlessWorkflowStatus `json:"status,omitempty"`
}

// KogitoServerlessWorkflowList contains a list of KogitoServerlessWorkflow
// +kubebuilder:object:root=true
// +k8s:deepcopy-gen:interfaces=k8s.io/apimachinery/pkg/runtime.Object
type KogitoServerlessWorkflowList struct {
	metav1.TypeMeta `json:",inline"`
	metav1.ListMeta `json:"metadata,omitempty"`
	Items           []KogitoServerlessWorkflow `json:"items"`
}

func init() {
	SchemeBuilder.Register(&KogitoServerlessWorkflow{}, &KogitoServerlessWorkflowList{})
}
