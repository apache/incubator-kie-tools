/*
Copyright 2022.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package v1alpha1

import (
	cloudevents "github.com/cloudevents/sdk-go/v2"
	v1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	duckv1 "knative.dev/pkg/apis/duck/v1"
	knservingv1 "knative.dev/serving/pkg/apis/serving/v1"
)

type KogitoServerlessConstant struct {
}

type KogitoServerlessTimeout struct {
}

type KogitoServerlessError struct {
}

type KogitoServerlessAuth struct {
}

type KogitoServerlessRetry struct {
}

type KogitoServerlessState struct {
}

// WorkflowSpec defines the desired state of Workflow
type WorkflowSpec struct {
	Constants   []KogitoServerlessConstant `json:"conditions"`
	Secrets     *[]v1.Secret               `json:"secrets"`
	Start       string                     `json:"start"`
	Timeouts    []KogitoServerlessTimeout  `json:"timeouts"`
	Errors      []KogitoServerlessError    `json:"errors"`
	KeepAlive   bool                       `json:"keepAlive"`
	Auth        KogitoServerlessAuth       `json:"auth"`
	Events      *[]cloudevents.Event       `json:"events"`
	Functions   knservingv1.Service        `json:"functions"`
	AutoRetries bool                       `json:"autoRetries"`
	Retries     KogitoServerlessRetry      `json:"retries"`
	States      KogitoServerlessState      `json:"states"`
}

type KogitoServerlessEndpoints struct {
}

// WorkflowStatus defines the observed state of Workflow
type WorkflowStatus struct {
	Conditions []metav1.Condition        `json:"conditions,omitempty"`
	Endpoints  KogitoServerlessEndpoints `json:"endpoints"`
	Address    duckv1.Addressable        `json:"address,omitempty"`
}

//+kubebuilder:object:root=true
//+kubebuilder:subresource:status

// Workflow is the Schema for the workflows API
type Workflow struct {
	metav1.TypeMeta   `json:",inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`

	Spec   WorkflowSpec   `json:"spec,omitempty"`
	Status WorkflowStatus `json:"status,omitempty"`
}

//+kubebuilder:object:root=true

// WorkflowList contains a list of Workflow
type WorkflowList struct {
	metav1.TypeMeta `json:",inline"`
	metav1.ListMeta `json:"metadata,omitempty"`
	Items           []Workflow `json:"items"`
}

func init() {
	SchemeBuilder.Register(&Workflow{}, &WorkflowList{})
}
